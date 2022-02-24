package servicios;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import modelo.Contacto;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("agenda")
public class RecursoAgenda {
	// Diccionario que guarda los contactos en la agenda
	private Map<Integer, Contacto> agendaDB = new ConcurrentHashMap<Integer, Contacto>();
	// Contador que permite generar identificadores únicos sucesivos para los contactos creados
	private AtomicInteger idCounter = new AtomicInteger();

	/**
	 * Constructor por defecto
	 */	
	public RecursoAgenda() {
		System.out.println("Construyo RecursoAgenda");
	}

	/**
	 * Crea un nuevo contacto y lo almacena en el diccionario
	 * Responde a peticiones POST a la URI {baseURI}
	 * @param	is	InputStream que permite leer el cuerpo del mensaje HTTP conteniendo el Contacto
	 * @return				URI del recurso con la partida recien creada en la cabecera Location
	 */	
	@POST
	@Consumes("text/plain")
	// La información se lee del cuerpo del mensaje HTTP usando un InputStream
	public Response nuevoContacto(InputStream is,
			@Context UriInfo uriInfo) {
				
		// Crea un contacto extrayendolo del cuerpo del mensaje accedido mediante un InputStream
		Contacto contacto = readContacto(is);
				
		int id = idCounter.incrementAndGet();
		agendaDB.put(id, contacto);

		// Construye la respuesta incluyendo la URI absoluta al nuevo recurso partida
		// Obtiene la ruta absoluta de la información de contexto inyectada mediante @Context al método
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		URI newURI = uriBuilder.path("agenda/" + id).build();
		
		System.out.println("Contacto creado: " + contacto + " con URI: " + newURI);

		// created añade el URI proporcionado a la cabecera 'Location'
		ResponseBuilder response = Response.created(newURI);
		// Devuelve el estado 201 indicando que la partida se ha CREATED con éxito
		return response.status(Response.Status.CREATED).build();
	}
	
	/**
	 *  Lee un contacto del cuerpo de una petición HTTP
	 * @param is	InputStrem mediante el que accedemos al cuerpo de la petición
	 * @return 		Contacto leido 
	 */
	protected Contacto readContacto(InputStream is ) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String cadena =  br.readLine();
			String[] componentes = cadena.split("#");
			return new Contacto(componentes[0], componentes[1]);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Borra un contacto del diccionario
	 * Responde a peticiones DELETE a la URI {baseURI}/{idContacto}
	 * @param	idContacto	identificador del contacto
	 * @return				cuerpo vacío y estado indicando si se ha podido borrar el contacto
	 */		
	@DELETE
	@Path("{idContacto}")
	public Response borraContacto(@PathParam("idContacto") int idContacto) {

		if (agendaDB.remove(idContacto) == null) {
			System.out.println("Contacto a borrar no encontrada: " + idContacto);
			// Si el contacto no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Borrado contacto con id: " + idContacto);
			ResponseBuilder builder = Response.ok();
			return builder.build();
		}
	}


	/**
	 * Actualiza contacto
	 * Responde a peticiones PUT a la URI {baseURI}/{idContacto}
	 * @param	idContacto	identificador del contacto
	 * @param	nombre		nuevo nombre del contacto
	 * @param	telefono	nuevo teléfono del contacto
	 * @return				cuerpo vacío y estado indicando si se ha podido actualizar el contacto
	 */
	@PUT
	@Path("{idContacto}")
	// Inyectamos la información sobre el nuevo contacto usando parámetros del "Query string"
	public Response actualizaContacto( @PathParam("idContacto") int idContacto, 
			@QueryParam("nombre") String nombre, 
			@QueryParam("telefono") String telefono)   {
		
		Contacto contacto = agendaDB.get(idContacto);

		if (contacto == null) {
			System.out.println("Contacto a actualizar no encontrado: " + idContacto);
			// Si el contacto no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Actualizo contacto con id: " + idContacto);
			contacto.setNombre(nombre);
			contacto.setTelefono(telefono);
            // Construye y devuelve la respuesta con el cuerpo vacio y el estado OK
			ResponseBuilder builder = Response.ok();
			return builder.build();
		}
	}


	/**
	 * Obtiene los datos de un contacto
	 * Responde a peticiones GET a la URI {baseURI}/{idContacto}
	 * @param	idContacto		identificador del contacto
	 * @return					cuerpo conteniendo la cadena con informacion sobre el contacto "nombre#telefono"
	 */
	@GET
	@Path("{idContacto}")
	@Produces("text/plain")
	public Response getContacto( @PathParam("idContacto") int idContacto)   {

		final Contacto contacto = agendaDB.get(idContacto);

		if (contacto == null) {
			System.out.println("Contacto a leer no encontrado: " + idContacto);
			// Si el contacto no existe devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Leido contacto con id: " + idContacto);
			// Construye y devuelve la respuesta con estado OK
			// e incluyendo en el cuerpo la cadena con el contacto
			ResponseBuilder builder = Response.ok(contacto.toString());
			return builder.build();
		}
	}
	
	@GET
	@Produces("text/plain")
	public Response getListaContactos()   {
		StringBuilder contactos = new StringBuilder();
		
		for(int id : agendaDB.keySet()) {
			Contacto c = agendaDB.get(id);
			contactos.append(id);
			contactos.append(" : " + c + ";");
		}
		
		if (contactos.length() == 0) {
			System.out.println("No hay contactos en la agenda de momento");
			// Si no existen contactos devolvemos una respuesta con estado error NOT_FOUND (404)
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		else {
			System.out.println("Leidos todos los contactos");
			// Construye y devuelve la respuesta con estado OK
			// e incluyendo en el cuerpo la cadena con los contactos
			ResponseBuilder builder = Response.ok(contactos.toString());
			return builder.build();
		}
	}

} // fin de la clase RecursoAgenda
