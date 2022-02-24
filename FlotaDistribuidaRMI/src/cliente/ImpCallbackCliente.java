package cliente;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import comun.IntCallbackCliente;

public class ImpCallbackCliente extends UnicastRemoteObject implements IntCallbackCliente{

	public ImpCallbackCliente() throws RemoteException {
		super();
	}

	@Override
	public void notificame(String nombre) throws RemoteException {
		System.out.println("El cliente " + nombre + " ha aceptado tu partida.");
	}

}
