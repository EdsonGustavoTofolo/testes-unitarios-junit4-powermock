package br.ce.wcaquino.exceptions;

public class FilmeSemEstoqueException extends Exception {
    public FilmeSemEstoqueException() {
        super("Filme sem estoque");
    }
}
