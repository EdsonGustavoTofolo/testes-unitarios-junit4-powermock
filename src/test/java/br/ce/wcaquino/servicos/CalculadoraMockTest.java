package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.*;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;
    @Spy
    private Calculadora calcSpy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        when(calcMock.somar(1, 2)).thenReturn(5);
        when(calcSpy.somar(1, 2)).thenReturn(5);

        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("Spy: " + calcSpy.somar(1, 2));

        System.out.println("Mock Imprime: ");
        calcMock.imprime();
        System.out.println("Spy Imprime: ");
        calcSpy.imprime();

        when(calcMock.somar(1, 2)).thenCallRealMethod();
        System.out.println("Mock real implementation: " + calcMock.somar(1, 2));

        doNothing().when(calcSpy).imprime();
        System.out.println("Mock Imprime: ");
        calcMock.imprime();
        System.out.println("Spy Imprime: ");
        calcSpy.imprime();

        doReturn(5).when(calcSpy).somar(1, 2);
        System.out.println("Spy sem imprimir texto do somar: " + calcSpy.somar(1, 2));
    }

    @Test
    public void testeComArgumentCapture() {
        Calculadora calculadora = mock(Calculadora.class);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);

        when(calculadora.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(5);

        Assert.assertEquals(5, calculadora.somar(22, 33));
        System.out.println(argumentCaptor.getAllValues());
    }
}
