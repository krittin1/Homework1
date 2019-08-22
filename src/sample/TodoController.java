package sample;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.Data;
import sample.Todo;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class TodoController {

    @FXML
    private TextField shortDescriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
     Button clickbtn;


    public Todo processResults() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();

        Todo newItem = new Todo(shortDescription, details, deadlineValue);
        Data.getInstance().addTodo(newItem);
        return newItem;
    }

    @FXML
    private void closeButtonAction(){

        Stage stage = (Stage) clickbtn.getScene().getWindow();
        stage.close();
    }


}