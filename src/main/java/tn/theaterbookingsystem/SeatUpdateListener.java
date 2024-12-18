package tn.theaterbookingsystem;

public interface SeatUpdateListener {

    //Définir une méthode update() pour répercuter les modifications sur les sièges dans l'interface graphique.
    void update(int row, int col, boolean isReserved, String reservedBy,
                int availableSeats, int reservedSeats);
}