package ru.avem.posum.models;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessSample {

    private final StringProperty mainText;

    private final StringProperty group1Color;
    private final StringProperty group1Value1;
    private final StringProperty group1Value2;
    private final StringProperty group1Value3;
    private final StringProperty group1Value4;
    private final StringProperty group1Value5;
    private final StringProperty group1Value6;
    private final StringProperty group2Color;
    private final StringProperty group2Value1;
    private final StringProperty group2Value2;
    private final StringProperty group2Value3;
    private final StringProperty group3Color;
    private final StringProperty group4Color;


    private List<StringProperty> properties = new ArrayList<>();

    public ProcessSample(String mainText, int group1Color, int group2Color, int group3Color, int group4Color) {
        this.mainText = new SimpleStringProperty(mainText);
        this.group1Color = new SimpleStringProperty(Integer.toString(group1Color));
        this.group1Value1 = new SimpleStringProperty("0");
        this.group1Value2 = new SimpleStringProperty("0");
        this.group1Value3 = new SimpleStringProperty("0");
        this.group1Value4 = new SimpleStringProperty("0");
        this.group1Value5 = new SimpleStringProperty("0");
        this.group1Value6 = new SimpleStringProperty("0");
        this.group2Color = new SimpleStringProperty(Integer.toString(group2Color));
        this.group2Value1 = new SimpleStringProperty("0");
        this.group2Value2 = new SimpleStringProperty("0");
        this.group2Value3 = new SimpleStringProperty("0");
        this.group3Color = new SimpleStringProperty(Integer.toString(group3Color));
        this.group4Color = new SimpleStringProperty(Integer.toString(group4Color));
        properties.addAll(Arrays.asList(this.mainText, this.group1Color , this.group1Value1 , this.group1Value2 , this.group1Value3 , this.group1Value4 , this.group1Value5 , this.group1Value6 , this.group2Color , this.group2Value1 , this.group2Value2 , this.group2Value3 , this.group3Color , this.group4Color));
    }

    public ProcessSample() {
        this(null,0, 0, 0, 0);
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

    public String getGroup1Color() { return group1Color.get(); }
    public StringProperty group1ColorProperty() { return group1Color; }
    public void  setGroup1Color(String group1Color) { this.group1Color.set(group1Color); }

    public String getGroup1Value1() { return group1Value1.get(); }
    public StringProperty group1Value1Property() { return group1Value1; }
    public void setGroup1Value1(String group1Value1) { this.group1Value1.set(group1Value1); }

    public String getGroup1Value2() { return group1Value2.get(); }
    public StringProperty group1Value2Property() { return group1Value2; }
    public void setGroup1Value2(String group1Value2) { this.group1Value2.set(group1Value2); }

    public String getGroup1Value3() { return group1Value3.get(); }
    public StringProperty group1Value3Property() { return group1Value3; }
    public void setGroup1Value3(String group1Value3) { this.group1Value3.set(group1Value3); }

    public String getGroup1Value4() { return group1Value4.get(); }
    public StringProperty group1Value4Property() { return group1Value4; }
    public void setGroup1Value4(String group1Value4) { this.group1Value4.set(group1Value4); }

    public String getGroup1Value5() { return group1Value5.get(); }
    public StringProperty group1Value5Property() { return group1Value5; }
    public void setGroup1Value5(String group1Value5) { this.group1Value5.set(group1Value5); }

    public String getGroup1Value6() { return group1Value6.get(); }
    public StringProperty group1Value6Property() { return group1Value6; }
    public void setGroup1Value6(String group1Value6) { this.group1Value6.set(group1Value6); }

    public String getGroup2Color() { return group2Color.get(); }
    public StringProperty group2ColorProperty() { return group2Color; }
    public void  setGroup2Color(String group2Color) { this.group2Color.set(group2Color); }

    public String getGroup2Value1() { return group2Value1.get(); }
    public StringProperty group2Value1Property() { return group2Value1; }
    public void setGroup2Value1(String group2Value1) { this.group2Value1.set(group2Value1); }

    public String getGroup2Value2() { return group2Value2.get(); }
    public StringProperty group2Value2Property() { return group2Value2; }
    public void setGroup2Value2(String group2Value2) { this.group2Value2.set(group2Value2); }

    public String getGroup2Value3() { return group2Value3.get(); }
    public StringProperty group2Value3Property() { return group2Value3; }
    public void setGroup2Value3(String group2Value3) { this.group2Value3.set(group2Value3); }

    public String getGroup3Color() { return group3Color.get(); }
    public StringProperty group3ColorProperty() { return group3Color; }
    public void  setGroup3Color(String group3Color) { this.group3Color.set(group3Color); }

    public String getGroup4Color() { return group4Color.get(); }
    public StringProperty group4ColorProperty() { return group4Color; }
    public void  setGroup4Color(String group4Color) { this.group4Color.set(group4Color); }

}
