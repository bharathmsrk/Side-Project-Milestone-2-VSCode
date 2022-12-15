package servers.jettyServer;
import jdbc.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        String username = (String) request.getSession().getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);
        String lastlogin = (String) request.getSession().getAttribute("lastlogin");
        lastlogin = StringEscapeUtils.escapeHtml4(lastlogin);

        HttpSession session = request.getSession();
        dbHandler.addLastLogin(username, lastlogin);
        session.invalidate();

        response.sendRedirect("/login");
    }
}
