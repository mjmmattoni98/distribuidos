package servidor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import comun.IntServidorPartidasRMI;
import partida.Partida;

public class ImpServidorPartidasRMI extends UnicastRemoteObject implements IntServidorPartidasRMI{
	Partida partida;
	public ImpServidorPartidasRMI() throws RemoteException {
		super();
	}

	@Override
	public void nuevaPartida(int nf, int nc, int nb) throws RemoteException {
		this.partida = new Partida(nf, nc, nb);
	}

	@Override
	public int pruebaCasilla(int f, int c) throws RemoteException {
		return this.partida.pruebaCasilla(f, c);
	}

	@Override
	public String getBarco(int barco) throws RemoteException {
		return this.partida.getBarco(barco);
	}

	@Override
	public String[] getSolucion() throws RemoteException {
		return this.partida.getSolucion();
	}
	

}
