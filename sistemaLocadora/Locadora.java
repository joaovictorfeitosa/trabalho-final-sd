package sistemaLocadora;

import java.util.ArrayList;
import java.util.List;

public class Locadora {
    
    private String nome;
    // Agregação: A locadora "tem" uma lista de mídias
    private List<Midia> acervo;

    public Locadora(String nome) {
        this.nome = nome;
        this.acervo = new ArrayList<>();
    }

    
     //Adiciona qualquer objeto que seja filho de Midia ao acervo.
     
    public void adicionarMidia(Midia midia) {
        this.acervo.add(midia);
        System.out.println(midia.getTitulo() + " foi adicionado ao acervo.");
    }
    
    /**
     * Busca uma mídia no acervo pelo título.
     * @return A Mídia encontrada ou null se não encontrar.
     */
    public Midia buscarPorTitulo(String titulo) {
        for (Midia midia : this.acervo) {
            // equalsIgnoreCase ignora maiúsculas/minúsculas
            if (midia.getTitulo().equalsIgnoreCase(titulo)) {
                return midia;
            }
        }
        return null; // Não encontrou
    }

    
    //Retorna uma String formatada de todo o acervo.
    //Usado pelo servidor para enviar ao cliente.
    
    public String getStringAcervo() {
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("--- Acervo da ").append(this.nome).append(" ---\n");
        
        if (this.acervo.isEmpty()) {
            builder.append("O acervo está vazio.\n");
        }
        
        for (Midia midia : this.acervo) {
            builder.append(midia.getDetalhes());
            builder.append("\n--------------------\n");
        }
        return builder.toString();
    }
}