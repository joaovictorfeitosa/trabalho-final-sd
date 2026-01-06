package sistemaVotacao;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ClienteRMI {

    private static Gson gson = new Gson();
    // Guarda o login localmente para enviar nas requisições
    private static String meuLogin = null; 
    private static String meuTipo = null;

    public static void main(String[] args) {
        System.out.println("--- Cliente de Votação via RMI ---");

        try {
            // Busca o serviço remoto (Lookup)
            IGatewayRMI gateway = (IGatewayRMI) Naming.lookup("rmi://localhost:1099/VotacaoService");
            System.out.println("Conectado ao Gateway RMI!");

            Scanner scannerTeclado = new Scanner(System.in);

            while (true) {
                System.out.println("\n--- MENU RMI ---");
                System.out.println("1. Fazer Login");
                System.out.println("2. Listar Candidatos");
                System.out.println("3. Votar");
                System.out.println("4. Adicionar Candidato (Admin)");
                System.out.println("9. Sair");
                System.out.print("Escolha: ");
                String opcao = scannerTeclado.nextLine();

                Map<String, Object> comandoMap = new HashMap<>();
                boolean deveSair = false;

                switch (opcao) {
                    case "1": // LOGIN
                        System.out.print("Login: ");
                        String l = scannerTeclado.nextLine();
                        System.out.print("Senha: ");
                        String s = scannerTeclado.nextLine();
                        comandoMap.put("comando", "LOGIN");
                        comandoMap.put("login", l);
                        comandoMap.put("senha", s);
                       
                        meuLogin = l; 
                        break;

                    case "2": // LISTAR
                        comandoMap.put("comando", "LISTAR");
                        break;

                    case "3": // VOTAR
                        if (meuLogin == null) {
                            System.out.println("ERRO: Faça login primeiro!");
                            continue;
                        }
                        System.out.print("Número do candidato: ");
                        int n = Integer.parseInt(scannerTeclado.nextLine());
                        comandoMap.put("comando", "VOTAR");
                        comandoMap.put("numero", n);
                        comandoMap.put("loginVotante", meuLogin); // Envia quem é
                        break;

                    case "4": // ADD (Admin)
                        if (meuLogin == null) {
                             System.out.println("ERRO: Faça login primeiro!");
                             continue;
                        }
                        System.out.print("Número: ");
                        int nAdd = Integer.parseInt(scannerTeclado.nextLine());
                        System.out.print("Nome: ");
                        String nomeAdd = scannerTeclado.nextLine();
                        comandoMap.put("comando", "ADDCANDIDATO");
                        comandoMap.put("numero", nAdd);
                        comandoMap.put("nome", nomeAdd);
                        comandoMap.put("loginAdmin", meuLogin); // Envia quem é
                        break;

                    case "9":
                        deveSair = true;
                        break;
                        
                    default: continue;
                }

                if (deveSair) break;

             
                // Transforma Map -> JSON -> Bytes
                byte[] requestBytes = gson.toJson(comandoMap).getBytes(StandardCharsets.UTF_8);
                
                //  Chama o doOperation (que chama o RMI)
                byte[] responseBytes = doOperation(gateway, requestBytes);
                
                // Transforma Bytes -> JSON -> Map
                String jsonResposta = new String(responseBytes, StandardCharsets.UTF_8);
                processarResposta(jsonResposta);
            }

        } catch (Exception e) {
            System.out.println("Erro no Cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    public static byte[] doOperation(IGatewayRMI gateway, byte[] arguments) {
        try {
            // Chama o método remoto real
            return gateway.enviarMensagem(arguments);
        } catch (Exception e) {
            System.out.println("Erro na transmissão RMI: " + e.getMessage());
            return new byte[0];
        }
    }

    private static void processarResposta(String jsonResposta) {
        try {
            Map<String, Object> resposta = gson.fromJson(jsonResposta, new TypeToken<Map<String, Object>>(){}.getType());
            String status = (String) resposta.get("status");
            
            if ("OK".equals(status)) {
                if (resposta.containsKey("mensagem")) System.out.println("Servidor: " + resposta.get("mensagem"));
                if (resposta.containsKey("tipoUsuario")) {
                    meuTipo = (String) resposta.get("tipoUsuario");
                    System.out.println("Logado como: " + meuTipo);
                }
                if (resposta.containsKey("candidatos")) {
                    List<Map<String, Object>> cand = (List<Map<String, Object>>) resposta.get("candidatos");
                    System.out.println("--- Candidatos ---");
                    for (Map<String, Object> c : cand) {
                         System.out.println(((Double)c.get("numero")).intValue() + " - " + c.get("nome") + " (" + ((Double)c.get("votos")).intValue() + " votos)");
                    }
                }
            } else {
                System.out.println("ERRO: " + resposta.get("mensagem"));
                // Se deu erro no login, limpa o login local
                if(jsonResposta.contains("Login inválido")) meuLogin = null;
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar resposta: " + e.getMessage());
        }
    }
}