package ru.avem.posum.controllers;

import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;

public interface BaseController {
    default void setWindowManager(WindowsManager wm) {
//        throw new NullPointerException("Unimplemented interface method.");
    }

    default void setControllerManager(ControllerManager cm) {
//        throw new NullPointerException("Unimplemented interface method.");
    }

    default void setLTR34ControllerManager(LTR34ControllerManager ltr34ControllerManager) {
//        throw new NullPointerException("Unimplemented interface method.");
    }
}
