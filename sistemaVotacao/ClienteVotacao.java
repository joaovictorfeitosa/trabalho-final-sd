package sistemaVotacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Imports para o UDP Multicast (Ouvinte)
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ClienteVotacao {

    private static Gson gson = new Gson();
    private static boolean ouvinteAtivo = false; // Flag para não iniciar 2x

    public static void main(String[] args) {
        String ipServidor = "localhost";
        int porta = 12345;
        System.out.println("--- Cliente de Votação (Questão 5) ---");

        try (
            Socket socket = new Socket(ipServidor, porta);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scannerTeclado = new Scanner(System.in)
        ) {
            
            System.out.println("Conectado ao Servidor de Votação!");

            while (true) {
                System.out.println("\n--- MENU DE VOTAÇÃO ---");
                System.out.println("1. Fazer Login");
                System.out.println("2. Listar Candidatos");
                System.out.println("3. Votar");
                System.out.println("4. Adicionar Candidato (Admin)");
                System.out.println("5. Enviar Nota Informativa (Admin)"); // <-- NOVO
                System.out.println("9. Sair");
                System.out.print("Escolha uma opção: ");
                
                String opcao = scannerTeclado.nextLine();
                String jsonComando = "";
                boolean deveSair = false;

                switch (opcao) {
                    case "1": // LOGIN
                        System.out.print("Digite o login: ");
                        String login = scannerTeclado.nextLine();
                        System.out.print("Digite a senha: ");
                        String senha = scannerTeclado.nextLine();
                        jsonComando = criarJsonLogin(login, senha);
                        break;
                    
                    case "2": // LISTAR
                        jsonComando = "{ \"comando\": \"LISTAR\" }";
                        break;

                    case "3": // VOTAR
                        System.out.print("Digite o número do candidato: ");
                        int numero = Integer.parseInt(scannerTeclado.nextLine());
                        jsonComando = criarJsonVotar(numero);
                        break;
                    
                    case "4": // ADICIONAR CANDIDATO
                        
                        System.out.print("Digite o número do novo candidato: ");
                        int numAdd = Integer.parseInt(scannerTeclado.nextLine());
                        System.out.print("Digite o nome do novo candidato: ");
                        String nomeAdd = scannerTeclado.nextLine();
                        jsonComando = criarJsonAddCandidato(numAdd, nomeAdd);
                        break;
                    
                    case "5":
                        System.out.print("Digite a nota informativa: ");
                        String nota = scannerTeclado.nextLine();
                        jsonComando = criarJsonEnviarNota(nota);
                        break;

                    case "9": // SAIR
                        jsonComando = "{ \"comando\": \"SAIR\" }";
                        deveSair = true;
                        break;
                    
                    default:
                        System.out.println("Opção inválida!");
                        continue;
                }

                System.out.println("\n[Enviando TCP: " + jsonComando + "]");
                out.println(jsonComando);

                String jsonResposta = in.readLine();
                System.out.println("[Recebido TCP: " + jsonResposta + "]");

                
                processarResposta(jsonResposta);

                if (deveSair) {
                    break; 
                }
            }
            System.out.println("Desconectando...");

        } catch (IOException e) {
            System.out.println("Erro no Cliente: " + e.getMessage());
        }
    }
    
    
    
    private static String criarJsonLogin(String l, String s) { Map<String,Object> c=new HashMap<>(); c.put("comando","LOGIN"); c.put("login",l); c.put("senha",s); return gson.toJson(c); }
    private static String criarJsonVotar(int n) { Map<String,Object> c=new HashMap<>(); c.put("comando","VOTAR"); c.put("numero",n); return gson.toJson(c); }
    private static String criarJsonAddCandidato(int n, String nm) { Map<String,Object> c=new HashMap<>(); c.put("comando","ADDCANDIDATO"); c.put("numero",n); c.put("nome",nm); return gson.toJson(c); }

    private static String criarJsonEnviarNota(String nota) { 
        Map<String, Object> comando = new HashMap<>();
        comando.put("comando", "ENVIARNOTA");
        comando.put("nota", nota);
        return gson.toJson(comando);
    }
    
    
    private static void processarResposta(String jsonResposta) {
        try {
            Map<String, Object> resposta = gson.fromJson(jsonResposta,
                new TypeToken<Map<String, Object>>(){}.getType());

            String status = (String) resposta.get("status");
            System.out.println("--- Resposta do Servidor ---");
            
            if ("OK".equals(status)) {
                
                if (resposta.containsKey("mensagem")) {
                    System.out.println(resposta.get("mensagem"));
                }
                
                //OUVINTE
                if (resposta.containsKey("tipoUsuario")) {
                    String tipo = (String) resposta.get("tipoUsuario");
                    System.out.println("Logado como: " + tipo);
                    
                    // Se for VOTANTE e o ouvinte ainda não estiver ativo...
                    if ("VOTANTE".equals(tipo) && !ouvinteAtivo) {
                        iniciarOuvinteMulticast();
                        ouvinteAtivo = true;
                    }
                }
                
                
                if (resposta.containsKey("candidatos")) {
                   
                    List<Map<String, Object>> candidatos = (List<Map<String, Object>>) resposta.get("candidatos");
                    System.out.println("Candidatos disponíveis:");
                    for (Map<String, Object> c : candidatos) {
                        String nome = (String) c.get("nome");
                        Double numDouble = (Double) c.get("numero");
                        int numero = numDouble.intValue();
                        System.out.println("  " + numero + " - " + nome);
                    }
                }
                
            } else {
                System.out.println("ERRO: " + resposta.get("mensagem"));
            }
            
        } catch (Exception e) {
            System.out.println("Erro ao processar resposta do servidor: " + e.getMessage());
        }
    }
    
    /**
     * NOVA FUNÇÃO (OUVINTE UDP)
     * Inicia uma nova Thread para este cliente,
     * que fica ouvindo o canal de multicast.
     */
    private static void iniciarOuvinteMulticast() {
        String GRUPO_IP = "239.0.0.1";
        int PORTA_MULTICAST = 5555;
        
        // Cria a tarefa (Runnable) para a nova thread
        Runnable tarefaOuvinte = () -> {
            
            try (MulticastSocket socket = new MulticastSocket(PORTA_MULTICAST)) {
                
                InetAddress grupo = InetAddress.getByName(GRUPO_IP);
                socket.joinGroup(grupo); // "Entra no canal"
                
                System.out.println("\n>>> (Ouvinte Multicast ATIVO: Aguardando notas do Admin...) <<<");
                
                byte[] buffer = new byte[1024];
                
                // Loop infinito de escuta
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    
                    socket.receive(packet); // Trava e espera uma mensagem
                    
                    String msgRecebida = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                    
                    // Imprime a mensagem recebida
                    System.out.println("\n\n>>> NOTA INFORMATIVA RECEBIDA (UDP) <<<");
                    System.out.println(msgRecebida);
                    System.out.print("\nEscolha uma opção: "); // Mostra o prompt de novo
                }
                
            } catch (IOException e) {
                System.out.println("Erro no ouvinte multicast: " + e.getMessage());
            }
        };
        
        // Inicia a Thread
        Thread threadOuvinte = new Thread(tarefaOuvinte);
        threadOuvinte.setDaemon(true); // Faz a thread fechar quando o programa principal fechar
        threadOuvinte.start();
    }
}