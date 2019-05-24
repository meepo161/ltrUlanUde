package ru.avem.posum.controllers.Process;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import ru.avem.posum.models.Process.Events;
import ru.avem.posum.models.Process.EventsModel;

public class EventsController {
    private EventsModel eventModel = new EventsModel();

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
