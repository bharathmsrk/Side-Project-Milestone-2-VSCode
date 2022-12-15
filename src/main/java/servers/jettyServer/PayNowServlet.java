package servers.jettyServer;
import jdbc.DatabaseHandler;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class PayNowServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        String username = (String) request.getSession().getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);
        if (username == null) {
            response.sendRedirect("/login");
        }

        String hotelId = request.getParameter("hotelId");
        hotelId = StringEscapeUtils.escapeHtml4(hotelId);

        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        Template template = ve.getTemplate("templates/payNowBootstrap.html");

        context.put("title", "Payment Page");
        context.put("formaction", "Please enter payment details.");
        context.put("action", request.getServletPath());
        context.put("button", "PAY NOW");

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        String username = (String) request.getSession().getAttribute("username");
        username = StringEscapeUtils.escapeHtml4(username);

        dbHandler.deleteItems(username);
        dbHandler.deleteCheckoutUsers(username);

        File input = new File("output/CustomerCheckoutList.csv");
        try {
            CSVReader reader = new CSVReader(new FileReader(input));
            String[] nextRecord;
            List<String[]> newData = new ArrayList<String[]>();
            while ((nextRecord = reader.readNext()) != null) {
                if(nextRecord[0].equals(username)){
                    continue;
                }
                newData.add(nextRecord);
                System.out.println();
            }

            CSVWriter writer = new CSVWriter(new FileWriter(input));
            writer.writeAll(newData);
            writer.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }

        response.sendRedirect("/landingpage");
    }

}
