package modelo;

public class Contacto {	
	/**
	 * Clase para guardar la informacion de un Contacto de la agenda
	 */

	private String nombre,	
				telefono; 
	
	/**
	 * Constructor por defecto. No hace nada
	 */
	public Contacto() { 
		super();
	}
	
	/**
	 * Constructor con argumentos
	 * @param nombre	nombre del contacto
	 * @param telefono	telefono del contacto
	 */
	public Contacto(String nombre, String telefono) {
		super();
		this.nombre = nombre;
		this.telefono = telefono;
	}

	
	@Override
	/**
	 * Convierte los atributos del Contacto en una cadena con formato
	 */
	public String toString() {
		return nombre + "#" + telefono;
	}

	/**
	 * Getter del atributo nombre
	 * @return	nombre del contacto
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Setter para dar valor al atributo nombre
	 * @param nombre	valor a dar al nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Getter del atributo telefono
	 * @return	telefono del contacto
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Setter para dar valor al atributo telefono
	 * @param nombre	valor a dar al telefono
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


} // end class Contacto
