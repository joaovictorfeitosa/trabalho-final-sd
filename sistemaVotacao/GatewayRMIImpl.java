package sistemaVotacao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GatewayRMIImpl extends UnicastRemoteObject implements IGatewayRMI {

    private SistemaVotacao sistema;
    private Gson gson = new Gson();
    
    protected GatewayRMIImpl(SistemaVotacao sistema) throws RemoteException {
        super();
        this.sistema = sistema;
    }

    @Override
    public byte[] enviarMensagem(byte[] mensagemRequest) throws RemoteException {
        try {
            // 1. Desempacota: Bytes -> String JSON
            String jsonCliente = new String(mensagemRequest, StandardCharsets.UTF_8);
            System.out.println("RMI Recebido: " + jsonCliente);

            // 2. Processa o JSON (Lógica reaproveitada do Tratador antigo)
            String jsonResposta = processarComandoJson(jsonCliente);

            // 3. Empacota: String JSON -> Bytes
            return jsonResposta.getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"ERRO\", \"mensagem\":\"Erro interno no servidor\"}".getBytes();
        }
    }

  
    private String processarComandoJson(String jsonComando) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            Map<String, Object> comando = gson.fromJson(jsonComando, new TypeToken<Map<String, Object>>(){}.getType());
            String acao = (String) comando.get("comando");

          
            
            synchronized (sistema) {
                switch (acao.toUpperCase()) {
                    case "LOGIN":
                        String login = (String) comando.get("login");
                        String senha = (String) comando.get("senha");
                        Usuario user = sistema.autenticarUsuario(login, senha);
                        if (user != null) {
                            resposta.put("status", "OK");
                            resposta.put("tipoUsuario", user.getTipo());
                        } else {
                            resposta.put("status", "ERRO");
                            resposta.put("mensagem", "Login inválido.");
                        }
                        break;

                    case "LISTAR":
                        List<Candidato> lista = sistema.getListaCandidatos();
                        resposta.put("status", "OK");
                        resposta.put("candidatos", lista);
                        break;

                    case "VOTAR":
                       
                        
                        // Convertendo numero
                        Double numDouble = (Double) comando.get("numero");
                        int numeroVoto = numDouble.intValue();
                        
                        
                        // Tenta pegar login se vier, senão usa um placeholder ou lógica do cliente
                        String loginVotante = (String) comando.get("loginVotante");
                        if(loginVotante == null) loginVotante = "anonimo"; // Ajuste conforme necessidade

                        
                        
                        String statusVoto = sistema.registrarVoto(loginVotante, numeroVoto);
                        
                        if (statusVoto.startsWith("OK")) {
                            resposta.put("status", "OK");
                            resposta.put("mensagem", statusVoto);
                        } else {
                            resposta.put("status", "ERRO");
                            resposta.put("mensagem", statusVoto);
                        }
                        break;
                        
                    case "ADDCANDIDATO":
                        // Requer Admin
                        String loginAdmin = (String) comando.get("loginAdmin");
                        Double numAdd = (Double) comando.get("numero");
                        String nomeAdd = (String) comando.get("nome");
                        
                        String resAdd = sistema.adicionarCandidato(loginAdmin, numAdd.intValue(), nomeAdd);
                        if (resAdd.startsWith("OK")) {
                             resposta.put("status", "OK");
                             resposta.put("mensagem", resAdd);
                        } else {
                             resposta.put("status", "ERRO");
                             resposta.put("mensagem", resAdd);
                        }
                        break;

                    default:
                        resposta.put("status", "ERRO");
                        resposta.put("mensagem", "Comando desconhecido.");
                }
            }
        } catch (Exception e) {
            resposta.put("status", "ERRO");
            resposta.put("mensagem", "Erro JSON: " + e.getMessage());
        }
        return gson.toJson(resposta);
    }
}