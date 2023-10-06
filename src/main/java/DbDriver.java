import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class DbDriver {
    private static final Logger log = LoggerFactory.getLogger("DbDriver");

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        try (InputStream input = new FileInputStream("etc/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            dbUrl = String.format("jdbc:postgresql://%s:%s/url_scanner?targetServerType=primary",
                    prop.getProperty("dbHost"), prop.getProperty("dbPort"));
            dbUser = prop.getProperty("dbUser");
            dbPassword = prop.getProperty("dbPassword");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public DbDriver() {
    }

    // TODO: connection pool, higher-level DB abstraction
    private Connection getConnection() throws SQLException {
        final Properties props = new Properties();
        props.setProperty("user", dbUser);
        props.setProperty("password", dbPassword);
        return DriverManager.getConnection(dbUrl, props);
    }

    public UUID storeScreeenshot(ScreenshotData data) {
        try (Connection connection = getConnection())  {
            try (PreparedStatement st = connection.prepareStatement("INSERT INTO screenshots (filename, content) " +
                    "VALUES (?, ?) RETURNING id;")){
                st.setString(1, data.filename);
                st.setBytes(2, data.content);
                ResultSet resultSet = st.executeQuery();
                resultSet.next();
                String insertedId = resultSet.getString(1);
                return UUID.fromString(insertedId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ScreenshotData fetchScreenshot(UUID id) {
        try (Connection connection = getConnection())  {
            try (PreparedStatement st = connection.prepareStatement("SELECT filename, content FROM screenshots " +
                    "WHERE id = ?::uuid;")){
                st.setString(1, id.toString());
                ResultSet resultSet = st.executeQuery();
                boolean found = resultSet.next();
                if (!found) {
                    return null;
                }

                String filename = resultSet.getString(1);
                byte[] content = resultSet.getBytes(2);
                return new ScreenshotData(filename, content);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public record ScreenshotData(String filename, byte[] content) {}
}
