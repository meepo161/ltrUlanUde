package ru.avem.posum.controllers.Process;

import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.hardware.Module;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.RegulatorModel;
import ru.avem.posum.models.Settings.LTR34SettingsModel;

import java.util.List;
import java.util.Optional;

public class RegulatorController {
    private final int SLOTS = 16;

    private List<ChannelModel> channels;
    private LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
    private List<Modules> modules;
    private ProcessController processController;
    private RegulatorModel[] regulatorModel = new RegulatorModel[SLOTS];
    private String[] typesOfModules = new String[SLOTS];

    public RegulatorController(ProcessController processController) {
        this.processController = processController;
    }

    public void initRegulator(List<ChannelModel> channels) {
        this.channels = channels;
        Optional<Modules> dac = getDacModule();

        if (dac.isPresent()) {
            int channelsCount = dac.get().getChannelsCount();

            for (int channelIndex = 0; channelIndex < channelsCount; channelIndex++) {
                regulatorModel[channelIndex] = new RegulatorModel();
            }

            setRegulatorParameters();
        }
    }

    private void setRegulatorParameters() {
        for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
            ChannelModel channel = channels.get(channelIndex);

            double neededAmplitude = Double.parseDouble(channel.getAmplitude());
            double neededDc = Double.parseDouble(channel.getDc());
            int neededFrequency = Integer.parseInt(channel.getFrequency());
            double neededRms = Double.parseDouble(channel.getRms());
            double pValue = Double.parseDouble(channel.getPcoefficient());
            double iValue = Double.parseDouble(channel.getICoefficient());
            double dValue = Double.parseDouble(channel.getDcoefficient());

            regulatorModel[channelIndex].setNeededAmplitude(neededAmplitude);
            regulatorModel[channelIndex].setNeededDc(neededDc);
            regulatorModel[channelIndex].setNeededFrequency(neededFrequency);
            regulatorModel[channelIndex].setNeededRms(neededRms);
            regulatorModel[channelIndex].setPCoefficient(pValue);
            regulatorModel[channelIndex].setICoefficient(iValue);
            regulatorModel[channelIndex].setCoefficient(dValue);
        }
    }

    public void setResponse() {
        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();

        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
            ChannelModel channel = channels.get(channelIndex);

            if (channel.getName().contains(linkedChannels.get(channelIndex).getValue().getText())) {
                double amplitude = Double.parseDouble(channel.getResponseAmplitude());
                double dc = Double.parseDouble(channel.getResponseDc());
                double frequency = Double.parseDouble(channel.getResponseFrequency());
                double rms = Double.parseDouble(channel.getResponseRms());

                regulatorModel[channelIndex].setResponseAmplitude(amplitude);
                regulatorModel[channelIndex].setResponseDc(dc);
                regulatorModel[channelIndex].setResponseFrequency(frequency);
            }
        }
    }

    public int getDacIndex() {
        int dacIndex = -1;

        for (int moduleIndex = 0; moduleIndex < typesOfModules.length; moduleIndex++) {
            if (typesOfModules[moduleIndex].equals(Crate.LTR34)) {
                dacIndex = moduleIndex;
                break;
            }
        }

        return dacIndex;
    }

    public double[] getSignalForDac() {
        Optional<Modules> dac = getDacModule();
        int signalLength = dac.get().getDataLength();
        double[] signal = new double[signalLength];
        int channelsCount = dac.get().getChannelsCount();

        int signalType = 0; // синусоидальный сигнал
        double[] amplitudes = ltr34SettingsModel.getAmplitudes();
        double[] dc = new double[channelsCount];
        double[] frequencies = ltr34SettingsModel.getFrequencies();

        for (int channelIndex = 0; channelIndex < channelsCount; channelIndex++) {
//            amplitudes[channelIndex] = regulatorModel[channelIndex].getAmplitude();
//            dc[channelIndex] = regulatorModel[channelIndex].getDc();
            frequencies[channelIndex] = regulatorModel[channelIndex].getFrequency();
        }

        ltr34SettingsModel.calculateSignal(signalType);
        System.arraycopy(ltr34SettingsModel.getSignal(), 0, signal, 0, ltr34SettingsModel.getSignal().length);

        return signal;
    }

    private Optional<Modules> getDacModule() {
        Optional<Modules> dac = Optional.empty();

        for (Modules module : modules) {
            if (module.getModuleType().equals(Crate.LTR34)) {
                dac = Optional.of(module);
                break;
            }
        }

        return dac;
    }

    public void setModules(List<Modules> modules) {
        this.modules = modules;
    }

    public void setTypesOfModules(String[] typesOfModules) {
        this.typesOfModules = typesOfModules;
    }
}
