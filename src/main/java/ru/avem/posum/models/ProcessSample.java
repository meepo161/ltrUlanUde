package ru.avem.posum.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessSample {

    private final StringProperty mainText;

    private List<StringProperty> properties = new ArrayList<>();

    public ProcessSample(String mainText) {
        this.mainText = new SimpleStringProperty(mainText);
        properties.addAll(Arrays.asList(this.mainText ));
    }

    public ProcessSample() {
        this(null);
    }

    public String getMainText() {
        return mainText.get();
    }
    public StringProperty mainTextProperty() {
        return mainText;
    }
    public void setMainText(String description) {
        this.mainText.set(description);
    }
}
