package ru.avem.posum.controllers.process;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import ru.avem.posum.db.CommandsRepository;
import ru.avem.posum.models.process.Command;
import ru.avem.posum.models.process.CommandsModel;
import ru.avem.posum.models.process.CommandsTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static ru.avem.posum.db.CommandsRepository.getAllCommands;

public class CommandsController {
    private ComboBox<String> commandsComboBox;
    private CommandsModel commandsModel = new CommandsModel();
    private ContextMenu contextMenu = new ContextMenu();
    private Dialog dialog;
    private boolean didBackSpacePressed;
    private GridPane grid;
    private ProcessController processController;
    private TableView<Command> table;
    private TextField descriptionTextFiled;
    private TimerController timerController = new TimerController();

    public CommandsController(ProcessController processController, TableView<Command> table) {
        this.processController = processController;
        this.table = table;

        initContextMenu();
        listen(table);
    }

    public void init(long testProgramId) {
        table.getItems().clear();
        loadCommands(testProgramId);
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
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty()) && processController.getProcess().isStopped()) {
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
        createDialog();
        createButtons();
        createComboBox();
        createTextField();
        createGrid();
        setButtonsStyle();
        setResult();
        dialog.showAndWait();
    }

    private void createDialog() {
        dialog = new Dialog();
        dialog.setTitle("Добавление команды");
        dialog.setHeaderText("Задайте необходымые параметры");
    }

    private void createButtons() {
        ButtonType add = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(add, cancel);
    }

    private void createComboBox() {
        commandsComboBox = new ComboBox<>();
        commandsComboBox.getItems().addAll(CommandsTypes.PAUSE.getTypeName(), CommandsTypes.STOP.getTypeName());
        commandsComboBox.getSelectionModel().select(0);
        commandsComboBox.setStyle("-fx-background-radius: 5px;\n");
        commandsComboBox.getStylesheets().add(getClass().getResource("combo-box.css").toExternalForm());
        commandsComboBox.setMinWidth(175);
        Platform.runLater(() -> commandsComboBox.requestFocus());
        listen(commandsComboBox);
    }

    private void listen(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener(observable -> {
            createTextField();
            createGrid();
            Platform.runLater(() -> {
                descriptionTextFiled.requestFocus();
                commandsComboBox.requestFocus();
            });
        });
    }

    private void createTextField() {
        descriptionTextFiled = new TextField();
        descriptionTextFiled.setPromptText("чч:мм:сс чч:мм:сс");
        descriptionTextFiled.setOnKeyPressed(this::listenBackSpaceKey);
        descriptionTextFiled.setStyle("-fx-background-radius: 5px;\n");

        String selectedValue = commandsComboBox.getSelectionModel().getSelectedItem();
        boolean isPause = selectedValue.equals(CommandsTypes.PAUSE.getTypeName());
        boolean isStop = selectedValue.equals(CommandsTypes.STOP.getTypeName());

        if (isPause) {
            descriptionTextFiled.setPromptText("чч:мм:сс чч:мм:сс");
            setFormat(17, ":");
            descriptionTextFiled.setOnKeyPressed(this::listenBackSpaceKey);
        }

        if (isStop) {
            descriptionTextFiled.setPromptText("чч:мм:сс");
            setFormat(8, ":");
            descriptionTextFiled.setOnKeyPressed(this::listenBackSpaceKey);
        }
    }

    private void listenBackSpaceKey(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        didBackSpacePressed = keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE;
    }

    private void setFormat(int maxLength, String separator) {
        descriptionTextFiled.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = descriptionTextFiled.getText();

            descriptionTextFiled.setText(text.replaceAll("[^\\d" + separator + "\\s]", ""));
            addSeparator(separator);

            if (text.length() > maxLength) {
                descriptionTextFiled.setText(oldValue);
            }
        });
    }


    private void addSeparator(String separator) {
        int charactersCounter = descriptionTextFiled.getText().length();
        String selectedValue = commandsComboBox.getSelectionModel().getSelectedItem();
        boolean isPause = selectedValue.equals(CommandsTypes.PAUSE.getTypeName());
        boolean isStop = selectedValue.equals(CommandsTypes.STOP.getTypeName());

        if (isPause) {
            if (!didBackSpacePressed) {
                if (charactersCounter == 2 || charactersCounter == 5 || charactersCounter == 11 || charactersCounter == 14) {
                    descriptionTextFiled.setText(descriptionTextFiled.getText() + separator);
                }

                if (charactersCounter == 8) {
                    descriptionTextFiled.setText(descriptionTextFiled.getText() + " ");
                }
            }
        }

        if (isStop) {
            if (!didBackSpacePressed) {
                if (charactersCounter == 2 || charactersCounter == 5) {
                    descriptionTextFiled.setText(descriptionTextFiled.getText() + separator);
                }
            }
        }
    }

    private void createGrid() {
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Команда:"), 0, 0);
        grid.add(commandsComboBox, 1, 0);
        grid.add(new Label("Параметры:"), 0, 1);
        grid.add(descriptionTextFiled, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getContent().setStyle("-fx-font-size: 13px;");
    }

    private void setButtonsStyle() {
        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 13px;\n" + "-fx-background-radius: 5px;\n" +
                "\t-fx-border-radius: 5px;"));
    }

    private void setResult() {
        ButtonType add = dialog.getDialogPane().getButtonTypes().get(0);
        ButtonType cancel = dialog.getDialogPane().getButtonTypes().get(1);

        dialog.setResultConverter(dialogButton -> {
            dialog.setOnCloseRequest(event -> {
                if (dialogButton == add) {
                    if (!validate(commandsComboBox.getSelectionModel().getSelectedItem(), descriptionTextFiled)) {
                        event.consume();
                    } else {
                        String commandType = commandsComboBox.getSelectionModel().getSelectedItem();
                        String newDescription = createDescription(commandType, descriptionTextFiled.getText());
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
    }

    private boolean validate(String commandType, TextField textField) {
        boolean isPause = commandType.equals(CommandsTypes.PAUSE.getTypeName());
        boolean isStop = commandType.equals(CommandsTypes.STOP.getTypeName());
        String description = textField.getText();
        boolean isDescriptionCorrect = true;

        if (isPause) {
            isDescriptionCorrect = description.matches("^[\\d]{2,3}:[0-5][\\d]:[0-5][\\d][\\s][\\d]{2,3}:[0-5][\\d]:[0-5][\\d]");
        } else if (isStop) {
            isDescriptionCorrect = description.matches("^[\\d]{2,3}:[0-5][\\d]:[0-5][\\d]");
        }

        if (!isDescriptionCorrect) {
            processController.getStatusBarLine().setStatus("Неверно задан параметр команды", false);
        }

        return isDescriptionCorrect;
    }

    private String createDescription(String commandType, String oldDescription) {
        String newDescription = oldDescription;
        boolean isPause = commandType.equals(CommandsTypes.PAUSE.getTypeName());
        boolean isStop = commandType.equals(CommandsTypes.STOP.getTypeName());

        if (isPause) {
            String startPauseTime = oldDescription.split(" ")[1];
            String pauseTime = oldDescription.split(" ")[0];
            newDescription = "На " + pauseTime + " через " + startPauseTime + " с начала запуска";
        } else if (isStop) {
            newDescription = "Через " + oldDescription + " с начала запуска";
        }

        return newDescription;
    }

    public void addCommand(String type, String description) {
        commandsModel.addCommand(processController.getTestProgramId(), type, description);
    }

    public void executeCommands() {
        for (Command command : getCommands()) {
            boolean isPause = command.getType().equals(CommandsTypes.PAUSE.getTypeName());
            boolean isStop = command.getType().equals(CommandsTypes.STOP.getTypeName());
            long time = command.getTime();

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isPause) {
                        processController.handlePause(command.getDelay());
                    } else if (isStop) {
                        processController.handleStop();
                    }
                }
            };

            timerController.createTimer(timerTask, time);
        }

        timerController.startTimers();
    }

    public int getCellsToMerge() {
        return getCommandsHeaders().length;
    }

    public ObservableList<Command> getCommands() {
        return commandsModel.getCommands();
    }

    public List<List<String>> getCommands(long testProgramId) {
        List<ru.avem.posum.db.models.Command> dbCommands = CommandsRepository.getAllCommands();
        List<String> commands = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        for (ru.avem.posum.db.models.Command command : dbCommands) {
            if (command.getTestProgramId() == testProgramId) {
                commands.add(command.getCommand());
                parameters.add(command.getDescription());
            }
        }
        List<List<String>> output = new ArrayList<>();
        output.add(commands);
        output.add(parameters);
        return output;
    }

    public List<Short> getCommandsColors(long testProgramId) {
        List<ru.avem.posum.db.models.Command> dbCommands = CommandsRepository.getAllCommands();
        List<Short> colors = new ArrayList<>();

        for (ru.avem.posum.db.models.Command command : dbCommands) {
            if (command.getTestProgramId() == testProgramId) {
                colors.add(command.getColorIndex());
            }
        }

        return colors;
    }

    public String[] getCommandsHeaders() {
        return new String[]{"Команды", "Параметры"};
    }

    public TimerController getTimerController() {
        return timerController;
    }
}
