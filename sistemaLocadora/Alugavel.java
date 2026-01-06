package sistemaLocadora;

public interface Alugavel {
    
    /**
     * Define o preço do aluguel.
     * @param preco O preço a ser definido.
     */
    void setPrecoAluguel(double preco);
    
    /**
     * Retorna o preço do aluguel.
     * @return O preço do aluguel.
     */
    double getPrecoAluguel();
    
    
    void alugar();

    void devolver();
    
    /**
     * Verifica se a mídia está disponível para aluguel.
     * @return true se disponível, false caso contrário.
     */
    boolean estaDisponivel();
}