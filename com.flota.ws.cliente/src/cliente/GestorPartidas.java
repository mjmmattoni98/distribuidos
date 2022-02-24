package cliente;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class GestorPartidas {

	// URI del recurso que permite acceder al juego
	final private String baseURI = "http://localhost:8080/com.flota.ws/servicios/partidas/";
	Client cliente = null;
	// Para guardar el target que obtendrá con la operación nuevaPartida y que le permitirá jugar la partida creada
	private WebTarget targetPartida = null;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de la clase
	 * Crea el cliente
	 */
	public GestorPartidas()  {
        this.cliente = ClientBuilder.newClient();
	}

	/**
	 * Crea una nueva partida
	 * Realiza una peticion POST a la URI {baseURI}
	 * @param	numFilas	numero de filas del tablero
	 * @param	numColumnas	numero de columnas del tablero
	 * @param	numBarcos	numero de barcos
	 * @throws	WebApplicationException 
	 */
	public void nuevaPartida(int numFilas, int numColumnas, int numBarcos) throws WebApplicationException   {

		String datosPartida = numFilas + "#" + numColumnas + "#" + numBarcos;
		Response response = cliente.target(baseURI)
				.request().post(Entity.text(datosPartida));

		if (response.getStatus() == 201) { // 201 = CREATED
			// Obtiene la informació sobre el URI del nuevo recurso partida de la cabecera 'Location' en la respuesta
			String recursoPartida = response.getLocation().toString();
			// se guarda la URI de la partida en el atributo targetPartida para poder usarla en los otros metodos que acceden a ella
			this.targetPartida = cliente.target(recursoPartida);
			System.out.println("Instancio una nueva partida con id: " + recursoPartida);
			response.close();
		} else
			throw new WebApplicationException("Fallo al crear partida");

	}

	/**
	 * Borra la partida en juego identificada mediatne un WebTarget construido al crearla
	 */
	public void borraPartida() throws NotFoundException, WebApplicationException {  
		Response response = this.targetPartida.request().delete();
        int estado = response.getStatus(); 
        response.close();
        if(estado == 200) //200 = OK
        	System.out.println("Partida borrada con �xito.");
        else {
	        if (estado == 404) // 404 = NOT_FOUND
				throw new NotFoundException("No se ha podido encontrar la partida.");
			else // Algun otro error
				throw new WebApplicationException("ERROR al intentar borrar la partida: ");
	    }
    }



	/**
	 * Prueba una casilla y devuelve el resultado
	 * @param	fila	fila de la casilla
	 * @param	columna	columna de la casilla
	 * @return			resultado de la prueba: AGUA, TOCADO, ya HUNDIDO, recien HUNDIDO
	 */
	public int pruebaCasilla( int fila, int columna) throws NotFoundException, WebApplicationException  { 
		// Actualiza la partida mediante un PUT pasando 
		// el identifiador en la ruta del URI
		// y los nuevos datos en la cadena de consulta (query string).
		// Pasamos el cuerpo como un texto vacío porque PUT no admite null como argumento
		Response response = this.targetPartida.path("/casilla")
				.queryParam("fila", fila)
				.queryParam("columna", columna)
				.request() 
				.put(Entity.text(""));
		int estado = response.getStatus();

		if (estado == 200) { // 200 = OK 			
			// Lee la cadena con el estado de la casilla del cuerpo (Entity) del mensaje
			int estadoCasilla = Integer.parseInt(response.readEntity(String.class));
			response.close();	
			return estadoCasilla;
		}
		else {
			response.close();			
			if (estado == 404) { // 404 = NOT_FOUND
				throw new NotFoundException("No se ha podido encontrar la partida para actualizarla.");
		    }
		    else  // Algun otro error
				throw new WebApplicationException("ERROR al intentar actualizar la partida.");
		}
	}

	/**
	 * Obtiene los datos de un barco.
	 * @param	idBarco	identificador del barco
	 * @return			cadena con informacion sobre el barco "fila#columna#orientacion#tamanyo"
	 */
	public String getBarco( int idBarco) throws NotFoundException, WebApplicationException { 
		Response response = this.targetPartida.path("/barco/"+idBarco)
				.request(MediaType.TEXT_PLAIN).get();

        int estado = response.getStatus();
		if (estado == 200) { // 200 = OK 			
			// Lee la cadena con la informacion del barco del cuerpo (Entity) del mensaje
			String cadenaBarco = response.readEntity(String.class);
			response.close();	
			return cadenaBarco;
		}
		else {
			response.close();			
			if (estado == 404) { // 404 = NOT_FOUND
				throw new NotFoundException("Barco a leer con id: " + idBarco + " no encontrado");
		    }
		    else  // Algun otro error
			    throw new WebApplicationException("ERROR al intentar obtener el barco con id: " + idBarco);		   
		}
	}


	/**
	 * Devuelve la informacion sobre todos los barcos
	 * Realiza una peticion GET a la URI {baseURI}/{idPartida}/solucion
	 * @return			vector de cadenas con la informacion de cada barco
	 * @throws	NotFoundException, WebApplicationException, Exception
	 */
	protected String[] getSolucion() throws NotFoundException, WebApplicationException, Exception {
		Response response = targetPartida.path("/solucion")
				.request().get();
		int estado = response.getStatus();
		if (estado == 200) { // 200 = OK
			// Instancia el constructor de objetos de tipo Document
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// Obtiene la informacion del cuerpo de la peticion
			Document doc = builder.parse(new InputSource(new StringReader(response.readEntity(String.class))));
			// Devuelve el resultado de convertir la informacion de formato XML a un vector de cadenas
			return XMLASolucion(doc);
		}
		else { // Estado de error en la respuesta
			response.close();
			if (estado == 404)	// 404 = NOT FOUND
		        throw new NotFoundException("Error: Partida " + 
		                       targetPartida.getUri() +  " no encontrada" );
		    else
		        throw new WebApplicationException("Error al intentar la solucion de la partida " + 
                       targetPartida.getUri() );
		}
	}
	
	/**
	 * Procesa un Document XML y lo convierte en la solucion de la partida
	 * @return			vector de cadenas con la informacion de cada barco
	 */
	protected String[] XMLASolucion(Document doc) {
		int numBarcos=0;
		Element root = doc.getDocumentElement(); // Accede a la etiqueta raiz: 'solucion'
		// Obtiene el numero de barcos del atributo 'tam'
		if (root.getAttribute("tam") != null && !root.getAttribute("tam").trim().equals(""))
			numBarcos = Integer.valueOf(root.getAttribute("tam"));
		// Accede a la informacion de los barcos a partir de las etiquetas 'barco'
		// y la almacena en el vector de cadenas a devolver
		NodeList nodes = root.getChildNodes();
		String[] solucion = new String[numBarcos];
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			if (element.getTagName().equals("barco")) {
				solucion[i] = element.getTextContent();
			}
			else System.out.println("[getSolucion: ] Error en el nombre de la etiqueta");
		}
		return solucion;
	}


} // fin clase
