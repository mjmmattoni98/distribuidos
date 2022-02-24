package servicios;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import modelo.Partida;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("partidas")
public class RecursoPartida {
	// Diccionario con las partidas jugadas por los distintos clientes
	private Map<Integer, Partida> partidaDB = new ConcurrentHashMap<Integer, Partida>();
	// contador para construir los identificadores 煤nicos de las partidas guardadas
	private AtomicInteger idCounter = new AtomicInteger(); 

	/**
	 * Constructor por defecto
	 */	
	public RecursoPartida() {
		super();
		System.out.println("construyo RecursoPartida");
	}

	/**
	 * Crea una nueva partida y la almacena en el diccionario
	 * Responde a una peticion POST a la URI {baseURI}
	 * @param	is			InputStream que da acceso al cuerpo de la peticion
	 * @param	uriInfo		ruta absoluta al nuevo recurso obtenida a partir del Contexto
	 * @return				cuerpo vac铆o y URI del recurso con la partida recien creada en la cabecera Location
	 */	
	@POST
	public Response nuevaPartida(InputStream is, @Context UriInfo uriInfo) {
		int[] datosPartida = leeDatosPartida(is); // Lee numFilas, numColumnas y numBarcos del cuerpo de la peticion
		
		System.out.println("[WS] nuevaPartida. POST con " + datosPartida[0] + " filas " + datosPartida[1] + " columnas y " + datosPartida[2] + " barcos.");
		Partida partida = new Partida(datosPartida[0], datosPartida[1], datosPartida[2]);

		// Asigna a la partida un identificador unico incrementando el atributo idCounter
		int id = idCounter.incrementAndGet();
		partidaDB.put(id, partida);

		// Construye la respuesta incluyendo la URI absoluta al nuevo recurso partida
		// Obtiene la ruta absoluta de la informaci贸n de contexto inyectada mediante @Context al m茅todo
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		URI newURI = uriBuilder.path("partidas/" + id).build();

		// El m茅todo created a帽ade el URI proporcionado a la cabecera 'Location'
		ResponseBuilder response = Response.created(newURI);
		// Devuelve el estado 201 indicando que la partida se ha CREATED con 茅xito
		return response.status(Response.Status.CREATED).build();
	}
	
	/**
	 *  Lee y devuelve los datos de una partida del cuerpo de una petici贸n HTTP
	 * @param is	    InputStrem mediante el que accedemos al cuerpo de la petici贸n
	 * @return			vector con los tres datos para crear una partida [numFilas, numColumnas, numBarcos]
	 */
	private int[] leeDatosPartida(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String cadena =  br.readLine();		
			String[] datos = cadena.split("#");
			int[] res = new int[3];
			res[0] = Integer.parseInt(datos[0]); // numFilas
			res[1] = Integer.parseInt(datos[1]); // numColumnas
			res[2] = Integer.parseInt(datos[2]); // numBarcos
		
			return res;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Borra una partida del diccionario
	 * @param	idPartida	identificador de la partida
	 * @return				cuerpo vac铆o y estado indicando si se ha podido borrar la partida
	 */		
	@DELETE
	@Path("{idPartida}")
	public Response borraPartida(@PathParam("idPartida") int idPartida) {
		if (partidaDB.remove(idPartida) == null) {
			System.out.println("Partida a borrar no encontrada: " + idPartida);
			// Si la partida no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Borrada partida con id: " + idPartida);
			ResponseBuilder builder = Response.ok();
			return builder.build();
		}
	}

	/**
	 * Prueba una casilla, y devuelve el resultado
	 * @param	idPartida	identificador de la partida
	 * @param	fila		fila de la casilla
	 * @param	columna		columna de la casilla
	 * @return				cuerpo conteniendo el resultado de probar la casilla
	 */
	@PUT
	@Path("{idPartida}/casilla")
	@Produces("text/plain")
	public Response pruebaCasilla( @PathParam("idPartida") int idPartida,
			@QueryParam("fila") int fila,
			@QueryParam("columna") int columna)   {
		
		final Partida partida = partidaDB.get(idPartida);

		if (partida == null) {
			System.out.println("Partida a actualizar no encontrada: " + idPartida);
			// Si la partida no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Actualizo casilla de la partida con id: " + idPartida);
			int estadoCasilla = partida.pruebaCasilla(fila, columna);
			// Construye y devuelve la respuesta con el cuerpo vacio y el estado OK
			ResponseBuilder builder = Response.ok(""+estadoCasilla);
			return builder.build();
		}
	}
	

	/**
	 * Obtiene los datos de un barco.
	 * @param	idPartida	identificador de la partida
	 * @param	idBarco		identificador del barco
	 * @return				cuerpo conteniendo la cadena con informacion sobre el barco "fila#columna#orientacion#tamanyo"
	 */
	@GET
	@Path("{idPartida}/barco/{idBarco}")
	@Produces("text/plain")
	public Response getBarco( @PathParam("idPartida") int idPartida,
			@PathParam("idBarco") int idBarco)   {
		
		final Partida partida = partidaDB.get(idPartida);

		if (partida == null) {
			System.out.println("Partida a leer no encontrada: " + idPartida);
			// Si la partida no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Obtenida la informaci髇 del barco de la partida con id: " + idPartida); 
			// Construye y devuelve la respuesta con estado OK
			// e incluyendo en el cuerpo la cadena con el barco
			ResponseBuilder builder = Response.ok(partida.getBarco(idBarco));
			return builder.build();
		}
	}


	/**
	 * Devuelve la informacion sobre todos los barcos
	 * @param	idPartida	identificador de la partida
	 * @return 		cuerpo conteniendo la codificaci贸n XML de la soluci贸n
	 */
	@GET
	@Path("{idPartida}/solucion")
	@Produces("text/plain")
	public Response getSolucion(@PathParam("idPartida") int idPartida) {
		final Partida partida = partidaDB.get(idPartida);

		if (partida == null) {
			System.out.println("Partida a leer no encontrada: " + idPartida);
			// Si la partida no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Obtenida la soluci髇 de la partida con id: " + idPartida);
			// Construye y devuelve la respuesta con estado OK
			// e incluyendo en el cuerpo la cadena con la soluci髇
			String[] solucion = partida.getSolucion();
			ResponseBuilder builder = Response.ok(solucionAXML(solucion, solucion.length));
			return builder.build();
		}
	}

	/**
	 * Construye una cadena con el c贸digo XML que contiene la soluci贸n de la partida
	 * @param solucion	vector de cadenas con la soluci贸n
	 * @param numBarcos	n煤mero de barcos en la partida
	 * @return			cadena con el c贸digo XML conteniendo la soluci贸n
	 */
	protected String solucionAXML(String[] solucion, int numBarcos) {
		StringBuilder str = new StringBuilder();
		// Crea la etiqueta 'solucion' con su atributo 'tam'
		str.append("<solucion tam=\"" + numBarcos + "\">");
		// Anyade las etiquetas correspondientes a cada barco
		for (int i = 0; i < numBarcos; i++) 
			str.append("<barco>" + solucion[i] + "</barco>");
		str.append("</solucion>");
		return str.toString();
	}
}
