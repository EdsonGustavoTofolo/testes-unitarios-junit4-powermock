package br.ce.wcaquino.servicos;

public class Calculadora {
    public int somar(int a, int b) {
        System.out.println("Esse texto sera impresso quando o Spy for chamado ");
        return a + b;
    }

    public int subtrair(int a, int b) {
        return a - b;
    }

    public int divide(int a, int b) {
        return a / b;
    }

    public void imprime() {
        System.out.println("Passei aqui");
    }
}
