package tn.theaterbookingsystem;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


//Gérer l'état des réservations de sièges et assurer la synchronisation entre les utilisateurs concurrents.
public class TheaterBookingSystem {

    private final boolean[][] seats;
    private final String[][] reservedBy;
    private final Semaphore semaphore;//Utilisé pour garantir qu'un seul thread peut modifier les données des sièges à un moment donné.
    private final List<SeatUpdateListener> listeners = new ArrayList<>();
    private int availableSeats;
    private int reservedSeats;

    public TheaterBookingSystem(int rows, int cols) {
        seats = new boolean[rows][cols];
        reservedBy = new String[rows][cols];
        availableSeats = rows * cols;
        reservedSeats = 0;
        semaphore = new Semaphore(1); // Binary semaphore for synchronization
    }


    public void registerListener(SeatUpdateListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(int row, int col, boolean isReserved, String reservedBy) {
        //Utilisé pour mettre à jour l'interface utilisateur de manière thread-safe.
        Platform.runLater(() -> {
            for (SeatUpdateListener listener : listeners) {
                listener.update(row, col, isReserved, reservedBy, availableSeats, reservedSeats);
            }
        });
    }

    public void toggleSeat(int row, int col, String userName) {
        new Thread(() -> {
            try {
                semaphore.acquire(); // Acquérir le sémaphore

                if (seats[row][col]) {
                    // Siège déjà réservé
                    if (reservedBy[row][col].equals(userName)) {
                        seats[row][col] = false;
                        reservedBy[row][col] = null;
                        reservedSeats--;
                        availableSeats++;
                        notifyListeners(row, col, false, null);
                    }
                } else {
                    // Réserver le siège
                    seats[row][col] = true;
                    reservedBy[row][col] = userName;
                    reservedSeats++;
                    availableSeats--;
                    notifyListeners(row, col, true, userName);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release(); // Libérer le sémaphore
            }
        }).start();
    }

    public void startInterface(Stage stage, String userName) {
        stage.setTitle("Theater Booking System - " + userName);

        VBox root = new VBox(10);
        Label userLabel = new Label("Welcome, " + userName);
        Label availableLabel = new Label("Available Seats: " + availableSeats);
        Label reservedLabel = new Label("Reserved Seats: " + reservedSeats);
        GridPane seatGrid = new GridPane();

        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                final int row = i, col = j;

                Button seatButton = new Button("Available");
                seatButton.setPrefSize(100, 50);

                seatButton.setOnAction(event -> toggleSeat(row, col, userName));

                registerListener((r, c, isReserved, reservedBy, availableSeats, reservedSeats) -> {
                    if (r == row && c == col) {
                        if (isReserved) {
                            seatButton.setText("Reserved by " + reservedBy);
                            seatButton.setStyle("-fx-background-color: #ea7412;");
                            seatButton.setDisable(!reservedBy.equals(userName));
                        } else {
                            seatButton.setText("Available");
                            seatButton.setDisable(false);
                        }
                    }
                });

                seatGrid.add(seatButton, j, i);
            }
        }

        registerListener((r, c, isReserved, reservedBy, availableSeats, reservedSeats) -> {
            availableLabel.setText("Available Seats: " + availableSeats);
            reservedLabel.setText("Reserved Seats: " + reservedSeats);
        });

        root.getChildren().addAll(userLabel, availableLabel, reservedLabel, seatGrid);
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();
    }
}
