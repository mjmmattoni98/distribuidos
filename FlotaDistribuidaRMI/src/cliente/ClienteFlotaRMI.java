package cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import javax.swing.*;

import comun.IntCallbackCliente;
import comun.IntServidorJuegoRMI;
import comun.IntServidorPartidasRMI;
import partida.Partida;

public class ClienteFlotaRMI {
	/**
	 * Implementa el juego 'Hundir la flota' mediante una interfaz gráfica (GUI)
	 */

	/** Parametros por defecto de una partida */
	public static final int NUMFILAS = 8, NUMCOLUMNAS = 8, NUMBARCOS = 6;
	public static final int AGUA = -1, TOCADO = -2, HUNDIDO = -3;

	private GuiTablero guiTablero = null;					// El juego se encarga de crear y modificar la interfaz gráfica
	private IntServidorPartidasRMI servidorPartidas;		// Objeto con los datos de la partida en juego
	private String nombre;
	private IntServidorJuegoRMI servidorJuego;				// Objeto servidor para poder interaccionar con las demás personas
	private Scanner sc = new Scanner(System.in);
	private IntCallbackCliente callbackCliente;
	
	/** Atributos de la partida guardados en el juego para simplificar su implementación */
	private int quedan = NUMBARCOS, disparos = 0;

	/**
	 * Programa principal. Crea y lanza un nuevo juego
	 * @param args
	 */
	public static void main(String[] args) {
		ClienteFlotaRMI juego = new ClienteFlotaRMI();
		juego.ejecuta();
	} // end main

	/**
	 * Lanza una nueva hebra que crea la primera partida y dibuja la interfaz grafica: tablero
	 */
	private void ejecuta() {
		// Instancia la primera partida
		try {
            System.out.println("Enter your name: ");
            this.nombre = sc.nextLine();

            System.setProperty("java.security.policy", "src/cliente/java.policy");
            
            System.setSecurityManager(new SecurityManager());
            
            String hostName = "localhost";
            int RMIPortNum = 1099;
            String registryURL = "rmi://" + hostName + ":" + RMIPortNum + "/FlotaRMI";
            // find the remote object and cast it to an interface object
            this.servidorJuego = (IntServidorJuegoRMI) Naming.lookup(registryURL);

            System.out.println("Lookup completed ");
            // invoke the remote method
            this.servidorPartidas = servidorJuego.nuevoServidorPartidas();
        } // end try
        catch (Exception e) {
            System.out.println("Exception in ClienteFlotaRMI: " + e);
        }
       
		try {
			callbackCliente = new ImpCallbackCliente();
			this.servidorPartidas.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
					guiTablero.dibujaTablero();
				}
			});
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	} // end ejecuta

	/******************************************************************************************/
	/*********************  CLASE INTERNA GuiTablero   ****************************************/
	/******************************************************************************************/
	private class GuiTablero {
		
		private int numFilas, numColumnas;

		private JFrame frame = null;        // Tablero de juego
		private JLabel estado = null;       // Texto en el panel de estado
		private JButton buttons[][] = null; // Botones asociados a las casillas de la partida

		/**
         * Constructor de una tablero dadas sus dimensiones
         */
		GuiTablero(int numFilas, int numColumnas) {
			this.numFilas = numFilas;
			this.numColumnas = numColumnas;
			frame = new JFrame(nombre);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		}

		/**
		 * Dibuja el tablero de juego y crea la partida inicial
		 */
		public void dibujaTablero() {
			anyadeMenus();
			anyadeGrid(numFilas, numColumnas);		
			anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);		
			frame.setSize(300, 300);
			frame.setVisible(true);	
		} // end dibujaTablero

		/**
		 * Anyade el menu de opciones del juego y le asocia un escuchador
		 */
		private void anyadeMenus() {
			JMenuItem salir, nuevaPartida, mostrarSolucion;
			JMenuBar barra = new JMenuBar();
			JMenu menu = new JMenu("Opciones");
			
			salir = new JMenuItem("Salir");
			salir.setActionCommand("salir");
			nuevaPartida = new JMenuItem("Nueva Partida");
			nuevaPartida.setActionCommand("nuevaPartida");
			mostrarSolucion = new JMenuItem("Mostrar solución");
			mostrarSolucion.setActionCommand("mostrarSolucion");
			
			MenuListener menuListener = new MenuListener();
			salir.addActionListener(menuListener);
			nuevaPartida.addActionListener(menuListener);
			mostrarSolucion.addActionListener(menuListener);
			
			menu.add(salir);
			menu.add(nuevaPartida);
			menu.add(mostrarSolucion);
			barra.add(menu);
			
			JMenuItem proponPartida, borraPartida, listaPartidas, aceptarPartida;
			menu = new JMenu("Opciones callbacks");
			
			proponPartida = new JMenuItem("Propon partida");
			proponPartida.setActionCommand("proponPartida");
			borraPartida = new JMenuItem("Borra Partida");
			borraPartida.setActionCommand("borraPartida");
			listaPartidas = new JMenuItem("Listar partidas");
			listaPartidas.setActionCommand("listaPartidas");
			aceptarPartida = new JMenuItem("Aceptar partida");
			aceptarPartida.setActionCommand("aceptarPartida");
			
			MenuListenerCallback menuListenerCallback = new MenuListenerCallback();
			proponPartida.addActionListener(menuListenerCallback);
			borraPartida.addActionListener(menuListenerCallback);
			listaPartidas.addActionListener(menuListenerCallback);
			aceptarPartida.addActionListener(menuListenerCallback);
			
			menu.add(proponPartida);
			menu.add(borraPartida);
			menu.add(listaPartidas);
			menu.add(aceptarPartida);
			barra.add(menu);
			
			frame.setJMenuBar(barra);
		} 

		/**
		 * Anyade el panel con las casillas del mar y sus etiquetas.
		 * Cada casilla sera un boton con su correspondiente escuchador
		 * @param nf	numero de filas
		 * @param nc	numero de columnas
		 */
		private void anyadeGrid(int nf, int nc) {
			buttons = new JButton[nf][nc];	//Creamos la matriz de botones
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(nf+1,nc+2));
			
			//primera fila
			panel.add(new JLabel(" "));
			for (int i = 0; i < nc; i++) {
			 	panel.add(new JLabel(Integer.toString(i), JLabel.CENTER)); //SwingConstants.CENTER
			}
			panel.add(new JLabel(" "));
			//fin primera fila
			
			ButtonListener buttonListener = new ButtonListener();
			//rellenar matriz
			for(int i = 0; i < nf; i++) {//por fila
				String letra = Character.toString((char) ('A' + i));
				panel.add(new JLabel(letra, JLabel.CENTER));
				
				for (int j = 0; j < nc; j++) { //por columna
					JButton b = new JButton();
					b.addActionListener(buttonListener);
					b.setActionCommand(Integer.toString(i) + "#" + Integer.toString(j));	//Para guardar la posición del botón
					buttons[i][j] = b; //guardo la refenecia del boton
			 		panel.add(b);
				}
				
				panel.add(new JLabel(letra, JLabel.CENTER));
			}
			
			frame.add(panel, BorderLayout.CENTER);
		} 

		/**
		 * Anyade el panel de estado al tablero
		 * @param cadena	cadena inicial del panel de estado
		 */
		private void anyadePanelEstado(String cadena) {	
			JPanel panelEstado = new JPanel();
			estado = new JLabel(cadena);
			panelEstado.add(estado);
			// El panel de estado queda en la posición SOUTH del frame
			frame.getContentPane().add(panelEstado, BorderLayout.SOUTH);
		} // end anyadePanel Estado

		/**
		 * Cambia la cadena mostrada en el panel de estado
		 * @param cadenaEstado	nuevo estado
		 */
		public void cambiaEstado(String cadenaEstado) {
			estado.setText(cadenaEstado);
		} // end cambiaEstado

		/**
		 * Muestra la solucion de la partida y marca la partida como finalizada
		 */
		public void muestraSolucion() {
			//Pintamos primero todo el mar de azul
			for(int i = 0; i < NUMFILAS; i++) {
				for(int j = 0; j < NUMCOLUMNAS; j++) {
					JButton b = buttons[i][j];
					pintaBoton(b, Color.BLUE);
				}
			}
			
			//Ahora pintamos simplemente los barcos como hundidos
			try {
				String[] stringBarcos = servidorPartidas.getSolucion();
				for(int i = 0; i < NUMBARCOS; i++) {
					pintaBarcoHundido(stringBarcos[i]);
				}
			}
			catch(RemoteException e) {
				e.printStackTrace();
			}
		} 

		/**
		 * Pinta un barco como hundido en el tablero
		 * @param cadenaBarco	cadena con los datos del barco codifificados como
		 *                      "filaInicial#columnaInicial#orientacion#tamanyo"
		 */
		public void pintaBarcoHundido(String cadenaBarco) {
			String[] barco = cadenaBarco.split("#");
			int filaInicial = Integer.parseInt(barco[0]); 
			int columnaInicial = Integer.parseInt(barco[1]); 
			String orientacion = barco[2];
			int tamanyo = Integer.parseInt(barco[3]); 
			for(int i = 0; i < tamanyo; i++) { 
				pintaBoton(buttons[filaInicial][columnaInicial], Color.RED);
				if(orientacion.equals("H"))  columnaInicial++; //Orientación horixontal
    			else filaInicial++; //Orientación vertical
			}
		}

		/**
		 * Pinta un botón de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente código solo es necesario en Mac OS X
			//b.setOpaque(true);
			//b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pintándolas del gris por defecto
		 */
		public void limpiaTablero() {
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++) {
					buttons[i][j].setBackground(null);
					buttons[i][j].setOpaque(true);
					buttons[i][j].setBorderPainted(true);
				}
			}
		} // end limpiaTablero

		/**
		 * 	Destruye y libera la memoria de todos los componentes del frame
		 */
		public void liberaRecursos() {
			frame.dispose();
		} // end liberaRecursos


	} // end class GuiTablero

	/******************************************************************************************/
	/*********************  CLASE INTERNA MenuListener ****************************************/
	/******************************************************************************************/

	/**
	 * Clase interna que escucha el menu de Opciones del tablero
	 * 
	 */
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String opcion = e.getActionCommand();

			switch(opcion) {
				case "salir":
					guiTablero.liberaRecursos();
					sc.close();
					try {
						servidorJuego.borraPartida(nombre);
					}
					catch(RemoteException ex) {
						ex.printStackTrace();
					}
					break;
				case "nuevaPartida":
					guiTablero.limpiaTablero();
					disparos = 0;
					quedan = NUMBARCOS;
					try {
						servidorPartidas.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
					}
					catch(RemoteException ex) {
						ex.printStackTrace();
					}
					guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
					break;
				case "mostrarSolucion":
					quedan = -1;
					guiTablero.muestraSolucion();
			}
			
		} // end actionPerformed

	} // end class MenuListener



	/******************************************************************************************/
	/*********************  CLASE INTERNA MenuListenerCallback ****************************************/
	/******************************************************************************************/

	/**
	 * Clase interna que escucha el menu de Opciones del tablero
	 * 
	 */
	private class MenuListenerCallback implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String opcion = e.getActionCommand();
			boolean exito = false;
			
			try {
				switch(opcion) {
					case "proponPartida":
						exito = servidorJuego.proponPartida(nombre, callbackCliente);
						if(exito)
							System.out.println("Has propuesto una partida.");
						else
							System.out.println("Ya habías propuesto una partida.");
						break;
					case "borraPartida":
						exito = servidorJuego.borraPartida(nombre);
						if (exito)
							System.out.println("Se te ha borrado la propuesta de partida");
						else
							System.out.println("No habías propuesto partida.");
						break;
					case "listaPartidas":
						String[] jugadores = servidorJuego.listaPartidas();
						if(jugadores.length == 0)
							System.out.println("Nadie ha propuesto una partida");
						else {
							System.out.println("Jugadores que han propuesto una partida:");
							for(String jugador : jugadores) {
								System.out.println(jugador);
							}
						}
						break;
					case "aceptarPartida":
						System.out.println("Enter the enemy name:");
				        String enemyName = sc.nextLine();
				        if(enemyName.equals(nombre))
				        	System.out.println("No puedes aceptar tu propia partida.");
				        else {
					        exito = servidorJuego.aceptaPartida(nombre, enemyName);
							if (exito)
								System.out.println("La partida ha sido aceptada con exito.");
							else
								System.out.println("Ha habido un error al aceptar la partida.");
				        }
				}
			}
			catch(RemoteException ex) {
				System.out.println("Error en el menu callbacks.");
				ex.printStackTrace();
			}
		} // end actionPerformed

	} // end class MenuListener



	/******************************************************************************************/
	/*********************  CLASE INTERNA ButtonListener **************************************/
	/******************************************************************************************/
	/**
	 * Clase interna que escucha cada uno de los botones del tablero
	 * Para poder identificar el boton que ha generado el evento se pueden usar las propiedades
	 * de los componentes, apoyandose en los metodos putClientProperty y getClientProperty
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
	            if (quedan > 0) { //Solo realizamos una acción cuando la partida no ha finalizado.
					String infoBoton = e.getActionCommand();
					String[] posiciones = infoBoton.split("#");
					int fila = Integer.parseInt(posiciones[0]);
					int columna = Integer.parseInt(posiciones[1]);
					int estado = servidorPartidas.pruebaCasilla(fila, columna);
					JButton boton = (JButton) e.getSource();
					//JButton boton = guiTablero.buttons[f][c];
					disparos++;
					
					switch(estado) {
						case AGUA: 
							guiTablero.pintaBoton(boton, Color.BLUE);
							break;
						case TOCADO: 
							guiTablero.pintaBoton(boton, Color.YELLOW);
							break;
						case HUNDIDO: 
							guiTablero.pintaBoton(boton, Color.RED);
							break;
						default: //IDs de los barcos
							quedan--;
							String barco = servidorPartidas.getBarco(estado);
							guiTablero.pintaBarcoHundido(barco);
					}
					
					if(quedan == 0) guiTablero.cambiaEstado("GAME OVER en " + disparos + " disparos");
					else guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
	            }
			}
			catch(RemoteException ex) {
	           	ex.printStackTrace();
	        }
        } // end actionPerformed

	} // end class ButtonListener

} // end class Juego
