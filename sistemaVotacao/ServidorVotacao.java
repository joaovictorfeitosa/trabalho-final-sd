package sistemaVotacao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorVotacao {

    //Q1 MODELO
    private static final SistemaVotacao sistema = new SistemaVotacao();

    public static void main(String[] args) {
        int porta = 12345; 
        
        System.out.println("Iniciando Servidor de Votação (TCP, Multi-Threaded)...");
        
        
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Servidor pronto. Aguardando conexões na porta " + porta + "...");
            
            // Abre a votação
            sistema.setVotacaoAberta(true);
            System.out.println(">>> VOTAÇÃO ABERTA <<<");

            // Loop infinito
            while (true) {
                try {
                    // 2. Trava e espera um novo cliente se conectar
                    Socket clientSocket = serverSocket.accept(); 
                    System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());

                    // 3. Cria a "tarefa" para cuidar desse cliente
                    TratadorClienteVotacao tratador = new TratadorClienteVotacao(clientSocket, sistema);
                    
                    // 4. Cria a Thread, entrega a tarefa, e a inicia
                    Thread threadDoCliente = new Thread(tratador);
                    threadDoCliente.start(); // O .start() chama o método .run() do tratador
                
                } catch (IOException e) {
                    System.out.println("Erro ao aceitar conexão de cliente: " + e.getMessage());
                    // O loop continua, esperando o próximo cliente
                }
            }
        } catch (IOException e) {
            System.out.println("Erro crítico ao iniciar o servidor: " + e.getMessage());
        }
    }
}