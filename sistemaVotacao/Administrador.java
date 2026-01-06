package sistemaVotacao;

public class Administrador extends Usuario {

    public Administrador(String login, String senha) {
        super(login, senha);
    }

    @Override
    public String getTipo() { return "ADMINISTRADOR"; }
}