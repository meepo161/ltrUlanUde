package ru.avem.posum.controllers.Process;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import ru.avem.posum.models.Process.Command;
import ru.avem.posum.models.Process.CommandsModel;
import ru.avem.posum.models.Process.CommandsTypes;

import java.util.List;

import static ru.avem.posum.db.CommandsRepository.getAllCommands;


public class CommandsController {
    private CommandsModel commandsModel = new CommandsModel();
    private ContextMenu contextMenu = new ContextMenu();
    private boolean didBackSpacePressed;
    private ProcessController processController;
    private TableView<Command> table;

    public CommandsController(ProcessController processController, TableView<Command> table) {
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
        Command selectedCommand = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedCommand);
        commandsModel.deleteCommand(selectedCommand);
        processController.getStatusBarLine().setStatus("Команда успешно удалена", true);
    }

    private void clearCommands() {
        ObservableList<Command> commands = table.getItems();

        for (Command command : commands) {
            commandsModel.deleteCommand(command);
        }

        commands.clear();
        processController.getStatusBarLine().setStatus("Команды успешно удалены", true);
    }

    private void listen(TableView<Command> tableView) {
        tableView.setRowFactory(tv -> {
            TableRow<Command> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 1) {
                    contextMenu.hide();
                }
            });

            return row;
        });
    }

    public void loadCommands(long testProgramId) {
        List<ru.avem.posum.db.models.Command> commands = getAllCommands();

        for (ru.avem.posum.db.models.Command command : commands) {
            if (command.getTestProgramId() == testProgramId) {
                commandsModel.loadCommand(command);
            }
        }
    }

    public void showDialogOfCommandAdding() {
        // Create the custom dialog.
        Dialog dialog = new Dialog();
        dialog.setTitle("Добавление команды");
        dialog.setHeaderText("Задайте необходымые параметры");

        // Set the button types.
        ButtonType add = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(add, cancel);

        // Create the commands combobox and description textfield.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> commands = new ComboBox<>();
        commands.getItems().addAll(CommandsTypes.PAUSE.getTypeName(), CommandsTypes.STOP.getTypeName());
        commands.getSelectionModel().select(0);
        TextField description = new TextField();
        description.setPromptText("чч:мм:сс");
        setTextFormat(description, 8, ":");
        description.setOnKeyPressed(this::listenBackSpaceKey);

        grid.add(new Label("Команда:"), 0, 0);
        grid.add(commands, 1, 0);
        grid.add(new Label("Параметры:"), 0, 1);
        grid.add(description, 1, 1);
        dialog.getDialogPane().setContent(grid);

        // Set the style.
        commands.setStyle("-fx-background-radius: 5px;\n");
        commands.setMinWidth(175);
        description.setStyle("-fx-background-radius: 5px;\n");
        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 13px;\n" + "-fx-background-radius: 5px;\n" +
                "\t-fx-border-radius: 5px;"));
        dialog.getDialogPane().getContent().setStyle("-fx-font-size: 13px;");

        // Add listeners.
        listen(commands, description);


        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            dialog.setOnCloseRequest(event -> {
                if (dialogButton == add) {
                    if (!validate(description)) {
                        event.consume();
                    } else {
                        String commandType = commands.getSelectionModel().getSelectedItem();
                        String newDescription = createDescription(commandType, description.getText());
                        addCommand(commandType, newDescription);
                    }
                }
            });

            if (dialogButton == add) {
                return add;
            } else {
                return cancel;
            }
        });

        dialog.showAndWait();
    }

    private void listen(ComboBox<String> comboBox, TextField textField) {
        comboBox.valueProperty().addListener(observable -> {
            String selectedValue = comboBox.getSelectionModel().getSelectedItem();

            if (selectedValue.equals(CommandsTypes.PAUSE.getTypeName()) ||
                    selectedValue.equals(CommandsTypes.STOP.getTypeName())) {
                textField.setPromptText("чч:мм:сс");
                setTextFormat(textField, 8, ":");
                textField.setOnKeyPressed(this::listenBackSpaceKey);
            }
        });
    }

    private void setTextFormat(TextField textField, int limitOfNumbers, String separator) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = textField.getText();

            textField.setText(text.replaceAll("[^\\d" + separator + "]", ""));
            addColons(textField, text, separator);

            if (text.length() > limitOfNumbers) {
                textField.setText(oldValue);
            }
        });
    }

    private void addColons(TextField textField, String text, String separator) {
        int charactersCounter = text.length();

        if (!didBackSpacePressed) {
            if (charactersCounter == 2 || charactersCounter == 5) {
                textField.setText(text + separator);
            }
        }
    }

    private void listenBackSpaceKey(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        didBackSpacePressed = keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE;
    }

    private boolean validate(TextField textField) {
        String description = textField.getText();
        boolean isDescriptionCorrect = true;

        if (!description.matches("^[\\d]{2,3}:[0-5][\\d]:[0-5][\\d]")) {
            processController.getStatusBarLine().setStatus("Неверно задано время", false);
            isDescriptionCorrect = false;
        }

        return isDescriptionCorrect;
    }

    private String createDescription(String commandType, String oldDescription) {
        String newDescription = oldDescription;

        if (commandType.equals(CommandsTypes.PAUSE.getTypeName()) || commandType.equals(CommandsTypes.STOP.getTypeName())) {
            newDescription = "Через " + oldDescription + " с начала запуска";
        }

        return newDescription;
    }

    public ObservableList<Command> getCommands() {
        return commandsModel.getCommands();
    }

    public void addCommand(String type, String description) {
        commandsModel.addCommand(processController.getTestProgramId(), type, description);
    }
}
