package ru.avem.posum.controllers.Process;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.avem.posum.ControllerManager;
import ru.avem.posum.WindowsManager;
import ru.avem.posum.controllers.BaseController;
import ru.avem.posum.models.Process.ProgramModel;
import ru.avem.posum.models.Process.ProcessSampleModel;

public class ProgramController implements BaseController {
    private AnchorPane mainPanel;
    private ControllerManager cm;
    private ProcessSampleModel processSampleModel;
    private ProgramModel programModel = new ProgramModel();
    private ToolBar toolbarSettings;
    private VBox topPanel;
    private WindowsManager wm;

    public ProgramController(AnchorPane mainPanel, ProcessSampleModel processSampleModel, ToolBar toolbarSettings, VBox topPanel) {
        this.mainPanel = mainPanel;
        this.processSampleModel = processSampleModel;
        this.toolbarSettings = toolbarSettings;
        this.topPanel = topPanel;

        topPanel.setPrefHeight(mainPanel.getMaxHeight());
        toolbarSettings.setVisible(false);
    }

    public void toggleSettingsPanel() {
        int TOOLBAR_HEIGHT = 110;
        boolean hide = programModel.checkToProgramClicksCounter();
        double neededHeight = hide ? mainPanel.getMaxHeight() : mainPanel.getMaxHeight() + TOOLBAR_HEIGHT;

        toolbarSettings.setVisible(!hide);
        topPanel.setPrefHeight(neededHeight);
        topPanel.maxHeight(neededHeight);
        topPanel.minHeight(neededHeight);
        processSampleModel.fitTable();
    }

    public void loadDacAndAdcChannels() {

    }


    @Override
    public void setControllerManager(ControllerManager cm) {
        this.cm = cm;
    }

    @Override
    public void setWindowManager(WindowsManager wm) {
        this.wm = wm;
    }
}
