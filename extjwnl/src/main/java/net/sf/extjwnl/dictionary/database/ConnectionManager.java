package net.sf.extjwnl.dictionary.database;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection manager for database-backed dictionaries.
 *
 * @author John Didion <jdidion@didion.net>
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ConnectionManager {

    private final Dictionary dictionary;
    private String driverClass;
    private String url;
    private String userName;
    private String password;
    private boolean registered;
    private Connection connection;
    private String jndi;

    public ConnectionManager(Dictionary dictionary, String driverClass, String url, String userName, String password) {
        this.dictionary = dictionary;
        this.driverClass = driverClass;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public ConnectionManager(Dictionary dictionary, String jndi) {
        this.dictionary = dictionary;
        this.jndi = jndi;
    }


    public Query getQuery(String sql) throws SQLException, JWNLException {
        return new Query(dictionary, sql, getConnection());
    }

    public Connection getConnection() throws SQLException, JWNLException {
        if (null != connection) {
            return connection;
        }
        if (jndi != null) {
            try {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup(jndi);
                if (ds != null) {
                    connection = ds.getConnection();
                    connection.setReadOnly(true);
                    return connection;
                }
            } catch (NamingException ne) {
                throw new JWNLException(dictionary.getMessages().resolveMessage("JNDI_NAMING_EXCEPTION", jndi));
            }
        }
        registerDriver();
        if (userName == null) {
            connection = DriverManager.getConnection(url);
            connection.setReadOnly(true);
            return connection;
        } else {
            connection = DriverManager.getConnection(url, userName, (password != null) ? password : "");
            connection.setReadOnly(true);
            return connection;
        }
    }

    private void registerDriver() throws JWNLException {
        if (!registered) {
            try {
                Driver driver = (Driver) Class.forName(driverClass).newInstance();
                DriverManager.registerDriver(driver);
                registered = true;
            } catch (Exception e) {
                throw new JWNLException(dictionary.getMessages().resolveMessage("DICTIONARY_EXCEPTION_024"), e);
            }
        }
    }

    public void close() {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                //nop
            }
            connection = null;
        }
    }
}