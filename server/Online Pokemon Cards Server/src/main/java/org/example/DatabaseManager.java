package org.example;

import java.sql.*;

public class DatabaseManager {
    // URL for the SQLite database
    private static final String DB_URL = "jdbc:sqlite:C:pokemon_cards.db";
    private final String USER_NOT_FOUND = "404 User not found!";
    private final String DATABASE_ERROR = "500 Database error: ";
    private Connection conn;

    public DatabaseManager() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Establish a connection to the database
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void createTables() {
        // Check if the connection to the database is established
        if (conn == null) {
            System.err.println("No connection to the database. Cannot create tables.");
            return;
        }
        // SQL statement for creating the Users table
        String sqlUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                "ID INTEGER PRIMARY KEY," +
                "first_name TEXT," +
                "last_name TEXT," +
                "user_name TEXT NOT NULL," +
                "password TEXT," +
                "email TEXT," +
                "usd_balance DOUBLE NOT NULL," +
                "is_root INTEGER NOT NULL DEFAULT 0" +
                ");";
        // SQL statement for creating the Pokemon_cards table
        String sqlCards = "CREATE TABLE IF NOT EXISTS Pokemon_cards (" +
                "ID INTEGER PRIMARY KEY," +
                "card_name TEXT NOT NULL," +
                "card_type TEXT NOT NULL," +
                "rarity TEXT NOT NULL," +
                "count INTEGER," +
                "owner_id INTEGER," +
                "FOREIGN KEY (owner_id) REFERENCES Users(ID)," +
                "UNIQUE(card_name, owner_id)" +
                ");";
        try (Statement stmt = conn.createStatement()) {
            // Execute the SQL statements to create the tables
            stmt.execute(sqlUsers);
            stmt.execute(sqlCards);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

//    public void insertSampleData() {
//        String sqlDeleteUsers = "DELETE FROM Users";
//        String sqlDeleteCards = "DELETE FROM Pokemon_cards";
//        String sqlInsertUser = "INSERT INTO Users(ID, first_name, last_name, user_name, password, email, is_root, usd_balance) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
//        String sqlInsertCard = "INSERT INTO Pokemon_cards(ID, card_name, card_type, rarity, count, owner_id) VALUES(?, ?, ?, ?, ?, ?)";
//        try (Statement stmt = conn.createStatement();
//             PreparedStatement pstmtUser = conn.prepareStatement(sqlInsertUser);
//             PreparedStatement pstmtCard = conn.prepareStatement(sqlInsertCard)) {
//
//            // Clear existing data
//            stmt.execute(sqlDeleteUsers);
//            stmt.execute(sqlDeleteCards);
//
//            // Insert users
//            Object[][] users = {
//                    {1, "john", "doe", "j_doe", "Passwrd4", "j.doe@abc.com", 1, 80.0},
//                    {2, "jane", "smith", "j_smith", "pass456", "j.smith@abc.com", 0, 10.99},
//                    {3, "charlie", "brown", "c_brown", "Snoopy", "c.brown@abc.com", 0, 90.0},
//                    {4, "lucy", "van", "l_van", "Football", "l.van@abc.com", 0, 70.0},
//                    {5, "linus", "blanket", "l_blanket", "security23", "l.blanket@abc.com", 0, 90.0 }
//            };
//            for (Object[] user : users) {
//                for (int i = 0; i < user.length; i++) {
//                    pstmtUser.setObject(i + 1, user[i]);
//                }
//                pstmtUser.executeUpdate();
//            }
//            // Insert cards
//            Object[][] cards = {
//                    {1, "Pikachu", "Electric", "Common", 2, 1},
//                    {2, "Charizard", "Fire", "Rare", 1, 1},
//                    {3, "Bulbasaur", "Grass", "Common", 50, 3},
//                    {4, "Squirtle", "Water", "Uncommon", 30, 4},
//                    {5, "Jigglypuff", "Normal", "Common", 3, 5}
//            };
//            for (Object[] card : cards) {
//                for (int i = 0; i < card.length; i++) {
//                    pstmtCard.setObject(i + 1, card[i]);
//                }
//                pstmtCard.executeUpdate();
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
    public void createDefaultUser() {
        // SQL query to insert a default user into the Users table
        String sql = "INSERT INTO Users (first_name, last_name, user_name, password, email, usd_balance, is_root) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the default user details
            pstmt.setString(1, "Default");
            pstmt.setString(2, "User");
            pstmt.setString(3, "default_user");
            pstmt.setString(4, "password123"); // You should use a secure password hashing method in practice
            pstmt.setString(5, "default@example.com");
            pstmt.setDouble(6, 100.00);
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();
            System.out.println("Default user created with $100 balance");
        } catch (SQLException e) {
            System.err.println("Error creating default user: " + e.getMessage());
        }
    }

    public double getBalance(int userId) {
        // SQL query to get the balance of a user by their ID
        String sql = "SELECT usd_balance FROM Users WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Return the user's balance
                return rs.getDouble("usd_balance");
            }
        } catch (SQLException e) {
            System.err.println("Error getting balance: " + e.getMessage());
        }
        return -1; // Return -1 if user not found
    }

    public String buyCard(String cardName, String cardType, String rarity, double price, int count, int ownerId) {
        if (userExists(ownerId)) {
            return USER_NOT_FOUND;
        }
        try {
            conn.setAutoCommit(false);  // Start transaction
            // Check user balance
            double balance = getBalance(ownerId);
            double totalCost = price * count;
            if (balance < totalCost) {
                return "403 Not enough balance";
            }

            // Update user balance
            String updateBalanceSql = "UPDATE Users SET usd_balance = usd_balance - ? WHERE ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                pstmt.setDouble(1, totalCost);
                pstmt.setInt(2, ownerId);
                pstmt.executeUpdate();
            }

            // Add or update card in Pokemon_cards table
            String upsertCardSql = "INSERT INTO Pokemon_cards (card_name, card_type, rarity, count, owner_id) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT(card_name, owner_id) DO UPDATE SET count = count + ?";
            try (PreparedStatement pstmt = conn.prepareStatement(upsertCardSql)) {
                pstmt.setString(1, cardName);
                pstmt.setString(2, cardType);
                pstmt.setString(3, rarity);
                pstmt.setInt(4, count);
                pstmt.setInt(5, ownerId);
                pstmt.setInt(6, count);
                pstmt.executeUpdate();
            }

            conn.commit();  // Commit the transaction after checking all the requirements

            // Get the new balance and card count
            double newBalance = getBalance(ownerId);
            int newCount = getCardCount(cardName, ownerId);
            return String.format("200 OK \nBOUGHT: New balance: %d %s. User USD balance $%.2f", newCount, cardName, newBalance);
        } catch (SQLException e) {
            try {
                conn.rollback();  // Rollback in case of any error
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return DATABASE_ERROR + e.getMessage();
        } finally {
            try {
                conn.setAutoCommit(true);  // Reset to default mode
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public String sellCard(String cardName, int quantity, double price, int ownerId) {
        if (userExists(ownerId)) {
            return USER_NOT_FOUND;
        }
        try {
            conn.setAutoCommit(false);  // Start transaction

            // Check if user has enough cards
            int currentCount = getCardCount(cardName, ownerId);
            if (currentCount < quantity) {
                return "403 Not enough cards to sell";
            }
            double totalEarnings = price * quantity;

            // Update user balance
            String updateBalanceSql = "UPDATE Users SET usd_balance = usd_balance + ? WHERE ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBalanceSql)) {
                pstmt.setDouble(1, totalEarnings);
                pstmt.setInt(2, ownerId);
                pstmt.executeUpdate();
            }

            // Update card count
            String updateCardSql = "UPDATE Pokemon_cards SET count = count - ? WHERE card_name = ? AND owner_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateCardSql)) {
                pstmt.setInt(1, quantity);
                pstmt.setString(2, cardName);
                pstmt.setInt(3, ownerId);
                pstmt.executeUpdate();
            }
            conn.commit();  // Commit the transaction

            double newBalance = getBalance(ownerId);
            int newCount = getCardCount(cardName, ownerId);
            return String.format("200 OK \nSOLD: New balance: %d %s. User's balance USD $%.2f", newCount, cardName, newBalance);
        } catch (SQLException e) {
            try {
                conn.rollback();  // Rollback in case of any error
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return DATABASE_ERROR + e.getMessage();
        } finally {
            try {
                conn.setAutoCommit(true);  // Reset to default mode
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public String listCards(int ownerId) {
        if (userExists(ownerId)) {
            return USER_NOT_FOUND;
        }
        try {
            // SQL query to select all cards owned by the specified user
            String sql = "SELECT * FROM Pokemon_cards WHERE owner_id = ?";
            StringBuilder result = new StringBuilder("200 OK \nThe list of records in the Pokemon cards table for current user, user " + ownerId + ":\n");
            result.append(String.format("%-5s %-15s %-15s %-15s %-10s %-10s\n", "ID", "Card Name", "Card Type", "Rarity", "Count", "OwnerID"));

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ownerId);
                ResultSet rs = pstmt.executeQuery();
                // Append each card's details to the result string
                while (rs.next()) {
                    result.append(String.format("%-5s %-15s %-15s %-15s %-10s %-10s\n",
                            rs.getInt("ID"),
                            rs.getString("card_name"),
                            rs.getString("card_type"),
                            rs.getString("rarity"),
                            rs.getInt("count"),
                            rs.getInt("owner_id")));
                }
            }
            return result.toString();
        } catch (SQLException e) {
            return DATABASE_ERROR + e.getMessage();
        }
    }

    public String getBalanceForUser(int ownerId) {
        if (userExists(ownerId)) {
            return USER_NOT_FOUND;
        }
        try {
            // SQL query to get the balance and name of the user by their ID
            String sql = "SELECT first_name, last_name, usd_balance FROM Users WHERE ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ownerId);
                ResultSet rs = pstmt.executeQuery();
                // Retrieve user details and balance
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                double balance = rs.getDouble("usd_balance");
                // Return the balance information
                return String.format("200 OK \nBalance for user %s %s: $%.2f", firstName, lastName, balance);
            }
        } catch (SQLException e) {
            return DATABASE_ERROR + e.getMessage();
        }
    }

    private int getCardCount(String cardName, int ownerId) throws SQLException {
        // SQL query to get the count of a specific card owned by the user
        String sql = "SELECT count FROM Pokemon_cards WHERE card_name = ? AND owner_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardName);
            pstmt.setInt(2, ownerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    private boolean userExists(int userId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) <= 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    public boolean hasUsers() {
        // SQL query to count the number of users in the Users table
        String sql = "SELECT COUNT(*) FROM Users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                // Return true if there is at least one user
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking for users: " + e.getMessage());
        }
        // Return false if no users found or an error occurred
        return false;
    }

    public void closeConnection() {
        try {
            // Close the database connection if it is not null
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}