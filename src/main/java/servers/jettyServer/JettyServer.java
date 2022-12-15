package servers.jettyServer;
import jdbc.DatabaseHandler;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.http.HttpServlet;
import java.util.Map;

public class JettyServer {
    private static final int PORT = 8060;
    private Map<String, HttpServlet> handlers;

    public JettyServer(Map<String, HttpServlet> handlers) {
        this.handlers = handlers;
    }

    public void start() throws Exception {

        Server server = new Server(PORT);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        for (String path: handlers.keySet()) {
            handler.addServlet(new ServletHolder(handlers.get(path)), path);
        }

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        handler.setAttribute("templateEngine", velocity);
        server.setHandler(handler);
        try {
            server.start();
            server.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
