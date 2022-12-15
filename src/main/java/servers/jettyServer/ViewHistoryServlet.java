package servers.jettyServer;
import jdbc.DatabaseHandler;
import mainScript.Item;
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
import java.util.ArrayList;
import java.util.List;

public class ViewHistoryServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        String username = (String) request.getSession().getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);

        if (username == null) {
            response.sendRedirect("/login");
        } else {
            VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
            VelocityContext context = new VelocityContext();
            Template template = ve.getTemplate("templates/viewHistoryBootstrap.html");

            context.put("title", "My History");
            List<Item> productsHistory = dbHandler.getHistory(username);
            if (productsHistory == null || productsHistory.isEmpty()) {
                context.put("message","No products in your history.");
            }
            else {
                context.put("products",productsHistory);
                context.put("lastlogin", dbHandler.getLastLogin(username));

            }
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        String username = (String) request.getSession().getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);

        dbHandler.deleteHistory(username);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/viewHistoryBootstrap.html");

        context.put("title", "My History");
        context.put("message","No products in your history.");
        context.put("lastlogin", dbHandler.getLastLogin(username));

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

}
