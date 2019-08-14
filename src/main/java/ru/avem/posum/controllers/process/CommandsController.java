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
    private TableColumn<Command, String> commandsTypesColumn;
    private TableColumn<Command, String> commandsDescriptionsColumn;
    private ContextMenu contextMenu = new ContextMenu();
    private Dialog dialog;
    private boolean didBackSpacePressed;
    private GridPane grid;
    private ProcessController processController;
    private TableView<Command> table;
    private TextField descriptionTextFiled;
    private TimerController timerController = new TimerController();

    public CommandsController(ProcessController processController, TableView<Command> table,
                              TableColumn<Command, String> commandsTypesColumn,
                              TableColumn<Command, String> commandsDescriptionsColumn) {
        this.processController = processController;
        this.table = table;
        this.commandsTypesColumn = commandsTypesColumn;
        this.commandsDescriptionsColumn = commandsDescriptionsColumn;

        initContextMenu();
        listen(table);
    }

    // Инициализирует список команд
    public void initTableView() {
        table.setItems(getCommands());
        commandsTypesColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        commandsDescriptionsColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
    }

    // Инициализирует внешний вид окна
    public void init(long testProgramId) {
        table.getItems().clear();
        loadCommands(testProgramId);
    }

    // Создает контекстное меню для шелчка на правую кнопку мыши
    private void initContextMenu() {
        MenuItem menuItemDelete = new MenuItem("Удалить");
        MenuItem menuItemClear = new MenuItem("Удалить все");

        menuItemDelete.setOnAction(event -> deleteCommand());
        menuItemClear.setOnAction(event -> clearCommands());

        contextMenu.getItems().addAll(menuItemDelete, menuItemClear);
    }

    // Удаляет команду
    private void deleteCommand() {
        Command selectedCommand = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(selectedCommand);
        commandsModel.deleteCommand(selectedCommand);
        processController.getStatusBarLine().setStatus("Команда успешно удалена", true);
    }

    // Удаляет все команды
    private void clearCommands() {
        ObservableList<Command> commands = table.getItems();

        for (Command command : commands) {
            commandsModel.deleteCommand(command);
        }

        commands.clear();
        processController.getStatusBarLine().setStatus("Команды успешно удалены", true);
    }

    // Отображает контекстное меню при нажатии на правую кнопку мыши
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

    // Загружает список команд
    public void loadCommands(long testProgramId) {
        List<ru.avem.posum.db.models.Command> commands = getAllCommands();

        for (ru.avem.posum.db.models.Command command : commands) {
            if (command.getTestProgramId() == testProgramId) {
                commandsModel.loadCommand(command);
            }
        }
    }

    // Отображает окно добавления новой команды
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

    // Создает окно добавления новой команды
    private void createDialog() {
        dialog = new Dialog();
        dialog.setTitle("Добавление команды");
        dialog.setHeaderText("Задайте необходымые параметры");
    }

    // Создает кнопки окна добавления новой команды
    private void createButtons() {
        ButtonType add = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(add, cancel);
    }

    // Создает выпадающее меню окна добавления новой команды
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

    // Меняет GUI в зависимости от выбранной команды
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

    // Создает текстовое поле окна добавления новой команды
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

    // Определяет нажатие на клавишу Backspace
    private void listenBackSpaceKey(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        didBackSpacePressed = keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE;
    }

    // Не допускает ввода некорректных параметров
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

    // Добавляет пробелы
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

    // Создает сетку для окна добавления новой команды
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

    // Задает стиль кнопок для окна добавления новой команды
    private void setButtonsStyle() {
        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-font-size: 13px;\n" + "-fx-background-radius: 5px;\n" +
                "\t-fx-border-radius: 5px;"));
    }

    // Обрабатывает нажатие на кнопку окна добавления новой команды
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

    // Проверяет, верно ли заданы параметры команды
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

    // Возвращает описание команды
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

    // Добавляет команду в список
    public void addCommand(String type, String description) {
        commandsModel.addCommand(processController.getTestProgramId(), type, description);
    }

    // Выполняет команды
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

    // Возвращает количество ячеек для объединения при формировании протокола испытаний
    public int getCellsToMerge() {
        return getCommandsHeaders().length;
    }

    // Возвращает список команд
    public ObservableList<Command> getCommands() {
        return commandsModel.getCommands();
    }

    // Возвращает список команд
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

    // Возвращает цвет, которым будут выделяться команды при формировании протокола испытаний
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

    // Возвращает заголовки колонок при формировании протокола испытаний
    public String[] getCommandsHeaders() {
        return new String[]{"Команды", "Параметры"};
    }

    // Возвращает контроллер таймера
    public TimerController getTimerController() {
        return timerController;
    }
}
