package sistemaLocadora;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServidorFilmes {

    public static void main(String[] args) {
        int porta = 12345;
        System.out.println("--- Servidor de Filmes (Questão 4) ---");
        System.out.println("Aguardando conexão na porta " + porta + "...");

        try (
            // 1. Cria o "ouvido" do servidor
            ServerSocket serverSocket = new ServerSocket(porta);
            
            // 2. Trava e espera um cliente se conectar
            Socket clientSocket = serverSocket.accept();
            
            // 3. Pega o "cano" de ENTRADA do cliente
            InputStream streamDoCliente = clientSocket.getInputStream();
            
            FilmeInputStream leitorFilmes = new FilmeInputStream(streamDoCliente)
        ) {
            
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
            System.out.println("Lendo dados do stream de bytes...");

            ArrayList<Filme> filmesRecebidos = new ArrayList<>();
            
             // Usa o leitor para desserializar (desempacotar) os dados
            int numFilmes = leitorFilmes.readNumFilmes();
            System.out.println("Recebendo " + numFilmes + " filme(s)...");

            for (int i = 0; i < numFilmes; i++) {
                Filme filmeRecebido = leitorFilmes.readFilme();
                filmesRecebidos.add(filmeRecebido);
            }

            System.out.println("\n--- Recebimento Concluído! ---");
            System.out.println("Filmes desserializados (desempacotados) do cliente:");
            for (Filme filme : filmesRecebidos) {
                System.out.println(filme.getDetalhes());
                System.out.println("--------------------");
            }

        } catch (Exception e) {
            System.out.println("Erro no Servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}