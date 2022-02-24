<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="modelo.Partida"%>
<!DOCTYPE html>
<html>
<head>
<style>
	td.rojo{
		background-color:red;
	}
	td.azul{
		background-color:blue;
	}
	table {
		text-align: center;
		margin: 0 auto;
		width: 50%;
	}
</style>

<meta charset="UTF-8">
<title>Solucion hundir la flota</title>
</head>
<body>
	<h1>Hundir la flota</h1><br><br>
	
	<% 
	HttpSession s = request.getSession();
	Partida partida = (Partida) session.getAttribute("partida");
	String[] barcos = partida.getSolucion();
	int filas = partida.getFilas();
	int columnas = partida.getColumnas();
	int disparos = partida.getDisparos();
	%>
	Solucion PARTIDA <br>
	Barcos navegando: <%= partida.getBarcosPorHundir()%> Barcos hundidos: <%= partida.getNumBarcos() - partida.getBarcosPorHundir()%> <br>
	Número de disparos efectuados: <%= disparos%>
	<%
	boolean[][] esBarco = new boolean[filas][columnas]; //Matriz para saber donde están los barcos. true si hay barco y false si no hay
	for(String cadenaBarco : barcos){
		String[] barco = cadenaBarco.split("#");
		int filaInicial = Integer.parseInt(barco[0]); 
		int columnaInicial = Integer.parseInt(barco[1]); 
		String orientacion = barco[2];
		int tamanyo = Integer.parseInt(barco[3]); 
		for(int i = 0; i < tamanyo; i++) {
			esBarco[filaInicial][columnaInicial] = true;
			if(orientacion.equals("H"))  columnaInicial++; //Orientación horizontal
			else filaInicial++; //Orientación vertical
		}
	}
	%>
	<table>
		<tr>
		<td></td>
		<%for(int j = 0; j < columnas; j++){%>
			<td>
			<%=Character.toString((char) ('A' + j)) %>
			</td>
		<%}%>
		</tr>
		<%for(int i = 0; i < filas; i++){%>
			<tr>
			<%
			for(int j = 0; j <= columnas; j++){
				if(j == 0){%>
					<td><%=Integer.toString(i+1) %></td>
				<%}
				else{ //Pintamos dependiendo si hay barco o no
					if(esBarco[i][j-1]){%>
						<td class="rojo"></td>
					<%}else{ %>
						<td class="azul"></td>
					<%} %>
				<% }%>
			<%}%>
			</tr>
		<%}%>
	</table>
	
	<a href="NuevaPartidaServlet">Nueva partida</a><br>
	<a href="SalirPartidaServlet">Salir</a>
	
</body>
</html>