package ru.avem.posum.models.Process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.db.CommandsRepository;
import ru.avem.posum.db.EventsRepository;

public class EventsModel {
    private ObservableList<Event> events = FXCollections.observableArrayList();

    public void addEvent(long testProgramId, String description, EventsTypes status, long mills) {
        ru.avem.posum.db.models.Event event = new ru.avem.posum.db.models.Event(testProgramId, description, status.toString(), mills);
        EventsRepository.insertEvent(event);
        events.add(new Event(event.getId(), event.getDescription(), event.getStatus(), event.getTime()));
    }

    public void deleteEvent(Event event) {
        for (ru.avem.posum.db.models.Event dbEvent : EventsRepository.getAllEvents()) {
            if (dbEvent.getId() == event.getId()) {
                EventsRepository.deleteEvent(dbEvent);
            }
        }
    }

    public void loadEvent(ru.avem.posum.db.models.Event event) {
        events.add(new Event(event.getId(), event.getDescription(), event.getStatus(), event.getTime()));
    }

    public ObservableList<Event> getEvents() {
        return events;
    }

    public void setEvent(long testProgramId, String description) {
        this.addEvent(testProgramId, description, EventsTypes.LOG, System.currentTimeMillis());
    }
}
