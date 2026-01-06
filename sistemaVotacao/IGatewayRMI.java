package sistemaVotacao;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IGatewayRMI extends Remote {
    
    
    byte[] enviarMensagem(byte[] mensagemRequest) throws RemoteException;
}