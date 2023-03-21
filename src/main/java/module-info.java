module ru.sirramzi.ssau {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens ru.sirramzi.ssau to javafx.fxml;
    exports ru.sirramzi.ssau;
}
