package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static br.ce.wcaquino.servicos.builders.FilmeBuilder.umFilme;
import static org.mockito.Mockito.mock;

/*
 * Data Driven Test (DDT)
 */
@RunWith(Parameterized.class)
public class DDT_CalculoValorLocacaoTest {

    private LocacaoService service;

    // value 0 significa que é o primeiro indice do array do metodo getParameters
    // Variavel deve ser PUBLIC
    @Parameterized.Parameter(value = 0)
    public List<Filme> filmes;
    // value 2 significa que é o segundo indice do array do metodo getParameters
    // Variavel deve ser PUBLIC
    @Parameterized.Parameter(value = 1)
    public Double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String descricao;

    @Before
    public void setup() {
        // Podemos substituir os mocks pela anotacao @Mock
        LocacaoDao dao = mock(LocacaoDao.class);
        SpcService spcService = mock(SpcService.class);
        EmailService emailService = mock(EmailService.class);
        // Podemos substituir esta instanciacao pela anotacao @InjectMocks
        this.service = new LocacaoService(dao, spcService, emailService);
    }

    private static Filme filme1 = umFilme().get();
    private static Filme filme2 = umFilme().get();
    private static Filme filme3 = umFilme().get();
    private static Filme filme4 = umFilme().get();
    private static Filme filme5 = umFilme().get();
    private static Filme filme6 = umFilme().get();
    private static Filme filme7 = umFilme().get();

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParameters() {
        return List.of(
                new Object[][] {
                        { List.of(filme1, filme2), 8.0, "2 Filmes: Sem desconto" },
                        { List.of(filme1, filme2, filme3), 11.0, "3 Filmes: 25%" },
                        { List.of(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50%" },
                        { List.of(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75%" },
                        { List.of(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%" },
                        { List.of(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: Sem desconto" },
                }
        );
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = new Usuario("Usuaro 1");

        // acao
        Locacao locacao = this.service.alugarFilmes(usuario, this.filmes);

        // verificacao
        Assert.assertEquals(this.valorLocacao, locacao.getValor(), 0.01);
    }

    @Test
    public void print() {
        System.out.println(valorLocacao);
    }

}
