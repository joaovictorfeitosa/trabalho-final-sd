package sistemaLocadora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteLocadora {

    public static void main(String[] args) {
        String ipServidor = "localhost";
        int portaServidor = 12345;

        try (
            // 1. Conecta ao servidor
            Socket socket = new Socket(ipServidor, portaServidor);
            
            // 2. Configura streams de comunicação
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            
            Scanner scannerTeclado = new Scanner(System.in)
        ) {
            System.out.println("Conectado com sucesso à Locadora!");

            while (true) {
                //Exibe o Menu
                System.out.println("\n--- Menu Cliente Locadora ---");
                System.out.println("Digite um comando:");
                System.out.println("  LISTAR");
                System.out.println("  BUSCAR:<titulo>");
                System.out.println("  ALUGAR:<titulo>");
                System.out.println("  DEVOLVER:<titulo>");
                System.out.println("  SAIR");
                System.out.print("Comando: ");
                
                String comandoUsuario = scannerTeclado.nextLine();

                //  Envia o comando do usuário para o servidor
                out.println(comandoUsuario);

                //  Se SAIR, encerra o loop
                if (comandoUsuario.equalsIgnoreCase("SAIR")) {
                    break;
                }

                // Lê a resposta do servidor
                System.out.println("\n--- Resposta do Servidor ---");
                String respostaServidor;
                while ((respostaServidor = in.readLine()) != null) {
                    // Verifica o "marcador de fim"
                    if (respostaServidor.equals("FIM_RESPOSTA")) {
                        break;
                    }
                    System.out.println(respostaServidor);
                }
                System.out.println("----------------------------");
            }

            System.out.println("Desconectando...");

        } catch (IOException e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }
}