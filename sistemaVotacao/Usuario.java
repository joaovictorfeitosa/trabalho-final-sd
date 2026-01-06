package sistemaVotacao;

import java.io.Serializable;

public abstract class Usuario implements Serializable {
    protected String login;
    protected String senha;

    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() { return login; }
    public abstract String getTipo(); // "VOTANTE" ou "ADMINISTRADOR"

    public boolean checarSenha(String senha) {
        return this.senha.equals(senha);
    }
}