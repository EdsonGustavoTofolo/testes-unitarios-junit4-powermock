package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;

/*
 * Chain Method
 * Fluence Interface
 */
public class FilmeBuilder {
    private Filme filme;

    private FilmeBuilder() {

    }

    public static FilmeBuilder umFilme() {
        FilmeBuilder builder = new FilmeBuilder();
        builder.filme = new Filme();
        builder.filme.setEstoque(1);
        builder.filme.setPrecoLocacao(4.0);
        builder.filme.setNome("Filme 1");
        return builder;
    }

    public static FilmeBuilder umFilmeSemEstoque() {
        return umFilme().semEstoque();
    }

    public FilmeBuilder semEstoque() {
        filme.setEstoque(0);
        return this;
    }

    public FilmeBuilder comValor(Double valor) {
        filme.setPrecoLocacao(valor);
        return this;
    }

    public Filme get() {
        return this.filme;
    }
}
