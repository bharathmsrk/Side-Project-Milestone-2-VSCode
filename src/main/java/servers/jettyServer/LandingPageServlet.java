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

public class LandingPageServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        String username = (String) request.getSession().getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);
        if (username == null) {
            response.sendRedirect("/login");
        }

        try{
            VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
            VelocityContext context = new VelocityContext();
            Template template = ve.getTemplate("templates/landingPageBootstrap.html");

            context.put("title", "Home Page");
            context.put("lastlogin", dbHandler.getLastLogin(username));
            
            ArrayList<Integer> productsId = dbHandler.getCheckout(username);
            if (productsId == null || productsId.isEmpty()) {
                context.put("message","No products in bag, you don't have dues!");
            }
            else {
                context.put("message","Please pay your dues before shopping again.");
                ArrayList<Item> productsBought = new ArrayList<>();
                float totalPayable = 0;
                for(int id: productsId) {
                    Item item = dbHandler.getItem(String.valueOf(id));
                    productsBought.add(item);
                    totalPayable += item.getPrice();
                }
                context.put("products",productsBought);
                context.put("due",totalPayable);
            }

            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer);
        } catch (Exception e) {
            out.println(e);
        }
    }

}
