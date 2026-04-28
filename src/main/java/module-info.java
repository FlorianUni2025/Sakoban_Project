module com.example.sokoban_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sokoban_project to javafx.fxml;
    exports com.example.sokoban_project;
}