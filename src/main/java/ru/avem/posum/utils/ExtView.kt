package ru.avem.posum.utils


import tornadofx.*
import tornadofx.Stylesheet.Companion.label
import javafx.scene.text.FontWeight
import ru.avem.posum.controllers.process.RegulatorController
import ru.avem.posum.controllers.process.TableController
import ru.avem.posum.models.process.RegulatorModel
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px


public class ExtView : View("onFrequency Corrector") {

    override val root = borderpane() {

        top= label(title) {
          //  this.text =
        }
        center = textfield {

        }
    }
}