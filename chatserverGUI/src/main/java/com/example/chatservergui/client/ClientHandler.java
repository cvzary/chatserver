package com.example.chatservergui.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final List<ClientHandler> clients;
    private PrintWriter out;
    private String login;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Witaj w czacie! Podaj login: ");
            String message = in.readLine();
            if (message.startsWith("login")) {
                this.login = message.substring(6).trim();
                broadcast("login " + login);
            }
            while((message = in.readLine()) != null) {
                if(message.equals("/online")) {
                    listOnlineUsers();
                } else if (message.startsWith("/w ")) {
                    sendPrivateMessage(message);
                } else {
                    System.out.println("Otrzymano wiadomość: " + message);
                    broadcast(this.login + ": " + message);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd komunikacji: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException _) {}
            clients.remove(this);
            broadcast("Uzytkownik " + login + " opuścił czat");
        }
    }

    private void sendMessage(String message) {
        if(out != null) {
            out.println(message);
        }
    }

    private void broadcast(String message) {
        for(ClientHandler c : clients) {
            if(c.login != null) {
                c.sendMessage(message);
            }
        }
    }

    private void listOnlineUsers() {
        StringBuilder sb = new StringBuilder("Zalogowani użytkownicy: ");
        for(ClientHandler c : clients) {
            if(c.login != null) {
                sb.append("\n - ").append(c.login);
            }
        }
        sendMessage(sb.toString());
    }

    private void sendPrivateMessage(String input) {
        String[] parts = input.split(" ", 3);
        if(parts.length < 3) {
            sendMessage("Błąd składni! Użyj: /w login wiadomość");
            return;
        }
        String recipientLogin = parts[1];
        String msg = parts[2];
        boolean found = false;

        for(ClientHandler c : clients) {
            if(recipientLogin.equals(c.login)) {
                c.sendMessage("[Whisper from " + this.login + "]: " + msg);
                found = true;
                break;
            }
        }

        if (!found) {
            sendMessage("Użytkownik " + recipientLogin + " nie jest online!");
        }
    }

}
