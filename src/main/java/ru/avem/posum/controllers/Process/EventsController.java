package ru.avem.posum.controllers.Process;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import ru.avem.posum.db.EventsRepository;
import ru.avem.posum.models.Process.Command;
import ru.avem.posum.models.Process.Event;
import ru.avem.posum.models.Process.EventsModel;

import java.util.List;
import java.util.Optional;

public class EventsController {
    private ContextMenu contextMenu = new ContextMenu();
    private EventsModel eventsModel = new EventsModel();
    private ProcessController processController;
    private TableView<Event> table;

    public EventsController(ProcessController processController, TableView<Event> table) {
        this.processController = processController;
        this.table = table;

        initContextMenu();
        listen(table);
    }

    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteCommand());
        menuItemClear.setOnAction(event -> clearCommands());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void deleteCommand() {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedEvent);
        eventsModel.deleteEvent(selectedEvent);
        processController.getStatusBarLine().setStatus("Событие успешно удалено", true);
    }

    private void clearCommands() {
        ObservableList<Event> events = table.getItems();

        for (Event event : events) {
            eventsModel.deleteEvent(event);
        }

        events.clear();
        processController.getStatusBarLine().setStatus("События успешно удалены", true);
    }

    public void listen(TableView<Event> tableView) {
        tableView.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
                }
            });

            row.itemProperty().addListener(observable -> {
                setEventsColors(row);
            });

            row.selectedProperty().addListener(observable -> {
                if (row.isSelected()) {
                    row.setStyle("-fx-background-color: #0096C9;");
                } else {
                    setEventsColors(row);
                }
            });


            tv.refresh();

            return row;
        });
    }

    public void loadEvents(long testProgramId) {
        List<ru.avem.posum.db.models.Event> events = EventsRepository.getAllEvents();

        for (ru.avem.posum.db.models.Event event : events) {
            if (event.getTestProgramId() == testProgramId) {
                eventsModel.loadEvent(event);
            }
        }
    }

    public void showDialogOfEventAdding() {
        // Create the text dialog.
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Добавление события");
        dialog.setHeaderText("Введите описание:");
        dialog.setContentText("Описание:");
        dialog.getDialogPane().setMinWidth(300);

        // Set the custom button types.
        dialog.getDialogPane().getButtonTypes().clear();
        ButtonType add = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(add, cancel);

        // Set the style.
        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 13px;\n" + "-fx-background-radius: 5px;\n" +
                "\t-fx-border-radius: 5px;"));
        dialog.getDialogPane().getContent().setStyle("-fx-font-size: 13px;");


        Optional<String> result = dialog.showAndWait();
        result.ifPresent(eventText -> eventsModel.setEvent(processController.getTestProgramId(), eventText));
    }

    public void setEventsColors(TableRow<Event> row) {
        if (row != null && row.getItem() != null) {
            switch (row.getItem().getStatus()) {
                case "ERROR":
                    row.setStyle("-fx-background-color: #ff4500;");
                    break;
                case "LOG":
                    row.setStyle("-fx-background-color: #f0f8ff;");
                    break;
                case "OK":
                    row.setStyle("-fx-background-color: #92cd74;");
                    break;
                case "WARNING":
                    row.setStyle("-fx-background-color: #f0e68c;");
                    break;
                default:
                    row.setStyle(null);
            }
        }
    }

    public EventsModel getEventsModel() {
        return eventsModel;
    }
}
