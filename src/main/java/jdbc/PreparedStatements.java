package jdbc;

public class PreparedStatements {
    public static final String CREATE_CUSTOMERS_TABLE =
            "CREATE TABLE customers (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    public static final String CREATE_HISTORY_TABLE =
            "CREATE TABLE history (" +
                        "username VARCHAR(100) NOT NULL, " +
                        "id VARCHAR(100) NOT NULL, " +
                        "brand VARCHAR(100) NOT NULL, " +
                        "name VARCHAR(500) NOT NULL, " +
                        "price VARCHAR(500) NOT NULL, " +
                        "date VARCHAR(500) NOT NULL);";

    public static final String INSERT_HISTORY_SQL = 
            "INSERT INTO history (username, id, brand, name, price, date) " +
                                "VALUES (?, ?, ?, ?, ?, ?);";
    
    public static final String GET_HISTORY_SQL = 
            "SELECT * FROM history WHERE username = ?;";
    
    public static final String DELETE_HISTORY_SQL = 
            "DELETE FROM history WHERE username = ?;";

    public static final String CREATE_ITEMS_TABLE =
            "CREATE TABLE items (" +
                        "id VARCHAR(100) PRIMARY KEY, " +
                        "num VARCHAR(100) NOT NULL, " +
                    "brand VARCHAR(100) NOT NULL, " +
                    "name VARCHAR(500) NOT NULL, " +
                    "price VARCHAR(100) NOT NULL);";
    
    public static final String CREATE_CHECKOUT_TABLE =
            "CREATE TABLE checkout (" +
                    "username VARCHAR(100) NOT NULL UNIQUE, " +
                    "time VARCHAR(100) NOT NULL);";

    public static final String GET_CHECKOUT_SQL = 
            "SELECT * FROM checkout WHERE username = ?;";

    public static final String INSERT_CHECKOUT_SQL = 
                "INSERT INTO checkout (username, time) " +
                        "VALUES (?, ?);";

    public static final String CREATE_USERITEM_TABLE =
                "CREATE TABLE useritem (" +
                        "username VARCHAR(100) NOT NULL, " +
                        "itemid INTEGER NOT NULL);";

    public static final String GET_USERITEM_SQL = 
                "SELECT * FROM useritem WHERE username = ?;";

    public static final String INSERT_USERITEM_SQL = 
                "INSERT INTO useritem (username, itemid) " +
                        "VALUES (?, ?);";
    
    public static final String DELETE_USERITEM_SQL = 
                "DELETE FROM useritem WHERE username = ?;";

    public static final String DELETE_CHECKOUT_SQL = 
                "DELETE FROM checkout WHERE username = ?;";

    public static final String LOAD_ITEMS_SQL =
            "LOAD DATA LOCAL INFILE ? " +
                "INTO TABLE items " +
                "FIELDS TERMINATED BY ',' " +
                "IGNORE 1 ROWS;";

    public static final String INSERT_ITEM_SQL = 
            "INSERT INTO items (id, num, brand, name, price) " +
                "VALUES (?, ?, ?, ?, ?);";

    public static final String GET_ITEM_SQL = 
            "SELECT * FROM items WHERE id = ?;";

    public static final String REGISTER_SQL =
            "INSERT INTO customers (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";

    public static final String USER_SQL =
            "SELECT username FROM customers WHERE username = ?;";

    public static final String SALT_SQL =
            "SELECT usersalt FROM customers WHERE username = ?;";

    public static final String AUTH_SQL =
            "SELECT username FROM customers " +
                    "WHERE username = ? AND password = ?;";

    public static final String CREATE_LASTLOGIN_TABLE =
            "CREATE TABLE lastlogin (" +
                    "username VARCHAR(100) NOT NULL UNIQUE, " +
                    "logintime VARCHAR(100) NOT NULL);";

    public static final String ADD_LASTLOGIN_SQL =
            "INSERT INTO lastlogin (username, logintime) " +
                    "VALUES (?, ?);";

    public static final String UPDATE_LASTLOGIN_SQL =
            "UPDATE lastlogin SET logintime = ? WHERE username = ?;";

    public static final String GET_LASTLOGIN_SQL =
            "SELECT logintime FROM lastlogin WHERE username = ?;";

}
