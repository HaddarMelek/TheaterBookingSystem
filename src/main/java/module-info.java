module tn.theaterbookingsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens tn.theaterbookingsystem to javafx.fxml;
    exports tn.theaterbookingsystem;
}