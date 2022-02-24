package controlador;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modelo.Partida;

/**
 * Servlet implementation class HundirFlotaServlet
 */
public class HundirFlotaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HundirFlotaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Object o = new Object();
		
		Partida partida = new Partida(8, 8, 6);
		String mensaje;
		if(session.getAttribute("partida") == null) { //Si la partida no estaba creada, se crea y se guarda en el session
			session.setAttribute("partida", partida);
			mensaje = "NUEVA PARTIDA";	
		}
		else { //Si la partida está creada
			partida = (Partida) session.getAttribute("partida");
			String posicion = (String) request.getParameter("casilla");
			if(posicion == null)	//Si no se ha seleccionado ninguna casilla
				mensaje = "Tienes que disparar sobre una casilla"; 
			else {  //Si se ha seleccionado una casilla, actualizamos partida
				int fila = Integer.parseInt(Character.toString(posicion.charAt(0)));
				int col = posicion.charAt(1) - 'A';
				boolean disparada = partida.casillaDisparada(fila, col);
				partida.pruebaCasilla(fila, col);
				int quedan = partida.getBarcosPorHundir();
				if(quedan == 0)
					mensaje = "GAME OVER";
				else {
					mensaje = "Página de resultado del disparo en (" + (fila+1) + "," + posicion.charAt(1) + "): ";
					if(!disparada)
						mensaje += "Ok!";
					else
						mensaje += "La casilla ya había sido disparada";
				}
			}
			session.setAttribute("partida", partida);	//Guardamos la partida actualizada en el sesion
		}
		session.setAttribute("mensaje", mensaje);
		RequestDispatcher vista = request.getRequestDispatcher("TableroActual.jsp"); 
		
		vista.forward(request, response); //lo redirigimos al tablero en juego
	}

}
