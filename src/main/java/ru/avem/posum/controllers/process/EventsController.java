package ru.avem.posum.controllers.process;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import ru.avem.posum.controllers.protocol.ProtocolController;
import ru.avem.posum.db.EventsRepository;
import ru.avem.posum.models.process.Event;
import ru.avem.posum.models.process.EventsModel;
import ru.avem.posum.utils.Utils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventsController {
    private ContextMenu contextMenu = new ContextMenu();
    private EventsModel eventsModel = new EventsModel();
    private ProcessController processController;
    private Button saveJournalButton;
    private TableView<Event> table;

    public EventsController(ProcessController processController, Button saveJournalButton, TableView<Event> table) {
        this.processController = processController;
        this.table = table;
        this.saveJournalButton = saveJournalButton;

        initContextMenu();
        listen(table);
    }

    public void init(long testProgramId) {
        table.getItems().addListener((ListChangeListener<Event>) observable -> {
            saveJournalButton.setDisable(table.getItems().isEmpty());
        });

        table.getItems().clear();
        loadEvents(testProgramId);
    }


    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteEvent());
        menuItemClear.setOnAction(event -> clearEvents());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    private void deleteEvent() {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedEvent);
        eventsModel.deleteEvent(selectedEvent);
        processController.getStatusBarLine().setStatus("Событие успешно удалено", true);
    }

    private void clearEvents() {
        processController.getStatusBarLine().toggleProgressIndicator(false);
        processController.getStatusBarLine().setStatusOfProgress("Удаление всех событий из журнала");

        new Thread(() -> {
            ObservableList<Event> events = table.getItems();

            for (Event event : events) {
                eventsModel.deleteEvent(event);
            }

            events.clear();
            processController.getStatusBarLine().toggleProgressIndicator(true);
            processController.getStatusBarLine().setStatus("События успешно удалены", true);
        }).start();
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

    public String saveJournal(String testProgramName, long testProgramId) {
        // Prepare the data
        String[] sheets = {"Журнал событий", "Программа испытаний"};
        String[][] headers = {Utils.getJournalHeaders(), Utils.getCommandsHeaders()};
        List<List<String>> journal = getEvents(testProgramId);
        List<List<String>> commands = processController.getCommandsController().getCommands(testProgramId);
        List<List<List<String>>> sheetsData = new ArrayList<>();
        sheetsData.add(journal);
        sheetsData.add(commands);
        List<Short> journalColors = getEventsColors(testProgramId);
        List<Short> commandsColors = processController.getCommandsController().getCommandsColors(testProgramId);
        List<List<Short>> colors = new ArrayList<>();
        colors.add(journalColors);
        colors.add(commandsColors);
        int[] cellsToMerge = {2, 2};

        // Create and fill the workbook
        ProtocolController protocolController = processController.getProtocolController();
        protocolController.createProtocol(sheets);
        protocolController.createTitle(testProgramName, cellsToMerge, sheets);
        for (int index = 0; index < headers.length; index++) {
            protocolController.createHeaders(sheets[index], headers[index]);
        }

        for (int index = 0; index < sheets.length; index++) {
            protocolController.fill(sheets[index], colors.get(index), sheetsData.get(index));
            protocolController.autosizeColumns(sheets[index]);
        }

        // Show the save file window
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Сохранение журнала");
        File selectedDirectory = fileChooser.showSaveDialog(new Stage());

        // Save the workbook
        String path = "";
        if (selectedDirectory != null) {
            path = selectedDirectory.getAbsolutePath();
            path = path.contains(".xlsx") ? path : path + ".xlsx";
            protocolController.saveProtocol(path);
            processController.getStatusBarLine().setStatus("Журнал событий сохранен в " + path, true);
        }

        return path;
    }

    public List<List<String>> getEvents(long testProgramId) {
        List<ru.avem.posum.db.models.Event> dbEvents = EventsRepository.getAllEvents();
        List<String> events = new ArrayList<>();
        List<String> time = new ArrayList<>();
        for (ru.avem.posum.db.models.Event event : dbEvents) {
            if (event.getTestProgramId() == testProgramId) {
                events.add(event.getDescription());
                time.add(event.getTime());
            }
        }
        List<List<String>> output = new ArrayList<>();
        output.add(events);
        output.add(time);
        return output;
    }

    public List<Short> getEventsColors(long testProgramId) {
        List<ru.avem.posum.db.models.Event> dbEvents = EventsRepository.getAllEvents();
        List<Short> colors = new ArrayList<>();

        for (ru.avem.posum.db.models.Event event : dbEvents) {
            if (event.getTestProgramId() == testProgramId) {
                colors.add(event.getColorIndex());
            }
        }

        return colors;
    }
}
