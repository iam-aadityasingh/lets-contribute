package com.charity.model;

import java.sql.*;

public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/charity_event_finder";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "saumya";

    static {
        initializeDatabase();
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }
    }

    public static void initializeDatabase() {
        try ( Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createTablesProcedure =  
                "CREATE PROCEDURE IF NOT EXISTS initialize_tables() " +
                "BEGIN " +
                "   CREATE TABLE IF NOT EXISTS users ( " +
                "       id INT AUTO_INCREMENT PRIMARY KEY, " +
                "       username VARCHAR(50) NOT NULL UNIQUE, " +
                "       password VARCHAR(255) NOT NULL, " +
                "       email VARCHAR(100) NOT NULL UNIQUE, " +
                "       phone VARCHAR(15) " +
                "   ); " +
                "   CREATE TABLE IF NOT EXISTS events ( " +
                "       id INT AUTO_INCREMENT PRIMARY KEY, " +
                "       name VARCHAR(100) NOT NULL, " +
                "       description TEXT, " +
                "       location VARCHAR(150), " +
                "       date DATE NOT NULL, " +
                "       time TIME NOT NULL " +
                "   ); " +
                "   CREATE TABLE IF NOT EXISTS registrations ( " +
                "       id INT AUTO_INCREMENT PRIMARY KEY, " +
                "       user_email VARCHAR(100) NOT NULL, " +
                "       event_id INT NOT NULL, " +
                "       FOREIGN KEY (user_email) REFERENCES users(email), " +
                "       FOREIGN KEY (event_id) REFERENCES events(id) " +
                "   ); " +
                "   CREATE TABLE IF NOT EXISTS audit_log ( " +
                "       id INT AUTO_INCREMENT PRIMARY KEY, " +
                "       action VARCHAR(255) NOT NULL, " +
                "       user_email VARCHAR(100) NOT NULL, " +
                "       old_username VARCHAR(50), " +
                "       new_username VARCHAR(50), " +
                "       old_phone VARCHAR(15), " +
                "       new_phone VARCHAR(15), " +
                "       old_password VARCHAR(255), " +
                "       new_password VARCHAR(255), " +
                "       event_id INT, " +
                "       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                "   ); " +
                "END;"; 

            stmt.execute(createTablesProcedure);
            stmt.execute("CALL initialize_tables();");

            String createEventCountFunction =  
                "CREATE FUNCTION get_event_count(user_email VARCHAR(100)) " +
                "RETURNS INT DETERMINISTIC " +
                "BEGIN " +
                "    DECLARE event_count INT DEFAULT 0; " +
                "    SELECT COUNT(*) INTO event_count FROM registrations WHERE registrations.user_email = user_email; " +
                "    RETURN event_count; " +
                "END;";

            stmt.execute(createEventCountFunction);

            String createCursorFunction =
                "CREATE FUNCTION get_total_counts() RETURNS VARCHAR(255) DETERMINISTIC " +
                "BEGIN " +
                "    DECLARE event_count INT DEFAULT 0; " +
                "    DECLARE place_count INT DEFAULT 0; " +
                "    DECLARE user_count INT DEFAULT 0; " +
                "    DECLARE cur CURSOR FOR " +
                "        SELECT COUNT(*) AS e_count, COUNT(DISTINCT location) AS p_count " +
                "        FROM events; " +
                "    OPEN cur; " +
                "    FETCH cur INTO event_count, place_count; " +
                "    CLOSE cur; " +
                "    SELECT COUNT(*) INTO user_count FROM users; " +
                "    RETURN CONCAT(event_count, ',', place_count, ',', user_count); " +
                "END;";

            stmt.execute(createCursorFunction);

            String createTriggerUserUpdate = 
                "CREATE TRIGGER after_user_update " +
                "AFTER UPDATE ON users " +
                "FOR EACH ROW " +
                "BEGIN " +
                "   INSERT INTO audit_log (action, user_email, old_username, new_username, old_phone, new_phone, old_password, new_password) " +
                "   VALUES ('Updated Profile', NEW.email, OLD.username, NEW.username, OLD.phone, NEW.phone, OLD.password, NEW.password); " +
                "END;";

            stmt.execute(createTriggerUserUpdate);

            String createTriggerRegistrationInsert = 
                "CREATE TRIGGER after_registration_insert " +
                "AFTER INSERT ON registrations " +
                "FOR EACH ROW " +
                "BEGIN " +
                "   INSERT INTO audit_log (action, user_email, event_id) " +
                "   VALUES ('Registered for Event', NEW.user_email, NEW.event_id); " +
                "END;";

            stmt.execute(createTriggerRegistrationInsert);

            String createTriggerRegistrationDelete = 
                "CREATE TRIGGER after_registration_delete " +
                "AFTER DELETE ON registrations " +
                "FOR EACH ROW " +
                "BEGIN " +
                "   INSERT INTO audit_log (action, user_email, event_id) " +
                "   VALUES ('Deleted Registration', OLD.user_email, OLD.event_id); " +
                "END;";

            stmt.execute(createTriggerRegistrationDelete);
            
            stmt.execute("SET GLOBAL event_scheduler = ON;");

            String createEvent = 
                "CREATE EVENT IF NOT EXISTS delete_past_events " +
                "ON SCHEDULE EVERY 1 DAY " +  
                "STARTS CURRENT_TIMESTAMP " +  
                "DO BEGIN " +
                "   DELETE FROM events WHERE date < CURDATE(); " +
                "END;";

            stmt.execute(createEvent);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing the database: " + e.getMessage());
        }
    }
    public static int[] getEventSummary() {
        int[] counts = new int[3];
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT get_total_counts()");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String[] result = rs.getString(1).split(",");
                counts[0] = Integer.parseInt(result[0]); // Total events
                counts[1] = Integer.parseInt(result[1]); // Total places
                counts[2] = Integer.parseInt(result[2]); // Total users
            }
        } catch (SQLException e) {
            System.err.println("ExceptionL " + e);
        }
        return counts;
    }
}
