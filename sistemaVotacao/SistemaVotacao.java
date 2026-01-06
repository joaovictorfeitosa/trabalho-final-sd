package sistemaVotacao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SistemaVotacao {

    // Bancos de Dados
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Map<Integer, Candidato> candidatos = new HashMap<>();
    private boolean votacaoAberta = false;

    public SistemaVotacao() {
        
        usuarios.put("admin", new Administrador("admin", "admin123"));
        usuarios.put("joao", new Votante("joao", "joao123"));
        usuarios.put("ana", new Votante("ana", "ana123"));
        candidatos.put(13, new Candidato(13, "Lula"));
        candidatos.put(17, new Candidato(17, "Bolsonaro"));
    }

    // Métodos de Serviço

    
    public synchronized Usuario autenticarUsuario(String login, String senha) {
        Usuario user = usuarios.get(login);
        
        if (user != null && user.checarSenha(senha)) {
            return user;
        }
        return null;
    }

    
    public synchronized List<Candidato> getListaCandidatos() {
        return this.candidatos.values().stream().collect(Collectors.toList());
    }

    //
    public synchronized String registrarVoto(String loginVotante, int numeroCandidato) {
        if (!votacaoAberta) return "ERRO: Votação está fechada.";
        
        Votante votante = (Votante) usuarios.get(loginVotante);
        if (votante == null) return "ERRO: Votante não encontrado."; 
        if (votante.isJaVotou()) return "ERRO: Votante já votou.";
        
        Candidato candidato = candidatos.get(numeroCandidato);
        if (candidato == null) return "ERRO: Candidato inexistente."; 
        
        candidato.adicionarVoto();
        votante.setJaVotou(true);
        return "OK: Voto para " + candidato.getNome() + " registrado.";
    }

    
    public synchronized String adicionarCandidato(String loginAdmin, int numero, String nome) {
        Usuario user = usuarios.get(loginAdmin);
       
        if (user == null || !(user instanceof Administrador)) return "ERRO: Permissão negada.";
        if (candidatos.containsKey(numero)) return "ERRO: Número de candidato já existe.";
        
        candidatos.put(numero, new Candidato(numero, nome));
        return "OK: Candidato " + nome + " adicionado.";
    }
    
    
    public synchronized String removerCandidato(String loginAdmin, int numero) {
         Usuario user = usuarios.get(loginAdmin);
        
        if (user == null || !(user instanceof Administrador)) return "ERRO: Permissão negada.";
        if (!candidatos.containsKey(numero)) return "ERRO: Candidato não existe.";

        candidatos.remove(numero);
        return "OK: Candidato removido.";
    }
    
    public void setVotacaoAberta(boolean aberta) {
        this.votacaoAberta = aberta;
    }
}