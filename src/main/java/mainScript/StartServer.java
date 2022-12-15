package mainScript;
import servers.jettyServer.*;
import javax.servlet.http.HttpServlet;
import jdbc.DatabaseHandler;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StartServer {
    public static void main(String[] args) throws Exception {
        runJettyServer();

    }

    public static void runJettyServer() throws Exception {
        DatabaseHandler dhandler = DatabaseHandler.getInstance();
        dhandler.createTable();
        // dhandler.addItemLoopDB();
        dhandler.addCheckoutDB();

        Map<String, HttpServlet> jettyHandlers = new HashMap<>();
        jettyHandlers.put("/register", new RegistrationServlet());
        jettyHandlers.put("/login", new LoginServlet());
        jettyHandlers.put("/logout", new LogoutServlet());
        jettyHandlers.put("/userinfo", new UserInfoServlet());
        jettyHandlers.put("/landingpage", new LandingPageServlet());
        jettyHandlers.put("/paynow", new PayNowServlet());
        jettyHandlers.put("/userhistory", new ViewHistoryServlet());
        jettyHandlers.put("/viewstat", new ViewStatServlet());

        JettyServer jettyServer = new JettyServer(jettyHandlers);
        jettyServer.start();
    }

}

