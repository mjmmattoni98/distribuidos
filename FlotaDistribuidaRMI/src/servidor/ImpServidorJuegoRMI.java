package servidor;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import comun.IntCallbackCliente;
import comun.IntServidorJuegoRMI;
import comun.IntServidorPartidasRMI;

public class ImpServidorJuegoRMI extends UnicastRemoteObject implements IntServidorJuegoRMI{
	volatile private Map<String, IntCallbackCliente> callbacks;
	
	public ImpServidorJuegoRMI() throws RemoteException {
		super();
		this.callbacks = new ConcurrentHashMap<String, IntCallbackCliente>();
	}

	@Override
	public IntServidorPartidasRMI nuevoServidorPartidas() throws RemoteException {
		return new ImpServidorPartidasRMI();
	}

	@Override
	public synchronized boolean proponPartida(String nombreJugador, IntCallbackCliente callbackClientObject) throws RemoteException {
		IntCallbackCliente cb = this.callbacks.put(nombreJugador, callbackClientObject);
		return cb == null; //Devuelve un booleano dependiendo de si ya había propuesto una partida o no
	}

	@Override
	public synchronized boolean borraPartida(String nombreJugador) throws RemoteException {
		IntCallbackCliente cb = this.callbacks.remove(nombreJugador);
		return cb != null; //Devuelve un booleano dependiendo de si había prupuesto una partida o no.
	}

	@Override
	public synchronized String[] listaPartidas() throws RemoteException {
		String[] jugadores = new String[this.callbacks.size()];
		int i = 0;
		for(String key : this.callbacks.keySet()) {
			jugadores[i++] = key;
		}
		return jugadores;
	}

	@Override
	public synchronized boolean aceptaPartida(String nombreJugador, String nombreRival) throws RemoteException {
		IntCallbackCliente cb = this.callbacks.get(nombreRival);
		boolean exito = false;
		
		if(cb != null) {
			try {
				cb.notificame(nombreJugador);
				exito = true;
			}
			catch(ConnectException e) {
				System.out.println("El jugador rival había salido abruptamente del servidor.");
				e.printStackTrace();
			}
			finally {
				this.callbacks.remove(nombreRival);		
			}
		}
		return exito;		
	}
}
