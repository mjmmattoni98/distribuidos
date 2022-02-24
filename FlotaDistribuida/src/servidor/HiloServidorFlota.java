package servidor;


import java.io.IOException;
import java.net.SocketException;

import partida.*;
import comun.MyStreamSocket;

/**
 * Clase ejecutada por cada hebra encargada de servir a un cliente del juego Hundir la flota.
 * El metodo run contiene la logica para gestionar una sesion con un cliente.
 */

 // Revisar el apartado 5.5. del libro de Liu

class HiloServidorFlota implements Runnable {
   MyStreamSocket myDataSocket;
   private Partida partida = null;

	/**
	 * Construye el objeto a ejecutar por la hebra para servir a un cliente
	 * @param	myDataSocket	socket stream para comunicarse con el cliente
	 */
   HiloServidorFlota(MyStreamSocket myDataSocket) {
	   this.myDataSocket=myDataSocket; 
   }
 
   /**
	* Gestiona una sesion con un cliente	
   */
   public void run( ) {
      int operacion = 0;
      boolean done = false;
      try {
         while (!done) {
        	 // Recibe una peticion del cliente
        	 // Extrae la operación y los argumentos
                      
        	 String[] cadena =myDataSocket.receiveMessage().split("#");
        	 operacion = Integer.parseInt(cadena[0]);
             switch (operacion) {
             case 0:  // fin de conexión con el cliente
            	 // ...
            	 myDataSocket.close();
            	 done=true;
            	 break;

             case 1: { // Crea nueva partida
            	 // ...
            	 int nf = Integer.parseInt(cadena[1]);
            	 int nc=Integer.parseInt(cadena[2]);
            	 int nb=Integer.parseInt(cadena[3]);
            	 partida = new Partida(nf,nc,nb);
            	 
            	 break;
             }             
             case 2: { // Prueba una casilla y devuelve el resultado al cliente
            	 // ... 
            	 int f = Integer.parseInt(cadena[1]);
            	 int c=Integer.parseInt(cadena[2]);
            	 
            	 int message =partida.pruebaCasilla(f, c);
            	 myDataSocket.sendMessage(message+"");
                 break;
             }
             case 3: { // Obtiene los datos de un barco y se los devuelve al cliente
            	 // ... 
            	 int b=Integer.parseInt(cadena[1]);
            	 String barco=partida.getBarco(b);
                 myDataSocket.sendMessage(barco);
            	 break;
             }
             case 4: { // Devuelve al cliente la solucion en forma de vector de cadenas
        	   // Primero envia el numero de barcos 
               // Despues envia una cadena por cada barco
            	String[] sor=partida.getSolucion();
            	myDataSocket.sendMessage(sor.length+"");
            	for (String b:sor) {
            		myDataSocket.sendMessage(b);
            	}
               break;
             }
         } // fin switch
       } // fin while   
     } // fin try
     catch (Exception ex) {
        System.out.println("Exception caught in thread: " + ex);
     } // fin catch
   } //fin run
   
} //fin class 
