package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

public class LocacaoService {

	private final LocacaoDao locacaoDao;
	private final SpcService spcService;
	private final EmailService emailService;

	public LocacaoService(LocacaoDao locacaoDao, SpcService spcService, EmailService emailService) {
		this.locacaoDao = locacaoDao;
		this.spcService = spcService;
		this.emailService = emailService;
	}

	public Locacao alugarFilmes(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException {
		if (usuario == null) {
			throw new LocadoraException("Usuario nao informado");
		}
		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme nao informado");
		}
		if (filmes.stream().anyMatch(filme -> filme.getEstoque() == 0)) {
			throw new FilmeSemEstoqueException();
		}

		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente mais tarde.");
		}

		if (negativado) {
			throw new LocadoraException("Usuario negativado junto ao SPC.");
		}

		AtomicInteger index = new AtomicInteger();
		Double valorTotal = filmes.stream().map(filme -> {
			double preco = switch (index.get()) {
				case 2 -> filme.getPrecoLocacao() * 0.75;
				case 3 -> filme.getPrecoLocacao() * 0.50;
				case 4 -> filme.getPrecoLocacao() * 0.25;
				case 5 -> 0d;
				default -> filme.getPrecoLocacao();
			};
			index.getAndIncrement();
			return preco;
		}).reduce(Double::sum).get();

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(valorTotal);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		this.locacaoDao.salvar(locacao);
		
		return locacao;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = this.locacaoDao.findLocacoesPendentes();
		locacoes.stream()
				.filter(locacao -> locacao.getDataRetorno().before(new Date()))
				.map(Locacao::getUsuario)
				.forEach(emailService::notificarAtraso);
	}

	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		locacaoDao.salvar(novaLocacao);
	}

}