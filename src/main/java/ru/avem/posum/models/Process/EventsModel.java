package ru.avem.posum.models.Process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import ru.avem.posum.db.EventRepository;
import ru.avem.posum.db.models.Event;

import java.util.List;

public class EventsModel {
    private ObservableList<Events> events = FXCollections.observableArrayList();
    private long testId;

    public void addEvent(String description, EventsTypes status) {
        this.setEvent(description, status, System.currentTimeMillis());
    }

    public ObservableList<Events> getEvents() {
        return events;
    }

    public void setEvent(String description, EventsTypes status, long millis) {
        Event event = new Event(testId, description, status.toString(), millis);
        EventRepository.insertEvent(event);
        events.add(new Events(event.getTime(), event.getDescription(), event.getStatus()));
    }

    public void setEvent(String description) {
        this.setEvent(description, EventsTypes.LOG, System.currentTimeMillis());
    }
}
