package ru.avem.posum.controllers.process;

import kotlin.Triple;

import java.util.*;

public class TimerController {
    private List<Triple<Timer, TimerTask, Long>> timers = new ArrayList<>();

    public void createTimer(TimerTask timerTask, long delay) {
        timers.add(new Triple<>(new Timer(), timerTask, delay));
    }

    public void startTimers() {
        for (Triple<Timer, TimerTask, Long> timerParams : timers) {
            timerParams.getFirst().schedule(timerParams.getSecond(), timerParams.getThird());
        }
    }

    public void stopTimers() {
        for (Triple<Timer, TimerTask, Long> timerParams : timers) {
            timerParams.getFirst().cancel();
        }
    }

    public void clearTimers() {
        timers.clear();
    }
}
