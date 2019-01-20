package ru.avem.posum.controllers;

import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public interface BaseController {
    void setWindowManager(WindowsManager wm);

    void setControllerManager(ControllerManager cm);
}