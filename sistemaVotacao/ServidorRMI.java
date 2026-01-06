
package sistemaVotacao;

import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;

public class ServidorRMI {

    private static final SistemaVotacao sistema = new SistemaVotacao();

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Servidor RMI...");

            
            LocateRegistry.createRegistry(1099);

            // Instancia a implementação do serviço (Gateway)
            sistema.setVotacaoAberta(true); // Abre a votação
            GatewayRMIImpl gateway = new GatewayRMIImpl(sistema);

            //  Registra o serviço com um nome
            Naming.rebind("rmi://localhost:1099/VotacaoService", gateway);

            System.out.println("Servidor RMI pronto e aguardando requisições!");

        } catch (Exception e) {
            System.out.println("Erro no Servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}