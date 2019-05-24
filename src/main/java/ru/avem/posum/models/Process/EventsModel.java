package ru.avem.posum.models.Process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import ru.avem.posum.db.EventRepository;
import ru.avem.posum.db.models.Event;

import java.util.List;

public class EventsModel {

    private ObservableList<Events> eventData = FXCollections.observableArrayList();
    private long testId;

    public EventsModel( ) {
        this.testId = 0;
        loadData(this.testId);
    }

    public void loadData(long idTest) {
        List<Event> allNeedEvents = EventRepository.getEventsByTestId(idTest);

        for (int i = 0; i < allNeedEvents.size(); i++) {
            Event event = allNeedEvents.get(i);
            eventData.add(new Events(event.getTime(), event.getDescription(), event.getStatus()));
        }

        eventData.add(new Events("12:10:10", "Test_OK", EventsTypes.OK));
        eventData.add(new Events("10:10:10", "Test_LOG", EventsTypes.LOG));
        eventData.add(new Events("14:10:10", "Test_ERROR", EventsTypes.ERROR));
        eventData.add(new Events("16:10:10", "Test_WARNING", EventsTypes.WARNING));
    }

    public void SetEventsTableFunction(TableView<Events> newTableEvent) {
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

    public ObservableList<Events> getEventData() {
        return eventData;
    }

    public void initEventData(TableView<Events> newTableEvent) {
        newTableEvent.setItems(this.getEventData());
    }

    public void setTestId(long testId) {
        this.testId = testId;
        loadData(this.testId);
    }

    public void setEvent(String description, EventsTypes status, long millis) {
        Event event = new Event(testId, description, status.toString(), millis);
        EventRepository.insertEvent(event);
        eventData.add(new Events(event.getTime(), event.getDescription(), event.getStatus()));
    }

    public void setEvent(String description, EventsTypes status) {
        this.setEvent(description, status, System.currentTimeMillis());
    }

    public void setEvent(String description) {
        this.setEvent(description, EventsTypes.LOG, System.currentTimeMillis());
    }


}
