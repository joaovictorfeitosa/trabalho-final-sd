package sistemaLocadora;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer; // Ferramenta para converter 'bytes' para 'int'
import java.nio.charset.StandardCharsets; // Para converter 'bytes' para 'String'


public class FilmeInputStream extends InputStream {

    private InputStream origem; // O "cano" de origem (arquivo, rede, etc.)

    /**
     * Construtor que recebe a origem dos dados, como pedido.
     * @param origem O "cano" de entrada de onde os bytes virão.
     */
    public FilmeInputStream(InputStream origem) {
        this.origem = origem;
    }

    //Lê os bytes do stream e os monta de volta em um objeto Filme.
     
    public Filme readFilme() throws IOException {
        // 1. Lê o Titulo (Tamanho + Dados)
        int tituloTamanho = readInt();
        String titulo = readString(tituloTamanho);

        // 2. Lê o Ano (Dados)
        int ano = readInt();

        // 3. Lê o Diretor (Tamanho + Dados)
        int diretorTamanho = readInt();
        String diretor = readString(diretorTamanho);
        
        System.out.println("FilmeInputStream: Lido -> " + titulo);

        
        return new Filme(titulo, ano, diretor, 0);
    }
    
    /**
     * Lê o primeiro inteiro do stream, que
     * é o número total de filmes que serão enviados.
     */
    public int readNumFilmes() throws IOException {
        System.out.println("FilmeInputStream: Lendo número de filmes...");
        return readInt();
    }

    /**
     * Ferramenta auxiliar para ler 4 bytes da origem e transformar em INT.
     */
    private int readInt() throws IOException {
        byte[] intBytes = new byte[4];
        int bytesLidos = origem.read(intBytes);
        if (bytesLidos < 4) {
            throw new IOException("Fim de stream inesperado ao ler um int.");
        }
        return ByteBuffer.wrap(intBytes).getInt();
    }

    /**
     * Ferramenta auxiliar para ler 'tamanho' bytes da origem e transformar em STRING.
     */
    private String readString(int tamanho) throws IOException {
        byte[] stringBytes = new byte[tamanho];
        int bytesLidos = origem.read(stringBytes);
        if (bytesLidos < tamanho) {
            throw new IOException("Fim de stream inesperado ao ler uma string.");
        }
        return new String(stringBytes, StandardCharsets.UTF_8);
    }

    
    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("Este stream não suporta leitura byte-a-byte.");
    }
}