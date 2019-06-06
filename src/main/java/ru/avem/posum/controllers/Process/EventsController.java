package ru.avem.posum.controllers.Process;

import javafx.scene.control.*;
import ru.avem.posum.models.Process.Events;
import ru.avem.posum.models.Process.EventsModel;

import java.util.Optional;

public class EventsController {
    private EventsModel eventModel = new EventsModel();

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
        result.ifPresent(eventText -> eventModel.setEvent(eventText));
    }

    public void setEventsColors(TableView<Events> newTableEvent) {
        newTableEvent.setRowFactory((TableView<Events> paramP) -> new TableRow<Events>() {
            @Override
            protected void updateItem(Events row, boolean paramBoolean) {
                if (row != null) {
                    switch (row.getStatus()) {
                        case "ERROR":
                            setStyle("-fx-background-color: #ff4500; -fx-text-background-color: black;");
                            break;
                        case "LOG":
                            setStyle("-fx-background-color: #f0f8ff; -fx-text-background-color: black;");
                            break;
                        case "OK":
                            setStyle("-fx-background-color: #92cd74; -fx-text-background-color: black;");
                            break;
                        case "WARNING":
                            setStyle("-fx-background-color: #f0e68c; -fx-text-background-color: black;");
                            break;
                        default:
                            setStyle(null);
                    }
                } else {
                    setStyle(null);
                }
                super.updateItem(row, paramBoolean);
            }
        });
    }

    public EventsModel getEventModel() {
        return eventModel;
    }
}
