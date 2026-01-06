package sistemaLocadora;

public abstract class Midia implements Alugavel {
    
    
    protected String titulo;
    protected int anoLancamento;
    protected double precoAluguel;
    protected boolean disponivel;

    
    public Midia(String titulo, int anoLancamento) {
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.disponivel = true;
    }

    // --- Métodos da Interface Alugavel ---
    @Override
    public void setPrecoAluguel(double preco) {
        this.precoAluguel = preco;
    }

    @Override
    public double getPrecoAluguel() {
        return this.precoAluguel;
    }

    @Override
    public void alugar() {
        if (this.disponivel) {
            this.disponivel = false;
        } else {
            System.out.println("ERRO: " + this.titulo + " já está alugado.");
        }
    }

    @Override
    public void devolver() {
        this.disponivel = true;
    }

    @Override
    public boolean estaDisponivel() {
        return this.disponivel;
    }
    
    
    public String getTitulo() {
        return this.titulo;
    }
    
   
    public String getDetalhes() {
        return "Título: " + this.titulo + 
               "\nAno: " + this.anoLancamento +
               "\nDisponível: " + (this.disponivel ? "Sim" : "Não");
    }

    public int getAnoLancamento() {
    return this.anoLancamento;
}
}