package sistemaLocadora;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class TesteQuestao3 {

    public static void main(String[] args) {
        System.out.println("--- Iniciando Teste da Questão 3 ---");
        String nomeArquivo = "filmes.dat"; 

        ArrayList<Filme> filmesLidos = new ArrayList<>();

        // --- Teste (ii): Arquivo (FileInputStream)  ---
        System.out.println("\n--- Testando com FileInputStream ---");
        
        // Usamos try-with-resources para garantir que o arquivo seja fechado
        try (InputStream fileOrigem = new FileInputStream(nomeArquivo);
             FilmeInputStream leitorFilmes = new FilmeInputStream(fileOrigem)) {
            
            // 1. Lê quantos filmes esperar
            int numFilmes = leitorFilmes.readNumFilmes();
            System.out.println("Esperando " + numFilmes + " filme(s) do arquivo...");

            // 2. Faz um loop para ler cada filme
            for (int i = 0; i < numFilmes; i++) {
                Filme filmeLido = leitorFilmes.readFilme();
                filmesLidos.add(filmeLido);
            }
            
            System.out.println("\n--- Leitura do arquivo concluída! ---");
            System.out.println("Filmes recuperados:");
            
            for (Filme filme : filmesLidos) {
                System.out.println(filme.getDetalhes());
                System.out.println("--------------------");
            }

        } catch (Exception e) {
            System.out.println("Erro no teste com Arquivo: " + e.getMessage());
            e.printStackTrace();
        }
        
        
       
    
    }
}