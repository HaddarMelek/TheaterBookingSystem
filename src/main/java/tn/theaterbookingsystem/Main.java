package tn.theaterbookingsystem;

import javafx.application.Application;
import javafx.stage.Stage;


//nitialise l'application.
//Crée plusieurs interfaces utilisateur en utilisant des threads distincts pour simuler un accès concurrent.2 users for exemple
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TheaterBookingSystem bookingSystem = new TheaterBookingSystem(5, 5);

        Stage user1Stage = new Stage();
        Stage user2Stage = new Stage();

        bookingSystem.startInterface(user1Stage, "User 1");
        bookingSystem.startInterface(user2Stage, "User 2");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
