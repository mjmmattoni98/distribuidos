package cliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;


public class ClienteFlotaWS {

	public static final int NUMFILAS = 8, NUMCOLUMNAS = 8, NUMBARCOS = 6;
	public static final int AGUA = -1, TOCADO = -2, HUNDIDO = -3;
	
	private GuiTablero guiTablero = null;			// El juego se encarga de crear y modificar la interfaz gr�fica
	private GestorPartidas partida = null;    // Objeto con los datos de la partida en juego
	
	/** Atributos de la partida guardados en el juego para simplificar su implementaci�n */
	private int quedan = NUMBARCOS, disparos = 0;

	/**
	 * Programa principal. Crea y lanza un nuevo juego
	 * @param args
	 */
	public static void main(String[] args) {
		ClienteFlotaWS juego= new ClienteFlotaWS();
		juego.ejecuta();
	} // end main

	/**
	 * Lanza una nueva hebra que crea la primera partida y dibuja la interfaz grafica: tablero
	 */
	private void ejecuta() {
		try {
			partida = new GestorPartidas();
			partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
				guiTablero.dibujaTablero();
			}
		});
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
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		}

		/**
		 * Dibuja el tablero de juego y crea la partida inicial
		 */
		public void dibujaTablero() {
			anyadeMenu();
			anyadeGrid(numFilas, numColumnas);		
			anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);		
			frame.setSize(300, 300);
			frame.setVisible(true);	
		} // end dibujaTablero

		/**
		 * Anyade el menu de opciones del juego y le asocia un escuchador
		 */
		private void anyadeMenu() {
			JMenuItem salir, nuevaPartida, mostrarSolucion;
			JMenuBar barra = new JMenuBar();
			JMenu menu = new JMenu("Opciones");
			
			salir = new JMenuItem("Salir");
			salir.setActionCommand("salir");
			nuevaPartida = new JMenuItem("Nueva Partida");
			nuevaPartida.setActionCommand("nuevaPartida");
			mostrarSolucion = new JMenuItem("Mostrar soluci�n");
			mostrarSolucion.setActionCommand("mostrarSolucion");
			
			MenuListener menuListener = new MenuListener();
			salir.addActionListener(menuListener);
			nuevaPartida.addActionListener(menuListener);
			mostrarSolucion.addActionListener(menuListener);
			
			menu.add(salir);
			menu.add(nuevaPartida);
			menu.add(mostrarSolucion);
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
					b.setActionCommand(Integer.toString(i) + "#" + Integer.toString(j));	//Para guardar la posici�n del bot�n
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
			// El panel de estado queda en la posici�n SOUTH del frame
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
			String[] stringBarcos;
			try {
				stringBarcos = partida.getSolucion();
				for(int i = 0; i < NUMBARCOS; i++) {
					pintaBarcoHundido(stringBarcos[i]);}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
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
				if(orientacion.equals("H"))  columnaInicial++; //Orientaci�n horixontal
    			else filaInicial++; //Orientaci�n vertical
			}
		}

		/**
		 * Pinta un bot�n de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente c�digo solo es necesario en Mac OS X
			//b.setOpaque(true);
			//b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pint�ndolas del gris por defecto
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
			
			try {
				switch(opcion) {
					case "salir":
						guiTablero.liberaRecursos();
						partida.borraPartida();
						break;
					case "nuevaPartida":
						guiTablero.limpiaTablero();
						disparos = 0;
						quedan = NUMBARCOS;
						partida.borraPartida();
						partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
						guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
						break;
					default: //Mostrar soluci�n
						quedan = -1;
						guiTablero.muestraSolucion();
				}
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
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
	            if (quedan > 0) { //Solo realizamos una acci�n cuando la partida no ha finalizado.
					String infoBoton = e.getActionCommand();
					String[] posiciones = infoBoton.split("#");
					int fila = Integer.parseInt(posiciones[0]);
					int columna = Integer.parseInt(posiciones[1]);
					int estado = partida.pruebaCasilla(fila, columna);
					JButton boton = (JButton) e.getSource();
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
							String barco = partida.getBarco(estado);
							guiTablero.pintaBarcoHundido(barco);
					}
					
					if(quedan == 0) guiTablero.cambiaEstado("GAME OVER en " + disparos + " disparos");
					else guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
	            }
			}
			catch(Exception ex) {
	           	System.out.println(ex.getMessage());
	        }	
        } // end actionPerformed

	} // end class ButtonListener

}
