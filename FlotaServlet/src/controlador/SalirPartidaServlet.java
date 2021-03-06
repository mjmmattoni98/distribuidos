package controlador;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SalirPartidaServlet
 */
public class SalirPartidaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SalirPartidaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if(session != null) //Hacemos que no se pueda seguir jugando la partida actual
			session.invalidate();
		
		RequestDispatcher vista = request.getRequestDispatcher("index.html"); //Y redireccionamos a la p?gina principal para que empiece otra si quiere
		
		vista.forward(request, response);
	}

}
