// Crie este arquivo em: sistemaLocadora/TesteQuestao2.java
package sistemaLocadora;

import java.io.FileOutputStream; // Para o teste de arquivo 
import java.io.OutputStream;

public class TesteQuestao2 {

    public static void main(String[] args) {
        System.out.println("--- Iniciando Teste da Questão 2 ---");

        // 1. Cria os dados POJO (nosso array)
        Filme[] meusFilmes = {
            new Filme("Matrix", 1999, "Wachowskis", 136),
            new Filme("O Poderoso Chefão", 1972, "Coppola", 175)
        };

        // --- Teste (i): Saída Padrão (System.out)  ---
        System.out.println("\n--- Testando com System.out ---");
        try {
            // Passa System.out como o "destino"
            new FilmeOutputStream(meusFilmes, meusFilmes.length, System.out);
            System.out.println("\n(Bytes estranhos acima, é a saída pura!)");
        } catch (Exception e) {
            System.out.println("Erro no teste com System.out: " + e.getMessage());
        }

        // --- Teste (ii): Arquivo (FileOutputStream)  ---
        System.out.println("\n--- Testando com FileOutputStream ---");
        String nomeArquivo = "filmes.dat"; // .dat = dados binários
        
        // Usamos try-with-resources para garantir que o arquivo seja fechado
        try (OutputStream fileDestino = new FileOutputStream(nomeArquivo)) {
            
            // Passa o arquivo como o "destino"
            new FilmeOutputStream(meusFilmes, meusFilmes.length, fileDestino);
            
            System.out.println("Sucesso! Verifique o arquivo '" + nomeArquivo + "' criado na pasta.");
            
        } catch (Exception e) {
            System.out.println("Erro no teste com Arquivo: " + e.getMessage());
        }
    }
}
