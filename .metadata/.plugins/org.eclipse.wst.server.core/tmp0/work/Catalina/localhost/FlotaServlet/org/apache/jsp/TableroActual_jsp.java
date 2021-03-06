/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/9.0.27
 * Generated at: 2019-12-23 22:12:21 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import modelo.Partida;

public final class TableroActual_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent,
                 org.apache.jasper.runtime.JspSourceImports {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private static final java.util.Set<java.lang.String> _jspx_imports_packages;

  private static final java.util.Set<java.lang.String> _jspx_imports_classes;

  static {
    _jspx_imports_packages = new java.util.HashSet<>();
    _jspx_imports_packages.add("javax.servlet");
    _jspx_imports_packages.add("javax.servlet.http");
    _jspx_imports_packages.add("javax.servlet.jsp");
    _jspx_imports_classes = new java.util.HashSet<>();
    _jspx_imports_classes.add("modelo.Partida");
  }

  private volatile javax.el.ExpressionFactory _el_expressionfactory;
  private volatile org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public java.util.Set<java.lang.String> getPackageImports() {
    return _jspx_imports_packages;
  }

  public java.util.Set<java.lang.String> getClassImports() {
    return _jspx_imports_classes;
  }

  public javax.el.ExpressionFactory _jsp_getExpressionFactory() {
    if (_el_expressionfactory == null) {
      synchronized (this) {
        if (_el_expressionfactory == null) {
          _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
        }
      }
    }
    return _el_expressionfactory;
  }

  public org.apache.tomcat.InstanceManager _jsp_getInstanceManager() {
    if (_jsp_instancemanager == null) {
      synchronized (this) {
        if (_jsp_instancemanager == null) {
          _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
        }
      }
    }
    return _jsp_instancemanager;
  }

  public void _jspInit() {
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
      throws java.io.IOException, javax.servlet.ServletException {

    if (!javax.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {
      final java.lang.String _jspx_method = request.getMethod();
      if ("OPTIONS".equals(_jspx_method)) {
        response.setHeader("Allow","GET, HEAD, POST, OPTIONS");
        return;
      }
      if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method)) {
        response.setHeader("Allow","GET, HEAD, POST, OPTIONS");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "JSPs only permit GET, POST or HEAD. Jasper also permits OPTIONS");
        return;
      }
    }

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<style>\r\n");
      out.write("\tform {\r\n");
      out.write("\t\ttext-align: center;\r\n");
      out.write("\t\tmargin: 0 auto;\r\n");
      out.write("\t}\r\n");
      out.write("\ttable {\r\n");
      out.write("\t\twidth: 50%;\r\n");
      out.write("\t\tmargin: 0 auto;\r\n");
      out.write("\t}\r\n");
      out.write("\ttd.rojo{\r\n");
      out.write("\t\tbackground-color:red;\r\n");
      out.write("\t}\r\n");
      out.write("\ttd.azul{\r\n");
      out.write("\t\tbackground-color:blue;\r\n");
      out.write("\t}\r\n");
      out.write("\ttd.amarillo{\r\n");
      out.write("\t\tbackground-color:yellow;\r\n");
      out.write("\t}\r\n");
      out.write("</style>\r\n");
      out.write("<meta charset=\"UTF-8\">\r\n");
      out.write("<title>Hundir la flota</title>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");
      out.write("\t<h1>Hundir la flota</h1><br><br>\r\n");
      out.write("\t\r\n");
      out.write("\t");
 
	HttpSession s = request.getSession();
	Partida partida = (Partida) session.getAttribute("partida");
	String mensaje = (String) session.getAttribute("mensaje");
	int disparos = partida.getDisparos();
	int quedan = partida.getBarcosPorHundir();
	int numBarcos = partida.getNumBarcos();
	
      out.write('\r');
      out.write('\n');
      out.write('	');
      out.print( mensaje);
      out.write("<br>\r\n");
      out.write("\tBarcos navegando: ");
      out.print( partida.getBarcosPorHundir());
      out.write(" Barcos hundidos: ");
      out.print( numBarcos - quedan);
      out.write(" <br>\r\n");
      out.write("\tN??mero de disparos efectuados: ");
      out.print( disparos);
      out.write("\r\n");
      out.write("\t<form action=\"HundirFlotaServlet\" method=\"GET\">\r\n");
      out.write("\t<table>\r\n");
      out.write("\t\t<tr>\r\n");
      out.write("\t\t<td></td>\r\n");
      out.write("\t\t");
for(int j = 0; j < partida.getColumnas(); j++){
      out.write("\r\n");
      out.write("\t\t\t<td>\r\n");
      out.write("\t\t\t");
      out.print(Character.toString((char) ('A' + j)) );
      out.write("\r\n");
      out.write("\t\t\t</td>\r\n");
      out.write("\t\t");
}
      out.write("\r\n");
      out.write("\t\t</tr>\r\n");
      out.write("\t\t");
for(int i = 0; i < partida.getFilas(); i++){
      out.write("\r\n");
      out.write("\t\t\t<tr>\r\n");
      out.write("\t\t\t");

			for(int j = 0; j <= partida.getColumnas(); j++){
				if(j == 0){
      out.write("\r\n");
      out.write("\t\t\t\t\t<td>");
      out.print(Integer.toString(i+1) );
      out.write("</td>\r\n");
      out.write("\t\t\t\t");
}
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
					if(partida.casillaDisparada(i, j-1)){
      out.write("\r\n");
      out.write("\t\t\t\t\t\t<td class=");
      out.print(color );
      out.write("><input type=\"radio\" name=\"casilla\" value=");
      out.print(posicion );
      out.write("></td>\r\n");
      out.write("\t\t\t\t\t");
}else{ 
      out.write("\r\n");
      out.write("\t\t\t\t\t\t<td><input type=\"radio\" name=\"casilla\" value=");
      out.print(posicion );
      out.write("></td>\r\n");
      out.write("\t\t\t\t\t");
} 
				 }
			}
      out.write("\r\n");
      out.write("\t\t\t</tr>\r\n");
      out.write("\t\t");
}
      out.write("\r\n");
      out.write("\t</table>\r\n");
      out.write("\t");
if(quedan == 0){ 
      out.write("\r\n");
      out.write("\t\t<input class=\"boton\" type=\"submit\" value=\"Submit\" disabled><br>\r\n");
      out.write("\t");
}else{ 
      out.write("\r\n");
      out.write("\t\t<input class=\"boton\" type=\"submit\" value=\"Submit\"><br>\r\n");
      out.write("\t");
} 
      out.write("\r\n");
      out.write("\t\r\n");
      out.write("\t</form>\r\n");
      out.write("\t\r\n");
      out.write("\t<a href=\"SolucionPartidaServlet\">Muestra soluci??n</a><br>\r\n");
      out.write("\t<a href=\"NuevaPartidaServlet\">Nueva partida</a><br>\r\n");
      out.write("\t<a href=\"SalirPartidaServlet\">Salir</a>\r\n");
      out.write("\t\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
