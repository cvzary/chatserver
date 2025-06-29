package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    //prywatne pole, dostep tylko z tej klasy
    private Connection connection;

    //publiczny akcesor(getter) - inne klasy moga przez to odczytac polaczenie
    public Connection getConnection() {
        return connection;
    }

    //metoda do polaczenia sie z baza SQLite
    public void connect(String dbPath) {
        try {
            String url = "jdbc:sqlite:" + dbPath; //format jdbc do sqlite
            connection = DriverManager.getConnection(url); // tworzy polaczenie
            System.out.println("Polaczono z basa danych");
        } catch (SQLException e) {
            System.err.println("Blad polaczenia z baza");
            e.printStackTrace();
        }
    }

    //metoda do zamykania polaczenia
    public void disconnect() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close(); //zamyka polaczenie
                System.out.println("Rozlaczono z baza danych");
            }
        } catch (SQLException e) {
            System.err.println("Blad rozlaczania z baza");
        }
    }

}
