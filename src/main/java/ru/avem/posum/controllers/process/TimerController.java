package ru.avem.posum.controllers.process;

import kotlin.Triple;

import java.util.*;

public class TimerController {
    private List<Triple<Timer, TimerTask, Long>> timers = new ArrayList<>();

    // Создает таймер
    public void createTimer(TimerTask timerTask, long delay) {
        timers.add(new Triple<>(new Timer(), timerTask, delay));
    }

    // Запускает таймер
    public void startTimers() {
        for (Triple<Timer, TimerTask, Long> timerParams : timers) {
            timerParams.getFirst().schedule(timerParams.getSecond(), timerParams.getThird());
        }
    }

    // Останавливает таймер
    public void stopTimers() {
        for (Triple<Timer, TimerTask, Long> timerParams : timers) {
            timerParams.getFirst().cancel();
        }
    }

    // Сбрасывает таймер
    public void clearTimers() {
        timers.clear();
    }
}
