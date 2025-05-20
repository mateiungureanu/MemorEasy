package com.unibuc.mds.memoreasy.Utils;

import com.unibuc.mds.memoreasy.Models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DatabaseUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/memoreasy";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    private static int id_current_user;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static User authenticateUser(String username, String password) {
        String query = "SELECT * FROM user WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    id_current_user = rs.getInt("id_user");
                    user.setName(rs.getString("name"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO user (name, password) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            if (userExists(username)) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private static boolean userExists(String username) {
        String query = "SELECT COUNT(*) FROM user WHERE name = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getIdUser() {
        return id_current_user;
    }

    public static int getCurrentStreak() {
        int streak = 1;
        try {
            Connection con = getConnection();
            String sql = "SELECT activity_date FROM audit ORDER BY activity_date DESC";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            LocalDate localDate1, localDate2;
            if (rs.next()) {
                localDate1 = rs.getDate(1).toLocalDate();
                while (rs.next()) {
                    localDate2 = rs.getDate(1).toLocalDate();
                    long diff = Math.abs(ChronoUnit.DAYS.between(localDate2, localDate1));

                    if (diff == 1) {
                        streak++;
                    } else if (diff > 1) {
                        break;
                    }
                    localDate1 = localDate2;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return streak;
    }
} 