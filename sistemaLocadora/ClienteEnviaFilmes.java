package sistemaLocadora;

import java.io.OutputStream;
import java.net.Socket;

public class ClienteEnviaFilmes {

    public static void main(String[] args) {
        String ipServidor = "localhost";
        int porta = 12345;
        System.out.println("--- Cliente de Filmes (Questão 4) ---");

        // 1. Cria os dados POJO (nosso array)
        Filme[] meusFilmes = {
            new Filme("Matrix", 1999, "Wachowskis", 136),
            new Filme("O Poderoso Chefão", 1972, "Coppola", 175),
            new Filme("Interestelar", 2014, "Nolan", 169) // Adicionando um novo
        };
        
        System.out.println("Conectando ao servidor em " + ipServidor + ":" + porta + "...");

        try (
            // 1. Conecta ao servidor
            Socket socket = new Socket(ipServidor, porta);
            
            // 2. Pega o "cano" de SAÍDA do servidor (para onde vão os bytes)
            OutputStream streamParaServidor = socket.getOutputStream()
        ) {
            
            System.out.println("Conectado! Serializando (empacotando) e enviando " + meusFilmes.length + " filmes...");
            
            // 3. "Enrola" o cano com nosso escritor customizado
            // O construtor do FilmeOutputStream JÁ FAZ o envio!
            new FilmeOutputStream(meusFilmes, meusFilmes.length, streamParaServidor);
            
            System.out.println("Envio concluído. Fechando cliente.");

        } catch (Exception e) {
            System.out.println("Erro no Cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}