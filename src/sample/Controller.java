package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import sample.Data;
import sample.Todo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<Todo> todo;

    @FXML
    private TextArea sbk;

    @FXML
    private ListView<Todo> todoListView;

    @FXML
    private TextArea itemDetailsTextArea;

    @FXML
    private Label deadline;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;



    private FilteredList<Todo> filteredList;

    private Predicate<Todo> wantAllItems;
    private Predicate<Todo> wantTodaysItems;


    @FXML
    Button addbtn;

    @FXML
    Label time;
    private int minute;
    private int hour;




    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Remove");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Todo item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Todo>() {
            @Override
            public void changed(ObservableValue<? extends Todo> observable, Todo oldValue, Todo newValue) {
                if(newValue != null) {
                    Todo item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                   DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy "); // "d M yy");






                    sbk.setText(df.format(item.getDeadline()));



                }
            }
        });

        wantAllItems = new Predicate<Todo>() {
            @Override
            public boolean test(Todo todo) {
                return true;
            }
        };

        wantTodaysItems = new Predicate<Todo>() {
            @Override
            public boolean test(Todo todo) {
                return (todo.getDeadline().equals(LocalDate.now()));
            }
        };

        filteredList = new FilteredList<Todo>(Data.getInstance().getTodo(), wantAllItems);

        SortedList<Todo> sortedList = new SortedList<Todo>(filteredList,
                new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });


        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<Todo>, ListCell<Todo>>() {
            @Override
            public ListCell<Todo> call(ListView<Todo> param) {
                ListCell<Todo> cell = new ListCell<Todo>() {

                    @Override
                    protected void updateItem(Todo item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if(item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if(item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.BROWN);
                            }
                        }
                    }
                };


                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }
        });




//
//        LocalDateTime  =  minute;
//        time.setText(minute.getHour());
//          //  hour = LocalDateTime.getHour();
//         //   time.setText(hour + ":" + (minute));


    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New To-do");
        dialog.setHeaderText("Use this blog to create a new to-do list");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todo.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            TodoController controller = fxmlLoader.getController();
            Todo newItem = controller.processResults();
            todoListView.getSelectionModel().select(newItem);
        }


    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        Todo selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    @FXML
    public void handleClickListView() {
        Todo item = todoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetails());
        sbk.setText(item.getDeadline().toString());

    }

    public void deleteItem(Todo item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Todo list");
        alert.setHeaderText("Remove To-do: " + item.getShortDescription());
        alert.setContentText("Are you sure to remove to-do ?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)) {
            Data.getInstance().deleteTodoItem(item);
        }

    }

    @FXML
    public void handleFilterButton() {
        Todo selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if(filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                sbk.setText("");
            } else if(filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }


    @FXML
    public void handleClickList() {
        Todo item = todoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetails());
        sbk.setText(item.getDeadline().toString());
       // sbk.setText(item.getDeadline().atTime(8,45).toString());;


    }


    @FXML
    public void handleExit() {
        Platform.exit();

    }


}

