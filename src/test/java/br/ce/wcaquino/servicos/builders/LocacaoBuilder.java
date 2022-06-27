package br.ce.wcaquino.servicos.builders;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocacaoBuilder {
    private Locacao locacao;

    private LocacaoBuilder() {}

    public static LocacaoBuilder umaLocacao() {
        LocacaoBuilder builder = new LocacaoBuilder();
        builder.locacao = new Locacao();
        builder.locacao.setDataLocacao(new Date());
        builder.locacao.setUsuario(UsuarioBuilder.umUsuario().get());
        builder.locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
        builder.locacao.setFilmes(new ArrayList<>());
        builder.locacao.setValor(5.0);
        return builder;
    }

    public LocacaoBuilder comUsuario(Usuario usuario) {
        this.locacao.setUsuario(usuario);
        return this;
    }

    public LocacaoBuilder comFilmes(Filme...filmes) {
        this.locacao.setFilmes(List.of(filmes));
        return this;
    }

    public LocacaoBuilder atrasada() {
        this.locacao.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
        this.locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
        return this;
    }

    public Locacao get() {
        return this.locacao;
    }
}
