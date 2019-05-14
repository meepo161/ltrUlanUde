package ru.avem.posum.models.Process;

import javafx.beans.property.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessSample {

    private final StringProperty mainText;

    private final StringProperty group1Color;
    private final IntegerProperty group1GraphNum;
    private final BooleanProperty group1Enable;
    private final IntegerProperty group1Status;
    private final StringProperty group1Value1;
    private final StringProperty group1Value2;
    private final StringProperty group1Value3;
    private final StringProperty group2Color;
    private final IntegerProperty group2GraphNum;
    private final BooleanProperty group2Enable;
    private final IntegerProperty group2Status;
    private final StringProperty group2Value1;
    private final StringProperty group2Value2;
    private final StringProperty group2Value3;
    private final StringProperty group3Color;
    private final IntegerProperty group3GraphNum;
    private final BooleanProperty group3Enable;
    private final IntegerProperty group3Status;
    private final StringProperty group3Value1;
    private final StringProperty group3Value2;
    private final StringProperty group3Value3;
    private final StringProperty group4Color;
    private final IntegerProperty group4GraphNum;
    private final BooleanProperty group4Enable;
    private final IntegerProperty group4Status;
    private final StringProperty group4Value1;
    private final StringProperty group4Value2;
    private final StringProperty group4Value3;
    private final StringProperty group5Color;
    private final IntegerProperty group5GraphNum;
    private final BooleanProperty group5Enable;
    private final IntegerProperty group5Status;
    private final StringProperty group5Value1;
    private final StringProperty group5Value2;
    private final StringProperty group5Value3;
    private final StringProperty group6Color;
    private final IntegerProperty group6GraphNum;
    private final BooleanProperty group6Enable;
    private final IntegerProperty group6Status;
    private final StringProperty group6Value1;
    private final StringProperty group6Value2;
    private final StringProperty group6Value3;

    private List<StringProperty> properties = new ArrayList<>();

    public ProcessSample(String mainText, Color group1Color, Color group2Color, Color group3Color, Color group4Color, Color group5Color, Color group6Color) {
        this.mainText = new SimpleStringProperty(mainText);
        this.group1Color = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (group1Color.getRed() * 255),
                (int) (group1Color.getGreen() * 255),
                (int) (group1Color.getBlue() * 255)));
        this.group1GraphNum = new SimpleIntegerProperty(-1);
        this.group1Enable = new SimpleBooleanProperty( false );
        this.group1Status = new SimpleIntegerProperty(3);
        this.group1Value1 = new SimpleStringProperty("0");
        this.group1Value2 = new SimpleStringProperty("0");
        this.group1Value3 = new SimpleStringProperty("0");
        this.group2Color = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (group2Color.getRed() * 255),
                (int) (group2Color.getGreen() * 255),
                (int) (group2Color.getBlue() * 255)));
        this.group2GraphNum = new SimpleIntegerProperty(-1);
        this.group2Enable = new SimpleBooleanProperty( false );
        this.group2Status = new SimpleIntegerProperty(3);
        this.group2Value1 = new SimpleStringProperty("0");
        this.group2Value2 = new SimpleStringProperty("0");
        this.group2Value3 = new SimpleStringProperty("0");
        this.group3Color = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (group3Color.getRed() * 255),
                (int) (group3Color.getGreen() * 255),
                (int) (group3Color.getBlue() * 255)));
        this.group3GraphNum = new SimpleIntegerProperty(-1);
        this.group3Enable = new SimpleBooleanProperty( false );
        this.group3Status = new SimpleIntegerProperty(3);
        this.group3Value1 = new SimpleStringProperty("0");
        this.group3Value2 = new SimpleStringProperty("0");
        this.group3Value3 = new SimpleStringProperty("0");
        this.group4Color = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (group4Color.getRed() * 255),
                (int) (group4Color.getGreen() * 255),
                (int) (group4Color.getBlue() * 255)));
        this.group4GraphNum = new SimpleIntegerProperty(-1);
        this.group4Enable = new SimpleBooleanProperty( false );
        this.group4Status = new SimpleIntegerProperty(3);
        this.group4Value1 = new SimpleStringProperty("0");
        this.group4Value2 = new SimpleStringProperty("0");
        this.group4Value3 = new SimpleStringProperty("0");
        this.group5Color = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (group5Color.getRed() * 255),
                (int) (group5Color.getGreen() * 255),
                (int) (group5Color.getBlue() * 255)));
        this.group5GraphNum = new SimpleIntegerProperty(-1);
        this.group5Enable = new SimpleBooleanProperty( false );
        this.group5Status = new SimpleIntegerProperty(3);
        this.group5Value1 = new SimpleStringProperty("0");
        this.group5Value2 = new SimpleStringProperty("0");
        this.group5Value3 = new SimpleStringProperty("0");
        this.group6Color = new SimpleStringProperty(String.format("%d, %d, %d",
                (int) (group6Color.getRed() * 255),
                (int) (group6Color.getGreen() * 255),
                (int) (group6Color.getBlue() * 255)));
        this.group6GraphNum = new SimpleIntegerProperty(-1);
        this.group6Enable = new SimpleBooleanProperty( false );
        this.group6Status = new SimpleIntegerProperty(3);
        this.group6Value1 = new SimpleStringProperty("0");
        this.group6Value2 = new SimpleStringProperty("0");
        this.group6Value3 = new SimpleStringProperty("0");
        properties.addAll(Arrays.asList(this.mainText, this.group1Color , this.group1Value1 , this.group1Value2 , this.group1Value3 , this.group2Color , this.group2Value1 , this.group2Value2 , this.group2Value3 ,
                                                       this.group3Color , this.group3Value1 , this.group3Value2 , this.group3Value3 , this.group4Color , this.group4Value1 , this.group4Value2 , this.group4Value3 ,
                                                       this.group5Color , this.group5Value1 , this.group5Value2 , this.group5Value3 , this.group6Color , this.group6Value1 , this.group6Value2 , this.group6Value3 ));
    }

    public ProcessSample(String mainText) {
        this(mainText,Color.white, Color.BLACK, Color.white, Color.white, Color.white, Color.white);
    }
    public ProcessSample() {
        this(null,Color.white, Color.white, Color.white, Color.white, Color.white, Color.white);
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

    public Integer getGroup1GraphNum() { return group1GraphNum.get(); }
    public IntegerProperty group1GraphNumProperty() { return group1GraphNum; }
    public void  setGroup1GraphNum(Integer graphNum) { this.group1GraphNum.set(graphNum); }

    public Boolean getGroup1Enable() { return group1Enable.get(); }
    public BooleanProperty group1EnableProperty() { return group1Enable; }
    public void  setGroup1Enable(Boolean group1Enable) { this.group1Enable.set(group1Enable); }

    public Integer getGroup1Status() { return group1Status.get(); }
    public IntegerProperty group1StatusProperty() { return group1Status; }
    public void  setGroup1Status(Integer group1Status) { this.group1Status.set(group1Status); }

    public String getGroup1Value1() { return group1Value1.get(); }
    public StringProperty group1Value1Property() { return group1Value1; }
    public void setGroup1Value1(String group1Value1) { this.group1Value1.set(group1Value1); }

    public String getGroup1Value2() { return group1Value2.get(); }
    public StringProperty group1Value2Property() { return group1Value2; }
    public void setGroup1Value2(String group1Value2) { this.group1Value2.set(group1Value2); }

    public String getGroup1Value3() { return group1Value3.get(); }
    public StringProperty group1Value3Property() { return group1Value3; }
    public void setGroup1Value3(String group1Value3) { this.group1Value3.set(group1Value3); }

    public String getGroup2Color() { return group2Color.get(); }
    public StringProperty group2ColorProperty() { return group2Color; }
    public void  setGroup2Color(String group2Color) { this.group2Color.set(group2Color); }

    public Integer getGroup2GraphNum() { return group2GraphNum.get(); }
    public IntegerProperty group2GraphNumProperty() { return group2GraphNum; }
    public void  setGroup2GraphNum(Integer graphNum) { this.group2GraphNum.set(graphNum); }

    public Boolean getGroup2Enable() { return group2Enable.get(); }
    public BooleanProperty group2EnableProperty() { return group2Enable; }
    public void  setGroup2Enable(Boolean group2Enable) { this.group2Enable.set(group2Enable); }

    public Integer getGroup2Status() { return group2Status.get(); }
    public IntegerProperty group2StatusProperty() { return group2Status; }
    public void  setGroup2Status(Integer group2Status) { this.group2Status.set(group2Status); }

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

    public Integer getGroup3GraphNum() { return group3GraphNum.get(); }
    public IntegerProperty group3GraphNumProperty() { return group3GraphNum; }
    public void  setGroup3GraphNum(Integer graphNum) { this.group3GraphNum.set(graphNum); }

    public Boolean getGroup3Enable() { return group3Enable.get(); }
    public BooleanProperty group3EnableProperty() { return group3Enable; }
    public void  setGroup3Enable(Boolean group3Enable) { this.group3Enable.set(group3Enable); }

    public Integer getGroup3Status() { return group3Status.get(); }
    public IntegerProperty group3StatusProperty() { return group3Status; }
    public void  setGroup3Status(Integer group3Status) { this.group3Status.set(group3Status); }

    public String getGroup3Value1() { return group3Value1.get(); }
    public StringProperty group3Value1Property() { return group3Value1; }
    public void setGroup3Value1(String group3Value1) { this.group3Value1.set(group3Value1); }

    public String getGroup3Value2() { return group3Value2.get(); }
    public StringProperty group3Value2Property() { return group3Value2; }
    public void setGroup3Value2(String group3Value2) { this.group3Value2.set(group3Value2); }

    public String getGroup3Value3() { return group3Value3.get(); }
    public StringProperty group3Value3Property() { return group3Value3; }
    public void setGroup3Value3(String group3Value3) { this.group3Value3.set(group3Value3); }

    public String getGroup4Color() { return group4Color.get(); }
    public StringProperty group4ColorProperty() { return group4Color; }
    public void  setGroup4Color(String group4Color) { this.group4Color.set(group4Color); }

    public Integer getGroup4GraphNum() { return group4GraphNum.get(); }
    public IntegerProperty group4GraphNumProperty() { return group4GraphNum; }
    public void  setGroup4GraphNum(Integer graphNum) { this.group4GraphNum.set(graphNum); }

    public Boolean getGroup4Enable() { return group4Enable.get(); }
    public BooleanProperty group4EnableProperty() { return group4Enable; }
    public void  setGroup4Enable(Boolean group4Enable) { this.group4Enable.set(group4Enable); }

    public Integer getGroup4Status() { return group4Status.get(); }
    public IntegerProperty group4StatusProperty() { return group4Status; }
    public void  setGroup4Status(Integer group4Status) { this.group4Status.set(group4Status); }

    public String getGroup4Value1() { return group4Value1.get(); }
    public StringProperty group4Value1Property() { return group4Value1; }
    public void setGroup4Value1(String group4Value1) { this.group4Value1.set(group4Value1); }

    public String getGroup4Value2() { return group4Value2.get(); }
    public StringProperty group4Value2Property() { return group4Value2; }
    public void setGroup4Value2(String group4Value2) { this.group4Value2.set(group4Value2); }

    public String getGroup4Value3() { return group4Value3.get(); }
    public StringProperty group4Value3Property() { return group4Value3; }
    public void setGroup4Value3(String group4Value3) { this.group4Value3.set(group4Value3); }

    public String getGroup5Color() { return group5Color.get(); }
    public StringProperty group5ColorProperty() { return group5Color; }
    public void  setGroup5Color(String group5Color) { this.group5Color.set(group5Color); }

    public Integer getGroup5GraphNum() { return group5GraphNum.get(); }
    public IntegerProperty group5GraphNumProperty() { return group5GraphNum; }
    public void  setGroup5GraphNum(Integer graphNum) { this.group5GraphNum.set(graphNum); }

    public Boolean getGroup5Enable() { return group5Enable.get(); }
    public BooleanProperty group5EnableProperty() { return group5Enable; }
    public void  setGroup5Enable(Boolean group5Enable) { this.group5Enable.set(group5Enable); }

    public Integer getGroup5Status() { return group5Status.get(); }
    public IntegerProperty group5StatusProperty() { return group5Status; }
    public void  setGroup5Status(Integer group5Status) { this.group5Status.set(group5Status); }

    public String getGroup5Value1() { return group5Value1.get(); }
    public StringProperty group5Value1Property() { return group5Value1; }
    public void setGroup5Value1(String group5Value1) { this.group5Value1.set(group5Value1); }

    public String getGroup5Value2() { return group5Value2.get(); }
    public StringProperty group5Value2Property() { return group5Value2; }
    public void setGroup5Value2(String group5Value2) { this.group5Value2.set(group5Value2); }

    public String getGroup5Value3() { return group5Value3.get(); }
    public StringProperty group5Value3Property() { return group5Value3; }
    public void setGroup5Value3(String group5Value3) { this.group5Value3.set(group5Value3); }

    public String getGroup6Color() { return group6Color.get(); }
    public StringProperty group6ColorProperty() { return group6Color; }
    public void  setGroup6Color(String group6Color) { this.group6Color.set(group6Color); }

    public Integer getGroup6GraphNum() { return group6GraphNum.get(); }
    public IntegerProperty group6GraphNumProperty() { return group6GraphNum; }
    public void  setGroup6GraphNum(Integer graphNum) { this.group6GraphNum.set(graphNum); }

    public Boolean getGroup6Enable() { return group6Enable.get(); }
    public BooleanProperty group6EnableProperty() { return group6Enable; }
    public void  setGroup6Enable(Boolean group6Enable) { this.group6Enable.set(group6Enable); }

    public Integer getGroup6Status() { return group6Status.get(); }
    public IntegerProperty group6StatusProperty() { return group6Status; }
    public void  setGroup6Status(Integer group6Status) { this.group6Status.set(group6Status); }

    public String getGroup6Value1() { return group6Value1.get(); }
    public StringProperty group6Value1Property() { return group6Value1; }
    public void setGroup6Value1(String group6Value1) { this.group6Value1.set(group6Value1); }

    public String getGroup6Value2() { return group6Value2.get(); }
    public StringProperty group6Value2Property() { return group6Value2; }
    public void setGroup6Value2(String group6Value2) { this.group6Value2.set(group6Value2); }

    public String getGroup6Value3() { return group6Value3.get(); }
    public StringProperty group6Value3Property() { return group6Value3; }
    public void setGroup6Value3(String group6Value3) { this.group6Value3.set(group6Value3); }
}
