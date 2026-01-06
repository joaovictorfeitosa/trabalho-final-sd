package sistemaVotacao;

import java.io.Serializable;

// Serializable é uma interface "marcadora" que facilita o uso de JSON
//Q1 POJO
public class Candidato implements Serializable {
    private int numero;
    private String nome;
    private int votos;

    public Candidato(int numero, String nome) {
        this.numero = numero;
        this.nome = nome;
        this.votos = 0;
    }

    public int getNumero() { return numero; }
    public String getNome() { return nome; }
    public int getVotos() { return votos; }

    // Método de serviço
    public void adicionarVoto() {
        this.votos++;
    }

    @Override
    public String toString() {
        return "Candidato [Nº " + numero + ", Nome: " + nome + ", Votos: " + votos + "]";
    }
}