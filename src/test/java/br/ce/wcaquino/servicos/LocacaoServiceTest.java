package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static br.ce.wcaquino.servicos.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.servicos.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.servicos.builders.LocacaoBuilder.umaLocacao;
import static br.ce.wcaquino.servicos.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.servicos.matchers.OwnMatchers.*;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();
    @Rule
    private ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private LocacaoService service;
    @Mock
    private SpcService spcService;
    @Mock
    private EmailService emailService;
    @Mock
    private LocacaoDao dao;

//    public static int contador;

//    @BeforeClass
//    public static void setupClass() {
//        contador = 0;
//    }

//    @AfterClass
//    public static void tearDownClass() {
//        System.out.println("Foram realizados " + contador + " testes");
//    }

    @Before
    public void setup() {
        // MockitoAnnotations.initMocks(this);

//        dao = mock(LocacaoDao.class);
//        spcService = mock(SpcService.class);
//        emailService = mock(EmailService.class);
//        this.service = new LocacaoService(dao, spcService, emailService);

//        contador++;
    }

//    @After
//    public void tearDown() {}

    @Test
    public void deveFazerLocarComSucesso() throws Exception {
        // Assume que só deve executar quando o new Date() nao for Sabado
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = umUsuario().get();
        Filme filme1 = umFilme().comValor(5.0).get();

        //acao
        Locacao locacao = service.alugarFilmes(usuario, Arrays.asList(filme1));

        // verificacao via @Rule ErrorCollector
        this.error.checkThat(locacao.getValor(), Is.is(5.0));
        this.error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), Is.is(true));
        this.error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), Is.is(true));

        //verificacao via Assert
        Assert.assertEquals(5.0, locacao.getValor(), 0.01);

        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
        MatcherAssert.assertThat(locacao.getDataLocacao(), ehHoje());

        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)));
        MatcherAssert.assertThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoAoAlugarFilmeSemEstoque_tratamentoAnotacaoExpectedException() throws Exception {
        //cenario
        Usuario usuario = umUsuario().get();
        Filme filme1 = umFilme().get();
        Filme filme2 = umFilmeSemEstoque().get();

        //acao
        service.alugarFilmes(usuario, Arrays.asList(filme1, filme2));
    }

    @Test
    public void deveLancarExcecaoAoAlugarFilmeSemEstoque_tratamentoTryCatchComAssertFail() {
        //cenario
        Usuario usuario = umUsuario().get();
        Filme filme1 = umFilmeSemEstoque().get();

        //acao
        try {
            service.alugarFilmes(usuario, Collections.singletonList(filme1));
            Assert.fail("Deveria ter lancado excecao!");
        } catch (Exception e) {
            Assert.assertEquals("Filme sem estoque", e.getMessage());
        }
    }

    @Test
    public void deveLancarExcecaoAoAlugarFilmeSemEstoque_tratamentoExpectedException() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = umUsuario().get();
        Filme filme1 = umFilmeSemEstoque().get();

        expectedException.expect(FilmeSemEstoqueException.class);
        expectedException.expectMessage("Filme sem estoque");

        //acao
        service.alugarFilmes(usuario, Arrays.asList(filme1));
    }

    @Test
    public void deveLancarExcecaoAoAlugarFilmeSemUsuario_tratamentoTryCatchComAssertFail() throws FilmeSemEstoqueException {
        // cenario
        Filme filme1 = umFilme().get();

        // acao
        try {
            service.alugarFilmes(null, Collections.singletonList(filme1));
            Assert.fail("Deveria lancar excecao de usuario nao informado");
        } catch (LocadoraException e) {
            Assert.assertEquals("Usuario nao informado", e.getMessage());
        }
    }

    @Test
    public void deveLancarExcecaoAoAlugarFilmeNaoInformado_tratamentoExpectedException() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = umUsuario().get();

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme nao informado");

        // acao
        service.alugarFilmes(usuario, null);
    }

    @Test
    @Ignore // Ignorado pois o teste foi movido para a classe DDT_CalculoValorLocacaoTest
    public void devePagar75percentoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = umUsuario().get();

        Filme filme1 = umFilme().get();
        Filme filme2 = umFilme().get();
        Filme filme3 = umFilme().get();

        // acao
        Locacao locacao = this.service.alugarFilmes(usuario, Arrays.asList(filme1, filme2, filme3));

        // verificacao
        Assert.assertEquals(11.0, locacao.getValor(), 0.01);
    }

    @Test
    @Ignore // Ignorado pois o teste foi movido para a classe DDT_CalculoValorLocacaoTest
    public void devePagar50percentoNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = umUsuario().get();

        Filme filme1 = umFilme().get();
        Filme filme2 = umFilme().get();
        Filme filme3 = umFilme().get();
        Filme filme4 = umFilme().get();

        // acao
        Locacao locacao = this.service.alugarFilmes(usuario, Arrays.asList(filme1, filme2, filme3, filme4));

        // verificacao
        Assert.assertEquals(13.0, locacao.getValor(), 0.01);
    }

    @Test
    @Ignore // Ignorado pois o teste foi movido para a classe DDT_CalculoValorLocacaoTest
    public void devePagar25percentoNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = umUsuario().get();

        Filme filme1 = umFilme().get();
        Filme filme2 = umFilme().get();
        Filme filme3 = umFilme().get();
        Filme filme4 = umFilme().get();
        Filme filme5 = umFilme().get();

        // acao
        Locacao locacao = this.service.alugarFilmes(usuario, Arrays.asList(filme1, filme2, filme3, filme4, filme5));

        // verificacao
        Assert.assertEquals(14.0, locacao.getValor(), 0.01);
    }

    @Test
    @Ignore // Ignorado pois o teste foi movido para a classe DDT_CalculoValorLocacaoTest
    public void devePagarZeropercentoNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = umUsuario().get();

        Filme filme1 = umFilme().get();
        Filme filme2 = umFilme().get();
        Filme filme3 = umFilme().get();
        Filme filme4 = umFilme().get();
        Filme filme5 = umFilme().get();
        Filme filme6 = umFilme().get();

        // acao
        Locacao locacao = this.service.alugarFilmes(usuario, Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6));

        // verificacao
        Assert.assertEquals(14.0, locacao.getValor(), 0.01);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        // Sem PowerMock: Assume que só deve executar quando o Date() for Sabádo
//        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        // Com PowerMock:
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));

        // cenario
        Usuario usuario = umUsuario().get();

        Filme filme1 = umFilme().get();

        // acao
        Locacao locacao = service.alugarFilmes(usuario, Arrays.asList(filme1));

        // verificacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);

        Assert.assertTrue(ehSegunda);

        // Verificacao com Matchers
        MatcherAssert.assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
        MatcherAssert.assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
        MatcherAssert.assertThat(locacao.getDataRetorno(), caiNumaSegundaFeira());
    }

    @Test
    public void deveLancarExcecaoAoAlugarFilmesParaUsuarioNegativado() throws Exception {
        // cenario
        Usuario usuario = umUsuario().get();
        List<Filme> filmes = Arrays.asList(umFilme().get());

        // cenario - mock
        Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);

        // acao
        try {
            service.alugarFilmes(usuario, filmes);
            Assert.fail("Deveria ter lancado excecao");
        } catch (Exception e) {
            Assert.assertEquals(LocadoraException.class, e.getClass());
            Assert.assertEquals("Usuario negativado junto ao SPC.", e.getMessage());
        }
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        // cenario
        Usuario usuario1 = umUsuario().get();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").get();
        Usuario usuario3 = umUsuario().comNome("Usuario atrasado 2").get();

        Locacao locacao1 = umaLocacao().comUsuario(usuario1).atrasada().get();
        Locacao locacao2 = umaLocacao().comUsuario(usuario2).get();
        Locacao locacao3 = umaLocacao().comUsuario(usuario3).atrasada().get();
        Locacao locacao4 = umaLocacao().comUsuario(usuario3).atrasada().get();

        List<Locacao> locacoes = Arrays.asList(locacao1, locacao2, locacao3, locacao4);

        // cenario - mock
        Mockito.when(dao.findLocacoesPendentes()).thenReturn(locacoes);

        // acao
        service.notificarAtrasos();

        // verificacao
        Mockito.verify(emailService, times(3)).notificarAtraso(any(Usuario.class));
        Mockito.verify(emailService).notificarAtraso(usuario1);
        Mockito.verify(emailService, never()).notificarAtraso(usuario2);
        Mockito.verify(emailService, times(2)).notificarAtraso(usuario3);
        Mockito.verifyNoMoreInteractions(emailService);
//        verifyZeroInteractions(spcService);
    }

    @Test
    public void deveTratarErroNoSpc() throws Exception {
        // cenario
        Usuario usuario = umUsuario().get();
        List<Filme> filmes = Arrays.asList(umFilme().get());

        Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));

        // acao
        try {
            service.alugarFilmes(usuario, filmes);
            Assert.fail("Deveria ter lancado excecao");
        } catch (Exception e) {
            Assert.assertEquals(LocadoraException.class, e.getClass());
            Assert.assertEquals("Problemas com SPC, tente novamente mais tarde.", e.getMessage());
        }
    }

    @Test
    public void deveProrrogarUmLocacao() {
        // cenario
        Locacao locacao = umaLocacao().get();
        int diasProrrogacao = 3;

        // acao
        service.prorrogarLocacao(locacao, diasProrrogacao);

        // verificacao
        ArgumentCaptor<Locacao> argument = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argument.capture());
        Locacao locacaoSalva = argument.getValue();

        Assert.assertEquals(5.0 * diasProrrogacao, locacaoSalva.getValor(), 0.01);
        MatcherAssert.assertThat(locacaoSalva.getDataLocacao(), ehHoje());
        MatcherAssert.assertThat(locacaoSalva.getDataRetorno(), ehHojeComDiferencaDias(diasProrrogacao));
    }
}
