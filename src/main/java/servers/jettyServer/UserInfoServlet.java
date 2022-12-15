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
public class UserInfoServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String username = (String) request.getSession().getAttribute("username");
		username = StringEscapeUtils.escapeHtml4(username);
		
		if (username == null) {
			response.sendRedirect("/login");
			
		} else {
			VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
			VelocityContext context = new VelocityContext();
			Template template = ve.getTemplate("templates/userProfile.html");

			context.put("title", "User Profile");
			context.put("formaction", "Hey, view your infomation here");
			context.put("action", request.getServletPath());
			context.put("username", username);

			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			out.println(writer);
		}
	}

}