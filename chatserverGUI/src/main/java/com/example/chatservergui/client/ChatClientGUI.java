package com.example.chatservergui.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientGUI extends Application {
    private final TextArea chatArea = new TextArea();
    private final TextField inputField = new TextField();
    private final ListView<String> userList = new ListView<>();
    private final Button sendButton = new Button("Send");
    private PrintWriter out;

    private void sendMessage() {
        String message = inputField.getText().trim();
        if(!message.isEmpty()) {
            out.println(message);
            inputField.clear();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Socket socket = new Socket("localhost", 12345);
        out = new PrintWriter(socket.getOutputStream(), true);

        TextInputDialog loginDialog = new TextInputDialog();
        loginDialog.setHeaderText("Podaj swój login:");
        loginDialog.setTitle("Logowanie");

        String login = loginDialog.showAndWait().orElse("Anonim");
        out.println("login " + login);

        ClientReceiver receiver = new ClientReceiver(chatArea, userList, socket);
        receiver.start();
        chatArea.setEditable(false);
        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        BorderPane pane = new BorderPane();
        HBox bottomPanel = new HBox(inputField, sendButton);
        VBox rightPanel = new VBox(new Label("Użytkownicy"), userList);
        pane.setCenter(chatArea);
        pane.setRight(rightPanel);
        pane.setBottom(bottomPanel);

        Scene scene = new Scene(pane, 600, 400);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}