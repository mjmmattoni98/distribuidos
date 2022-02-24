<%@ page language="java" contentType="text/html;charset=UTF-8"
    pageEncoding="UTF-8" import="modelo.Partida"%>
<!DOCTYPE html>
<html>
<head>
<style>
	form {
		text-align: center;
		margin: 0 auto;
	}
	table {
		width: 50%;
		margin: 0 auto;
	}
	td.rojo{
		background-color:red;
	}
	td.azul{
		background-color:blue;
	}
	td.amarillo{
		background-color:yellow;
	}
</style>
<meta charset="UTF-8">
<title>Hundir la flota</title>
</head>
<body>
	<h1>Hundir la flota</h1><br><br>
	
	<% 
	HttpSession s = request.getSession();
	Partida partida = (Partida) session.getAttribute("partida");
	String mensaje = (String) session.getAttribute("mensaje");
	int disparos = partida.getDisparos();
	int quedan = partida.getBarcosPorHundir();
	int numBarcos = partida.getNumBarcos();
	%>
	<%= mensaje%><br>
	Barcos navegando: <%= partida.getBarcosPorHundir()%> Barcos hundidos: <%= numBarcos - quedan%> <br>
	Número de disparos efectuados: <%= disparos%>
	<form action="HundirFlotaServlet" method="GET">
	<table>
		<tr>
		<td></td>
		<%for(int j = 0; j < partida.getColumnas(); j++){%>
			<td>
			<%=Character.toString((char) ('A' + j)) %>
			</td>
		<%}%>
		</tr>
		<%for(int i = 0; i < partida.getFilas(); i++){%>
			<tr>
			<%
			for(int j = 0; j <= partida.getColumnas(); j++){
				if(j == 0){%>
					<td><%=Integer.toString(i+1) %></td>
				<%}
				else{
					String color = "";
					int estado = partida.getCasilla(i, j-1);
					if(estado == -1)
						color = "azul";
					else if(estado == -2)
						color = "amarillo";
					else if(estado == -3)
						color = "rojo";
					String posicion = i + "" + Character.toString((char)('A'+j-1));				
					if(partida.casillaDisparada(i, j-1)){%>
						<td class=<%=color %>><input type="radio" name="casilla" value=<%=posicion %>></td>
					<%}else{ %>
						<td><input type="radio" name="casilla" value=<%=posicion %>></td>
					<%} 
				 }
			}%>
			</tr>
		<%}%>
	</table>
	<%if(quedan == 0){ %>
		<input class="boton" type="submit" value="Submit" disabled><br>
	<%}else{ %>
		<input class="boton" type="submit" value="Submit"><br>
	<%} %>
	
	</form>
	
	<a href="SolucionPartidaServlet">Muestra solución</a><br>
	<a href="NuevaPartidaServlet">Nueva partida</a><br>
	<a href="SalirPartidaServlet">Salir</a>
	
</body>
</html>