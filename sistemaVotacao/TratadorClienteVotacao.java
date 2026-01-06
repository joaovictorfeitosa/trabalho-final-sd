package sistemaVotacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Imports para o UDP Multicast (Remetente)
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TratadorClienteVotacao implements Runnable {

    
    private Socket clienteSocket;
    private SistemaVotacao sistema;
    private PrintWriter out;
    private BufferedReader in;
    private Usuario usuarioLogado = null;
    private Gson gson = new Gson();

    public TratadorClienteVotacao(Socket socket, SistemaVotacao sistema) {
        this.clienteSocket = socket;
        this.sistema = sistema;
    }

    @Override
    public void run() {
        try {
            this.out = new PrintWriter(clienteSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            
            String jsonCliente;
            while ((jsonCliente = in.readLine()) != null) {
                System.out.println("Cliente [" + clienteSocket.getPort() + "] enviou JSON: " + jsonCliente);
                String jsonResposta = processarComandoJson(jsonCliente);
                out.println(jsonResposta);
                
                if (jsonCliente.contains("\"comando\":\"SAIR\"")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro na Thread do cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {}
            System.out.println("Cliente [" + clienteSocket.getPort() + "] desconectado.");
        }
    }
    

    
    //Processa um comando que chega como uma String JSON.
    //Retorna uma String JSON como resposta.
     
    private String processarComandoJson(String jsonComando) {
        Map<String, Object> resposta = new HashMap<>();
        try {
            Map<String, Object> comando = gson.fromJson(jsonComando, 
                new TypeToken<Map<String, Object>>(){}.getType());
            
            String acao = (String) comando.get("comando");
            if (acao == null) {
                throw new Exception("Comando JSON inválido, 'comando' não encontrado.");
            }

           
            synchronized (sistema) {
                switch (acao.toUpperCase()) {
                    
                    case "LOGIN":
                        
                        String login = (String) comando.get("login");
                        String senha = (String) comando.get("senha");
                        this.usuarioLogado = sistema.autenticarUsuario(login, senha);
                        if (this.usuarioLogado != null) {
                            resposta.put("status", "OK");
                            resposta.put("tipoUsuario", this.usuarioLogado.getTipo());
                        } else {
                            resposta.put("status", "ERRO");
                            resposta.put("mensagem", "Login ou senha inválidos.");
                        }
                        break;
                    
                    case "LISTAR":
                        
                        List<Candidato> lista = sistema.getListaCandidatos();
                        resposta.put("status", "OK");
                        resposta.put("candidatos", lista);
                        break;

                    case "VOTAR":
                       
                        if (this.usuarioLogado == null) throw new Exception("Faça login primeiro.");
                        if (!(this.usuarioLogado instanceof Votante)) throw new Exception("Administradores não votam.");
                        Double numDouble = (Double) comando.get("numero"); 
                        int numeroVoto = numDouble.intValue();
                        String statusVoto = sistema.registrarVoto(this.usuarioLogado.getLogin(), numeroVoto);
                        if (statusVoto.startsWith("OK")) {
                            resposta.put("status", "OK");
                            resposta.put("mensagem", statusVoto);
                        } else {
                            resposta.put("status", "ERRO");
                            resposta.put("mensagem", statusVoto);
                        }
                        break;

                   

                    
                    case "ENVIARNOTA":
                        if (this.usuarioLogado == null) throw new Exception("Faça login primeiro.");
                        if (!(this.usuarioLogado instanceof Administrador)) throw new Exception("Permissão negada (apenas Admins).");

                        String notaInformativa = (String) comando.get("nota");
                        
                        
                        enviarNotaMulticast(notaInformativa);

                        resposta.put("status", "OK");
                        resposta.put("mensagem", "Nota informativa enviada para todos os votantes.");
                        break;
                    

                    case "SAIR":
                        resposta.put("status", "OK");
                        resposta.put("mensagem", "Desconectando.");
                        break;

                    default:
                        resposta.put("status", "ERRO");
                        resposta.put("mensagem", "Comando desconhecido.");
                }
            }
        } catch (Exception e) {
            resposta.put("status", "ERRO");
            resposta.put("mensagem", "Erro ao processar comando: " + e.getMessage());
        }
        
        return gson.toJson(resposta);
    }
    
    /*
      Cria um socket UDP "descartável" para enviar uma mensagem
      para o grupo de multicast.
     */
    private void enviarNotaMulticast(String mensagem) throws IOException {
        String GRUPO_IP = "239.0.0.1";
        int PORTA_MULTICAST = 5555;
        
        // Cria um socket UDP "normal" (remetente)
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress grupo = InetAddress.getByName(GRUPO_IP);
            
            String msgFormatada = "[AVISO DO ADMIN]: " + mensagem;
            byte[] buffer = msgFormatada.getBytes("UTF-8"); 

            // Cria o pacote UDP
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, grupo, PORTA_MULTICAST);
            
            // Envia (fire-and-forget)
            socket.send(packet);
            System.out.println("Servidor enviou nota multicast: " + msgFormatada);
        }
    }
}