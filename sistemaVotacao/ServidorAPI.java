package sistemaVotacao;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ServidorAPI {

    private static final SistemaVotacao sistema = new SistemaVotacao();
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        int porta = 8080;
        // Cria um servidor HTTP leve 
        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        
        // Define o endpoint (URL) da API
        server.createContext("/api/votacao", new VotacaoHandler());
        
        server.setExecutor(null); // cria um executor padrão
        System.out.println("Servidor API REST rodando na porta " + porta);
        sistema.setVotacaoAberta(true);
        server.start();
    }

    // Classe que manipula as requisições HTTP
    static class VotacaoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Configura CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Ler o corpo da requisição (JSON)
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder jsonInput = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonInput.append(line);
                }

                System.out.println("API Recebeu: " + jsonInput.toString());

                // Processar a lógica
                String jsonResposta = processarComando(jsonInput.toString());

                // Enviar Resposta
                byte[] responseBytes = jsonResposta.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                
                String msg = "{\"status\":\"ERRO\", \"mensagem\":\"Use POST\"}";
                exchange.sendResponseHeaders(405, msg.length());
                OutputStream os = exchange.getResponseBody();
                os.write(msg.getBytes());
                os.close();
            }
        }
    }

    private static String processarComando(String jsonComando) {
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
                            resposta.put("mensagem", "Login realizado com sucesso");
                        } else {
                            resposta.put("status", "ERRO");
                            resposta.put("mensagem", "Credenciais invalidas");
                        }
                        break;

                    case "LISTAR":
                        List<Candidato> lista = sistema.getListaCandidatos();
                        resposta.put("status", "OK");
                        resposta.put("candidatos", lista);
                        break;

                    case "VOTAR":
                        Double numDouble = (Double) comando.get("numero");
                        String loginVotante = (String) comando.get("loginVotante");
                        String statusVoto = sistema.registrarVoto(loginVotante, numDouble.intValue());
                        if (statusVoto.startsWith("OK")) {
                            resposta.put("status", "OK");
                            resposta.put("mensagem", statusVoto);
                        } else {
                            resposta.put("status", "ERRO");
                            resposta.put("mensagem", statusVoto);
                        }
                        break;
                    
                    case "ADDCANDIDATO":
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
                        resposta.put("mensagem", "Comando desconhecido");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resposta.put("status", "ERRO");
            resposta.put("mensagem", "Erro no servidor: " + e.getMessage());
        }
        return gson.toJson(resposta);
    }
}