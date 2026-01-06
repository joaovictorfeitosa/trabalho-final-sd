package sistemaLocadora;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer; // Ferramenta para converter 'int' para 'bytes'
import java.nio.charset.StandardCharsets; // Para converter 'String' para 'bytes'

//Q2 TRASNFORMA OBJETOS FILME EM BYTES E MANDA PRA DESTINO
public class FilmeOutputStream extends OutputStream {

    private OutputStream destino; 

    /**
     * Construtor que envia os dados
     * @param filmes Array de filmes para enviar
     * @param numFilmes Número de filmes do array que devem ser enviados
     * @param destino saída para onde os bytes irão.
     */
    public FilmeOutputStream(Filme[] filmes, int numFilmes, OutputStream destino) throws IOException {
        this.destino = destino;
        
        System.out.println("FilmeOutputStream: Iniciando envio de " + numFilmes + " filme(s)...");

        // Primeiro, envia quantos filmes virão (para o receptor saber)
        destino.write(intToBytes(numFilmes));

        // Loop para enviar cada filme
        for (int i = 0; i < numFilmes; i++) {
            Filme filme = filmes[i];
            
            // Serializa 3 atributos
            byte[] tituloBytes = filme.getTitulo().getBytes(StandardCharsets.UTF_8);
            int ano = filme.getAnoLancamento(); // Pegando o ano 
            String diretor = filme.getDiretor(); // Pegando o diretor
            byte[] diretorBytes = diretor.getBytes(StandardCharsets.UTF_8);

            // 1. Envia Titulo (Tamanho + Dados)
            destino.write(intToBytes(tituloBytes.length));
            destino.write(tituloBytes);
            
            // 2. Envia Ano (Dados)
            destino.write(intToBytes(ano));
            
            // 3. Envia Diretor (Tamanho + Dados)
            destino.write(intToBytes(diretorBytes.length));
            destino.write(diretorBytes);
            
            System.out.println("FilmeOutputStream: Enviado -> " + filme.getTitulo());
        }
        System.out.println("FilmeOutputStream: Envio concluído.");
    }

    /*
      Ferramenta auxiliar para transformar um INT em um array de 4 bytes.
     */
    private byte[] intToBytes(int valor) {
        return ByteBuffer.allocate(4).putInt(valor).array();
    }

    
    @Override
    public void write(int b) throws IOException {
       
        throw new UnsupportedOperationException("Este stream não suporta escrita byte-a-byte.");
    }
}