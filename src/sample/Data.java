package sample;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class Data {
    private static Data instance = new Data();
    private static String filename = "TodoListItems.txt";

    private ObservableList<Todo> todo;
    private DateTimeFormatter formatter;

    public static Data getInstance() {
        return instance;
    }

    private Data() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<Todo> getTodo() {
        return todo;
    }

    public void addTodo(Todo item) {
        todo.add(item);
    }

    public void loadTodoItems() throws IOException {

        todo = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);
                Todo todoItem = new Todo(shortDescription, details, date);
                todo.add(todoItem);
            }

        } finally {
            if(br != null) {
                br.close();
            }
        }
    }

    public void storeTodoItems() throws IOException {

        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<Todo> iter = todo.iterator();
            while(iter.hasNext()) {
                Todo item = iter.next();
                bw.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));
                bw.newLine();
            }

        } finally {
            if(bw != null) {
                bw.close();
            }
        }
    }

    public void deleteTodoItem(Todo item) {
        todo.remove(item);
    }

}
