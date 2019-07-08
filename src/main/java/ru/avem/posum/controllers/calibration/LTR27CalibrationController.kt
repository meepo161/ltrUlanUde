package ru.avem.posum.controllers.calibration

import ru.avem.posum.ControllerManager
import ru.avem.posum.WindowsManager
import ru.avem.posum.controllers.BaseController

class LTR27CalibrationController: BaseController {
    private lateinit var cm: ControllerManager
    private lateinit var wm: WindowsManager

    fun handleAddPoint() {

    }

    fun handleSaveButton() {

    }

    fun handleBackButton() {

    }

    override fun setControllerManager(cm: ControllerManager) {
        this.cm = cm
    }

    override fun setWindowManager(wm: WindowsManager) {
        this.wm = wm
    }
}