package ru.avem.posum.controllers;

import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public interface BaseController {
    void setWindowManager(WindowsManager wm);

    default void setControllerManager(ControllerManager cm) {

    }
}
