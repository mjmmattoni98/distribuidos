package comun;

import java.rmi.Remote;
import java.rmi.RemoteException;

import partida.Partida;

public interface IntServidorPartidasRMI extends Remote{
	public void nuevaPartida(int nf, int nc, int nb) throws RemoteException;
	public int pruebaCasilla(int f, int c) throws RemoteException;
	public String getBarco(int barco) throws RemoteException;
	public String[] getSolucion() throws RemoteException;
}
