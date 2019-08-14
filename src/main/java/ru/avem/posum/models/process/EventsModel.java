package ru.avem.posum.models.process;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.avem.posum.db.EventsRepository;

/**
 * Класс для работы с событиями
 */

public class EventsModel {
    private ObservableList<Event> events = FXCollections.observableArrayList();

    // Добавляет событие в список и записывает его в базу данных
    public void addEvent(long testProgramId, String description, EventsTypes status) {
        ru.avem.posum.db.models.Event event = new ru.avem.posum.db.models.Event(testProgramId, description, status.toString(), System.currentTimeMillis());
        EventsRepository.insertEvent(event);
        events.add(new Event(event.getId(), event.getDescription(), event.getStatus(), event.getTime()));
    }

    // Удаляет событие из базы данных
    public void deleteEvent(Event event) {
        for (ru.avem.posum.db.models.Event dbEvent : EventsRepository.getAllEvents()) {
            if (dbEvent.getId() == event.getId()) {
                EventsRepository.deleteEvent(dbEvent);
            }
        }
    }

    // Загружает событие из базы данных
    public void loadEvent(ru.avem.posum.db.models.Event event) {
        events.add(new Event(event.getId(), event.getDescription(), event.getStatus(), event.getTime()));
    }

    // Возвращает журнал событий
    public ObservableList<Event> getEvents() {
        return events;
    }

    // Добавляет событие
    public void setEvent(long testProgramId, String description) {
        this.addEvent(testProgramId, description, EventsTypes.LOG);
    }
}
