module ru.sirramzi.ssau {
    requires javafx.controls;
    requires javafx.fxml;

    opens ru.sirramzi.ssau to javafx.fxml;
    exports ru.sirramzi.ssau;
}
