package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public interface SpcService {
    boolean possuiNegativacao(Usuario usuario) throws Exception;
}
