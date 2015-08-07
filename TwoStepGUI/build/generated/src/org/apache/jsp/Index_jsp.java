package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class Index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

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
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
                
        HttpSession _session = request.getSession(true);
        
        Object gameSessionState = session.getAttribute("user_session_state");     
        
        if (gameSessionState != null)
        {
           response.sendRedirect("UserWebLogic");
        }        

      out.write("\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta name=\"viewport\" content=\"initial-scale = 0.5, user-scalable=yes\"  />\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("        <title>Logging page</title>\n");
      out.write("        <link rel=\"stylesheet\" type=\"text/css\" href=\"css/special2.css\">\n");
      out.write("        <script type=\"text/javascript\" src=\"scripts/helper.js\"></script>\n");
      out.write("    </head>\n");
      out.write("    <body>    \n");
      out.write("    <div class=\"container\">\t\n");
      out.write("\t<h1>Logging Page.</h1><hr>\n");
      out.write("\t\t<form name=\"myform\" action=\"UserWebLogic\" method=\"post\" onsubmit=\"return false;\">                      \t\n");
      out.write("\t\t\t<div>\n");
      out.write("                            <input type=\"text\" placeholder=\"UserID\" required=\"\" name=\"uname\" id=\"uname\" style=\"width:500px\"/>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t\t<div>\n");
      out.write("                            <input type=\"password\" placeholder=\"Password\" required=\"\" name=\"password\" id=\"password\" style=\"width:500px\"/>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t\t<div>                            \n");
      out.write("                            <input type=\"submit\" value=\"Log in\" onclick=\"login_validation1()\" style=\"width:100px;height:45px\"/>\n");
      out.write("                            <a href=\"mailto: unsupported@email.com\">Forgot your password?</a>\n");
      out.write("                            <a href=\"register.jsp\">Register</a>                            \n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t</form>\t\t\n");
      out.write("    </div>\t\n");
      out.write("</body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
