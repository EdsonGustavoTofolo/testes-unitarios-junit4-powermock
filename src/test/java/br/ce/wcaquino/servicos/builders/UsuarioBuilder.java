package br.ce.wcaquino.servicos.builders;

import br.ce.wcaquino.entidades.Usuario;

import java.util.List;

public class UsuarioBuilder {
    private Usuario usuario;

    private UsuarioBuilder() {
    }

    public static UsuarioBuilder umUsuario() {
        UsuarioBuilder usuarioBuilder = new UsuarioBuilder();
        usuarioBuilder.usuario = new Usuario();
        usuarioBuilder.usuario.setNome("Usuario 1");
        return usuarioBuilder;
    }

    public UsuarioBuilder comNome(String nome) {
        this.usuario.setNome(nome);
        return this;
    }

    public Usuario get() {
        return this.usuario;
    }
}
