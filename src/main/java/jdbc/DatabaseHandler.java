package jdbc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Random;
import mainScript.*;

public class DatabaseHandler {

    private static DatabaseHandler dbHandler = new DatabaseHandler("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();

    private DatabaseHandler(String propertiesFile){
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        System.out.println(this.uri);
    }

    public static DatabaseHandler getInstance() {
        return dbHandler;
    }

    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    public void createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            // statement.executeUpdate(PreparedStatements.CREATE_CUSTOMERS_TABLE);
            // statement.executeUpdate(PreparedStatements.CREATE_ITEMS_TABLE);
            // statement.executeUpdate(PreparedStatements.CREATE_CHECKOUT_TABLE);
            // statement.executeUpdate(PreparedStatements.CREATE_USERITEM_TABLE);
            statement.executeUpdate(PreparedStatements.CREATE_HISTORY_TABLE);
        }
        catch (SQLException ex) {
             System.out.println(ex);
        }
    }

    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }

        return hashed;
    }

    public void registerUser(String newuser, String newpass) {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32); 
        String passhash = getHash(newpass, usersalt);
        System.out.println(usersalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            try {
                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean checkUser(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.USER_SQL);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }

    public void addItemsDB() {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("dbConnection successful");
            statement = connection.prepareStatement(PreparedStatements.LOAD_ITEMS_SQL);
            statement.setString(1, "/Users/bharathradhakrishnan/side-project-bharathmsrk/input/ModifiedGroceryList.csv");
            // statement.setLocalInfileInputStream(1);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addItemLoopDB() {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            Scanner sc = new Scanner(new File("/Users/bharathradhakrishnan/side-project-bharathmsrk/input/ModifiedGroceryList.csv"));
            if(sc.hasNext()) {
                sc.nextLine();
            }
            while (sc.hasNext()) {
                String entry = sc.nextLine();
                String[] entry_vals = entry.split(",",5);
                statement = connection.prepareStatement(PreparedStatements.INSERT_ITEM_SQL);
                statement.setString(1, entry_vals[0]);
                statement.setString(2, entry_vals[1]);
                statement.setString(3, entry_vals[2]);
                statement.setString(4, entry_vals[3]);
                statement.setString(5, entry_vals[4]);
                statement.executeUpdate();
                statement.close();
            }
            sc.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Item getItem(String id) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_ITEM_SQL);
            statement.setString(1, id);
            ResultSet results = statement.executeQuery();
            Item item = null;
            if (results.next()) {
                item = new Item(results.getString("id"),results.getString("brand"),results.getString("name"),results.getString("price"));
            }
            return item;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public void addCheckoutDB() {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            Scanner sc = new Scanner(new File("/Users/bharathradhakrishnan/side-project-bharathmsrk/output/CustomerCheckoutList.csv"));
            sc.useDelimiter("\n");
            if(sc.hasNext()) {
                sc.next();
            }
            while (sc.hasNext()) {
                String entry = sc.next();
                if (entry.isBlank()) continue;
                String[] entry_vals = entry.split(",",2);
                ArrayList<Integer> currentValue = getCheckout(entry_vals[0]);
                if (currentValue == null || currentValue.isEmpty()) {
                    Random rand = new Random();
                    int upperbound = 10000;
                    for(int i=0; i<8; i++) {
                        statement = connection.prepareStatement(PreparedStatements.INSERT_USERITEM_SQL);
                        int randId = rand.nextInt(upperbound);
                        statement.setString(1, entry_vals[0]);
                        statement.setInt(2, randId);
                        statement.executeUpdate();
                        statement.close();
                        statement = connection.prepareStatement(PreparedStatements.INSERT_HISTORY_SQL);
                        Item item = getItem(String.valueOf(randId));
                        statement.setString(1, entry_vals[0]);
                        statement.setString(2, String.valueOf(randId));
                        statement.setString(3, item.getBrand());
                        statement.setString(4, item.getName());
                        statement.setString(5, String.valueOf(item.getPrice()));
                        statement.setString(6, entry_vals[1]);
                        statement.executeUpdate();
                        statement.close();
                    }
                    statement = connection.prepareStatement(PreparedStatements.INSERT_CHECKOUT_SQL);
                    statement.setString(1, entry_vals[0]);
                    statement.setString(2, entry_vals[1]);
                    statement.executeUpdate();
                    statement.close();
                }
            }
            sc.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<Item> getHistory(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_HISTORY_SQL);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            List<Item> itemList = new ArrayList<>();
            while (results.next()) {
                Item item = new Item(results.getString("id"),results.getString("brand"),results.getString("name"),results.getString("price"));
                item.setDate(results.getString("date"));
                itemList.add(item);
            }
            return itemList;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public void deleteHistory(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.DELETE_HISTORY_SQL);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    public ArrayList<Integer> getCheckout(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_USERITEM_SQL);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            ArrayList<Integer> items = new ArrayList<>();
            while (results.next()) {
                items.add(results.getInt("itemid"));
            }
            return items;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public void deleteCheckoutUsers(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.DELETE_CHECKOUT_SQL);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteItems(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.DELETE_USERITEM_SQL);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public void addLastLogin(String username, String loginTime) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            String currentValue = getLastLogin(username);
            if (currentValue == null || currentValue.equals("")) {
                statement = connection.prepareStatement(PreparedStatements.ADD_LASTLOGIN_SQL);
                statement.setString(1, username);
                statement.setString(2, loginTime);
                statement.executeUpdate();
                statement.close();
            }
            else {
                statement = connection.prepareStatement(PreparedStatements.UPDATE_LASTLOGIN_SQL);
                statement.setString(1, loginTime);
                statement.setString(2, username);
                statement.executeUpdate();
                statement.close();
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    public String getLastLogin(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.GET_LASTLOGIN_SQL);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            String lastLogin = "";
            if (results.next()) {
                lastLogin = results.getString("logintime");
            }
            return lastLogin;
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

}

