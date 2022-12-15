package servers.jettyServer;
import jdbc.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class RegistrationServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String username = (String) request.getSession().getAttribute("username");
		username = StringEscapeUtils.escapeHtml4(username);

		if (username == null) {
			VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
			VelocityContext context = new VelocityContext();
			Template template = ve.getTemplate("templates/signUpBootstrap.html");

			context.put("title", "Registration");
			context.put("formaction", "Register your account!");
			context.put("action", request.getServletPath());
			context.put("button", "Register");
			context.put("button2", "Back to Login");
			if(request.getParameter("user") != null) {
				context.put("message", "Username already exists");
				// display items and PAY button, remove from table
			}
			if(request.getParameter("invalid") != null) {
				context.put("message", "Invalid password format");
			}

			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			out.println(writer);
		} else {
			response.sendRedirect("/landingpage");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		DatabaseHandler dbHandler = DatabaseHandler.getInstance();

		String usernameParam = request.getParameter("username");
		usernameParam = StringEscapeUtils.escapeHtml4(usernameParam);
		String password = request.getParameter("pass");
		password = StringEscapeUtils.escapeHtml4(password);

		if (request.getParameter("button") != null) {
			if (dbHandler.checkUser(usernameParam)) {
				response.sendRedirect("/register?user=true");
			} else if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()_+].*")) {
				response.sendRedirect("/register?invalid=true");
			} else {
				dbHandler.registerUser(usernameParam, password);
				response.sendRedirect("/login?newuser=true");
			}
		}
		else{
			response.sendRedirect("/login");
		}
	}
}