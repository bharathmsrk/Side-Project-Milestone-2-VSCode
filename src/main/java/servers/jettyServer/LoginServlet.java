package servers.jettyServer;
import jdbc.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String username = (String) request.getSession().getAttribute("username");
		username = StringEscapeUtils.escapeHtml4(username);
		
		if (username == null) {
			VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
			VelocityContext context = new VelocityContext();
			Template template = ve.getTemplate("templates/signUpBootstrap.html");

			context.put("title", "Login");
			context.put("formaction", "Sign into your account");
			context.put("action", request.getServletPath());
			context.put("button", "Login");
			context.put("button2", "Sign Up Now");
			if(request.getParameter("invalid") != null) {
				context.put("message", "Invalid username or password");
			}
			if(request.getParameter("newuser") != null) {
				context.put("message", "Successfully registered the user. Please login now. ");
			}

			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			out.println(writer);
		} else {
			response.sendRedirect("/landingpage");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		DatabaseHandler dbHandler = DatabaseHandler.getInstance();

		String user = request.getParameter("username");
		String pass = request.getParameter("pass");

		boolean flag = dbHandler.authenticateUser(user, pass);

		if (flag & request.getParameter("button") != null) {
			HttpSession session = request.getSession();
			session.setAttribute("username", user);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'kk:mm:ss");
			java.util.Date date = new Date();
			String lastlogin = dateFormat.format(date);
			session.setAttribute("lastlogin", lastlogin);
			response.sendRedirect("/login?username=" + user);
		}
		else if(request.getParameter("button") != null){
			response.sendRedirect("/login?invalid=true");
		}
		else{
			response.sendRedirect("/register");
		}
	}
}