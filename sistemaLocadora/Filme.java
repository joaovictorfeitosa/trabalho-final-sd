package sistemaLocadora;

public class Filme extends Midia {
    
    private String diretor;
    private int duracaoMinutos;

    
    public Filme(String titulo, int anoLancamento, String diretor, int duracao) {
        
        super(titulo, anoLancamento);
        this.diretor = diretor;
        this.duracaoMinutos = duracao;
    }

    
    @Override
    public String getDetalhes() {
        
        String detalhesPai = super.getDetalhes();
        
        return detalhesPai + 
               "\nDiretor: " + this.diretor +
               "\nDuração: " + this.duracaoMinutos + " min";
    }
    public String getDiretor() {
    return this.diretor;
}
}