package auth;

import database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountManager {
    private final DatabaseConnection db;

    public AccountManager(DatabaseConnection db) {
        this.db = db;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try(Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT UNIQUE NOT NULL, " +
                            "password TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String username, String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt()); //haszujemy haslo przy uzyciu brcypt

        try //prepared statement to szablon zapytania sql z miejscami na dane
            (PreparedStatement ps = db.getConnection().prepareStatement(
            "INSERT INTO accounts (username, password) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.executeUpdate(); //wykonuejmy zapytanie
            return true;
        } catch (SQLException e) {
            System.err.println("rejestracja sie nie powiodla " + e.getMessage());
            return false;
        }
    }

    public boolean authenticate(String username, String password) {
        try(PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT password FROM accounts WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String hash = rs.getString("password");
                return BCrypt.checkpw(password, hash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccount(int id) {
        try(PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT id, username FROM accounts WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new Account(rs.getInt("id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getAccount(String username) {
        try(PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT id, username FROM accounts WHERE id = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new Account(rs.getInt("id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}