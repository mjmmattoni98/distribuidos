package cliente;
import java.net.*;
import java.io.*;

import comun.*;

/**
 * Esta clase implementa el intercambio de mensajes
 * asociado a cada una de las operaciones basicas que comunican cliente y servidor
 */

public class AuxiliarClienteFlota {

   private MyStreamSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

	/**
	 * Constructor del objeto auxiliar del cliente
	 * Crea un socket de tipo 'MyStreamSocket' y establece una conexión con el servidor
	 * 'hostName' en el puerto 'portNum'
	 * @param	hostName	nombre de la máquina que ejecuta el servidor
	 * @param	portNum		numero de puerto asociado al servicio en el servidor
	 */
   AuxiliarClienteFlota(String hostName,
                     String portNum) throws SocketException,
                     UnknownHostException, IOException {
	   
	  this.serverHost = InetAddress.getByName(hostName);
      this.serverPort = Integer.parseInt(portNum);
      mySocket = new MyStreamSocket(this.serverHost, this.serverPort);
	  System.out.println("Connection request made");
   } // end constructor
   
   
   /**
	 * Usa el socket para enviar al servidor una petición de fin de conexión
	 * con el formato: "0"
	 * @throws	IOException
	 */
   public void fin( ) {
	   
	   try {
		mySocket.sendMessage("0");//mensaje con el que se cierra la conexión
		mySocket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("Fallo conexion cierre");
		e.printStackTrace();
	}
	   
   } // end fin 
  
   /**
    * Usa el socket para enviar al servidor una petición de creación de nueva partida 
    * con el formato: "1#nf#nc#nb"
    * @param nf	número de filas de la partida
    * @param nc	número de columnas de la partida
    * @param nb	número de barcos de la partida
    * @throws IOException
    */
   public void nuevaPartida(int nf, int nc, int nb)  throws IOException {
	   mySocket.sendMessage(1+"#"+nf+"#"+nc+"#"+nb); //Con el primer número se indica que método es
   } // end nuevaPartida

   /**
    * Usa el socket para enviar al servidor una petición de disparo sobre una casilla 
    * con el formato: "2#f#c"
    * @param f	fila de la casilla
    * @param c	columna de la casilla
    * @return	resultado del disparo devuelto por la operación correspondiente del objeto Partida
    * 			en el servidor.
    * @throws IOException
    */
   public int pruebaCasilla(int f, int c) throws IOException {
	   mySocket.sendMessage(2+"#"+f+"#"+c);
	   String resultadoCasilla = mySocket.receiveMessage();
	 
	   return Integer.parseInt(resultadoCasilla); 
    } // end pruebaCasilla
   
   /**
    * Usa el socket para enviar al servidor una petición de los datos de un barco
    * con el formato: "3#idBarco"
    * @param idBarco	identidad del Barco
    * @return			resultado devuelto por la operación correspondiente del objeto Partida
    * 					en el servidor.
    * @throws IOException
    */
   public String getBarco(int idBarco) throws IOException {
	   mySocket.sendMessage(3+"#"+idBarco);
	   String informacionBarco = mySocket.receiveMessage();
	   
	   return informacionBarco;
    } // end getBarco
   
   /**
    * Usa el socket para enviar al servidor una petición de los datos de todos los barcos
    * con el formato: "4"
    * @return	resultado devuelto por la operación correspondiente del objeto Partida
    * 			en el servidor
    * @throws IOException
    */
   public String[] getSolucion() throws IOException {
	   mySocket.sendMessage("4");
	   int numB=Integer.parseInt(mySocket.receiveMessage());
	   String[] stringBarcos = new String[numB];
	   for(int i=0;i<numB;i++) {
		   stringBarcos[i]=mySocket.receiveMessage();
	   }
	   
	   return stringBarcos; 
    } // end getSolucion
   


} //end class
