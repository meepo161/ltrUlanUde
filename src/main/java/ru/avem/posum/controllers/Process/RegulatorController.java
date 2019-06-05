package ru.avem.posum.controllers.Process;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.util.Pair;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.RegulatorModel;
import ru.avem.posum.models.Settings.LTR34SettingsModel;

import java.util.List;
import java.util.Optional;

public class RegulatorController {
    private final int SLOTS = 16;

    private List<ChannelModel> channels;
    private boolean firstStart;
    private LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
    private List<Modules> modules;
    private ProcessController processController;
    private RegulatorModel[] regulatorModel = new RegulatorModel[SLOTS];
    private boolean stopped;
    private String[] typesOfModules = new String[SLOTS];

    public RegulatorController(ProcessController processController) {
        this.processController = processController;
    }

    public void initRegulator(List<ChannelModel> channels) {
        this.channels = channels;

        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();
        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
            for (ChannelModel channel : channels) {
                if (channel.getName().contains(linkedChannels.get(channelIndex).getValue().getText())) {
                    regulatorModel[channelIndex] = new RegulatorModel();
                    setRegulatorParameters(channel, channelIndex);
                }
            }
        }
    }

    private void setRegulatorParameters(ChannelModel channel, int channelIndex) {
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

    public void setResponse() {
        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();

        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
            for (ChannelModel channel : channels) {
                if (channel.getName().contains(linkedChannels.get(channelIndex).getValue().getText())) {
                    double amplitude = Double.parseDouble(channel.getResponseAmplitude());
                    double dc = Double.parseDouble(channel.getResponseDc());
                    double frequency = Double.parseDouble(channel.getResponseFrequency());

                    regulatorModel[channelIndex].setResponseAmplitude(amplitude);
                    regulatorModel[channelIndex].setResponseDc(dc);
                    regulatorModel[channelIndex].setResponseFrequency(frequency);
                }

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
        int signalType = 0; // синусоидальный сигнал
        double[] amplitudes = ltr34SettingsModel.getAmplitudes();
        double[] dc = ltr34SettingsModel.getDc();
        double[] frequencies = ltr34SettingsModel.getFrequencies();

        Optional<Modules> dac = getDacModule();
        parseDacSettings(dac.get());

        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();
        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
            String adcChannelDescription = linkedChannels.get(channelIndex).getValue().getText();
            String dacChannelDescription = linkedChannels.get(channelIndex).getKey().getText();


            for (ChannelModel channel : channels) {
                if (channel.getName().contains(adcChannelDescription)) {
                    List<Pair<Integer, String>> dacChannels = Modules.getChannelsDescriptions(dac.get());
                    for (Pair<Integer, String> dacChannel : dacChannels) {
                        if (dacChannel.getValue().equals(dacChannelDescription)) {
                            switch (Integer.parseInt(channel.getChosenParameterIndex())) {
                                case 0:
                                    double newAmplitude = regulatorModel[channelIndex].getAmplitude();
                                    if ((newAmplitude + amplitudes[channelIndex] + dc[channelIndex]) < 10) { // ограничение максимального напряжения, подаваемого с ЦАП
                                        if (newAmplitude + amplitudes[channelIndex] < 0) {
                                            amplitudes[channelIndex] = 0;
                                        } else {
                                            amplitudes[channelIndex] += newAmplitude;
                                        }
                                    }
                                    break;
                                case 1:
                                    double newDc = regulatorModel[channelIndex].getAmplitude();
                                    if ((newDc + dc[channelIndex] + amplitudes[channelIndex]) < 10) {  // ограничение максимального напряжения, подаваемого с ЦАП
                                        if (newDc + dc[channelIndex] < 0) {
                                            dc[channelIndex] = 0;
                                        } else {
                                            dc[channelIndex] += newDc;
                                        }
                                    }
                                    break;
                                case 2:
                                    double newFrequency = regulatorModel[channelIndex].getFrequency();
                                    if (newFrequency + frequencies[channelIndex] < 0) {
                                        frequencies[channelIndex] = 0;
                                    } else {
                                        frequencies[channelIndex] += newFrequency;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }

        int signalLength = dac.get().getDataLength();
        double[] signal = new double[signalLength];
        ltr34SettingsModel.calculateSignal(signalType);
        System.arraycopy(ltr34SettingsModel.getSignal(), 0, signal, 0, ltr34SettingsModel.getSignal().length);

        return signal;
    }

    private void parseDacSettings(Modules dac) {
        if (firstStart) {
            double[] amplitudes = ltr34SettingsModel.getAmplitudes();
            double[] dc = ltr34SettingsModel.getDc();
            double[] frequencies = ltr34SettingsModel.getFrequencies();

            double[] parsedAmplitudes = Modules.getAmplitudes(dac);
            double[] parsedDc = Modules.getDc(dac);
            double[] parsedFrequencies = Modules.getFrequencies(dac);

            System.arraycopy(parsedAmplitudes, 0, amplitudes, 0, parsedAmplitudes.length);
            System.arraycopy(parsedDc, 0, dc, 0, parsedDc.length);
            System.arraycopy(parsedFrequencies, 0, frequencies, 0, parsedFrequencies.length);
        }

        firstStart = false;
    }

    public void doSmoothStop() {
        for (ChannelModel channel : channels) {
            channel.setChosenParameterIndex("0"); // регулировка по амплитуде
        }

        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();
        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
            RegulatorModel regulator = regulatorModel[channelIndex];
            regulator.setNeededAmplitude(0);
            regulator.setPCoefficient(1);
        }

        new Thread(() -> {
            while (!stopped) {
                stopped = true;
                for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
                    if (ltr34SettingsModel.getAmplitudes()[channelIndex] != 0) {
                        stopped = false;
                    }
                }
            }
        }).start();
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

    public boolean isStopped() {
        return stopped;
    }

    public void setFirstStart(boolean firstStart) {
        this.firstStart = firstStart;
    }

    public void setModules(List<Modules> modules) {
        this.modules = modules;
    }

    public void setTypesOfModules(String[] typesOfModules) {
        this.typesOfModules = typesOfModules;
    }
}
