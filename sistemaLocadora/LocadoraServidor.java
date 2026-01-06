package sistemaLocadora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LocadoraServidor {

    // O Servidor tem a locadora (o banco de dados)
    private static Locadora locadora = new Locadora("Locadora Distribuída");

    
     //Método para processar os comandos do protocolo

    private static String processarComando(String comando) {
        try {
            String[] partes = comando.split(":", 2); // Divide no primeiro ":"
            String acao = partes[0].toUpperCase();
            String titulo = (partes.length > 1) ? partes[1] : "";

            Midia midia;

            switch (acao) {
                case "LISTAR":
                    return locadora.getStringAcervo();

                case "BUSCAR":
                    midia = locadora.buscarPorTitulo(titulo);
                    if (midia != null) {
                        return midia.getDetalhes();
                    } else {
                        return "ERRO: Mídia não encontrada.";
                    }

                case "ALUGAR":
                    midia = locadora.buscarPorTitulo(titulo);
                    if (midia == null) {
                        return "ERRO: Mídia não encontrada.";
                    }
                    if (!midia.estaDisponivel()) {
                        return "ERRO: Mídia já está alugada.";
                    }
                    midia.alugar();
                    return "OK: " + midia.getTitulo() + " alugado com sucesso.";

                case "DEVOLVER":
                    midia = locadora.buscarPorTitulo(titulo);
                    if (midia == null) {
                        return "ERRO: Mídia não encontrada.";
                    }
                    if (midia.estaDisponivel()) {
                        return "INFO: Mídia já estava disponível.";
                    }
                    midia.devolver();
                    return "OK: " + midia.getTitulo() + " devolvido com sucesso.";

                case "SAIR":
                    return "OK: Desconectando.";

                default:
                    return "ERRO: Comando desconhecido. Use LISTAR, BUSCAR, ALUGAR, DEVOLVER, SAIR.";
            }
        } catch (Exception e) {
            return "ERRO: Falha ao processar comando: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        
        locadora.adicionarMidia(new Filme("Matrix", 1999, "Wachowskis", 136));
        locadora.adicionarMidia(new Filme("O Poderoso Chefão", 1972, "Coppola", 175));
        locadora.adicionarMidia(new Jogo("The Witcher 3", 2015, "PC"));
        locadora.adicionarMidia(new Jogo("Elden Ring", 2022, "PS5"));
        
        
        int porta = 12345; // Porta que o servidor vai escutar
        
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Servidor da Locadora iniciado. Aguardando clientes na porta " + porta + "...");
            
           
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Bloqueia até um cliente conectar
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                // Configura streams de comunicação com o cliente
                try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {
                    String comandoCliente;
                    // Loop de leitura de comandos do cliente
                    while ((comandoCliente = in.readLine()) != null) {
                        System.out.println("Cliente [" + clientSocket.getPort() + "] enviou: " + comandoCliente);
                        
                        // Processa o comando e envia a resposta
                        String resposta = processarComando(comandoCliente);
                        
                        // Envia a resposta linha por linha
                        for (String linha : resposta.split("\n")) {
                            out.println(linha);
                        }
                        
                        // Envia um "marcador de fim"
                        out.println("FIM_RESPOSTA");

                        if (comandoCliente.equalsIgnoreCase("SAIR")) {
                            break; // Sai do loop de leitura
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao comunicar com o cliente: " + e.getMessage());
                } finally {
                    clientSocket.close();
                    System.out.println("Cliente desconectado.");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}