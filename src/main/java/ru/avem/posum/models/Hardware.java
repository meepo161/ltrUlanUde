package ru.avem.posum.models;

import ru.avem.posum.controllers.IMainController;
import ru.avem.st49.hardware.Crate;

public class Hardware {
    private static Hardware instance = new Hardware();

    private Crate crate;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN1;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN2;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN3;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN4;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN5;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN6;
    private ru.avem.posum.models.LTR212Model ltr212ModuleN7;
    private ru.avem.posum.models.LTR24Model ltr24ModuleN8;
    private ru.avem.posum.models.LTR24Model ltr24ModuleN9;
    private ru.avem.posum.models.LTR24Model ltr24ModuleN10;
    private ru.avem.posum.models.LTR24Model ltr24ModuleN11;
    private ru.avem.posum.models.LTR34Model ltr34ModuleN12;

    private Hardware() {
    }

    public void setiMainViewController(IMainController iMainController) {
        crate = new Crate(iMainController);
//        ltr212ModuleN1 = new LTR212Model(iMainController, 1);
//        ltr212ModuleN2 = new LTR212Model(iMainController, 2);
//        ltr212ModuleN3 = new LTR212Model(iMainController, 3);
//        ltr212ModuleN4 = new LTR212Model(iMainController, 4);
//        ltr212ModuleN5 = new LTR212Model(iMainController, 5);
//        ltr212ModuleN6 = new LTR212Model(iMainController, 6);
//        ltr212ModuleN7 = new LTR212Model(iMainController, 7);
//        ltr24ModuleN8  = new LTR24Model(iMainController, 8);
//        ltr24ModuleN9  = new LTR24Model(iMainController, 9);
//        ltr24ModuleN10 = new LTR24Model(iMainController, 10);
//        ltr24ModuleN11 = new LTR24Model(iMainController, 11);
//        ltr34ModuleN12 = new LTR34Model(iMainController, 12);
    }

    public static Hardware getInstance() {
        return instance;
    }

    public Crate getCrate() {
        return crate;
    }

    public LTR212Model getLtr212ModuleN1() {
        return ltr212ModuleN1;
    }

    public LTR212Model getLtr212ModuleN2() {
        return ltr212ModuleN2;
    }

    public LTR212Model getLtr212ModuleN3() {
        return ltr212ModuleN3;
    }

    public LTR212Model getLtr212ModuleN4() {
        return ltr212ModuleN4;
    }

    public LTR212Model getLtr212ModuleN5() {
        return ltr212ModuleN5;
    }

    public LTR212Model getLtr212ModuleN6() {
        return ltr212ModuleN6;
    }

    public LTR212Model getLtr212ModuleN7() {
        return ltr212ModuleN7;
    }

    public LTR24Model getLtr24ModuleN8() {
        return ltr24ModuleN8;
    }

    public LTR24Model getLtr24ModuleN9() {
        return ltr24ModuleN9;
    }

    public LTR24Model getLtr24ModuleN10() {
        return ltr24ModuleN10;
    }

    public LTR24Model getLtr24ModuleN11() {
        return ltr24ModuleN11;
    }

    public LTR34Model getLtr34ModuleN12() {
        return ltr34ModuleN12;
    }
}
