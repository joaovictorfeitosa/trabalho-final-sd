package sistemaVotacao;

public class Votante extends Usuario {
    
    private boolean jaVotou;

    public Votante(String login, String senha) {
        super(login, senha);
        this.jaVotou = false;
    }

    public boolean isJaVotou() { return jaVotou; }
    public void setJaVotou(boolean jaVotou) { this.jaVotou = jaVotou; }

    @Override
    public String getTipo() { return "VOTANTE"; }
}