package br.ce.wcaquino.suites;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.DDT_CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * Suite de testes nao e muito indicado utilizar.
 * Pois necessita incluir as novas classes de testes toda vez aqui na lista de SuiteClasses.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalculadoraTest.class,
        DDT_CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuiteExecucao {

    // Útil quando precisa de alguma configuração antes de executar cada teste
    @BeforeClass
    public static void before() {
        System.out.println("Executando Suite de Testes @Before");
    }

    @AfterClass
    public static void after() {
        System.out.println("Executando Suite de Testes @After");
    }
}
