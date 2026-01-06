package sistemaLocadora;

public class Jogo extends Midia {
    
    private String plataforma;


    public Jogo(String titulo, int anoLancamento, String plataforma) {
        super(titulo, anoLancamento);
        this.plataforma = plataforma;
    }
    
    
    @Override
    public String getDetalhes() {
        String detalhesPai = super.getDetalhes();
        
        return detalhesPai + 
               "\nPlataforma: " + this.plataforma;
    }
}