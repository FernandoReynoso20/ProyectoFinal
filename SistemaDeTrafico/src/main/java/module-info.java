module com.example.sistemadetrafico {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.sistemadetrafico to javafx.fxml;
    exports com.example.sistemadetrafico;
}
