package ru.avem.posum.controllers.Process;

import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.Process.ChannelModel;
import ru.avem.posum.models.Process.RegulatorModel;
import ru.avem.posum.models.Settings.LTR34SettingsModel;

import java.util.List;

public class RegulatorController {
    private final int SLOTS = 16;

    private List<ChannelModel> channels;
    private LTR34SettingsModel ltr34SettingsModel = new LTR34SettingsModel();
    private List<Modules> modules;
    private RegulatorModel[] regulatorModel = new RegulatorModel[SLOTS];
    private String[] typesOfModules = new String[SLOTS];

    public void initRegulator(List<ChannelModel> channels) {
        this.channels = channels;
        Modules dac = getDacModule();
        int channelsCount = dac.getChannelsCount();

        for (int channelIndex = 0; channelIndex < channelsCount; channelIndex++) {
            regulatorModel[channelIndex] = new RegulatorModel();
        }

        setRegulatorParameters();
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
        for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
            ChannelModel channel = channels.get(channelIndex);

            double amplitude = Double.parseDouble(channel.getResponseAmplitude());
            double dc = Double.parseDouble(channel.getResponseDc());
            double frequency = Double.parseDouble(channel.getResponseFrequency());
            double rms = Double.parseDouble(channel.getResponseRms());

            regulatorModel[channelIndex].setResponseAmplitude(amplitude);
            regulatorModel[channelIndex].setResponseDc(dc);
            regulatorModel[channelIndex].setResponseFrequency(frequency);
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
        Modules dac = getDacModule();
        int signalLength = dac.getDataLength();
        double[] signal = new double[signalLength];
        int channelsCount = dac.getChannelsCount();

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

    private Modules getDacModule() {
        Modules dac = new Modules();

        for (Modules module : modules) {
            if (module.getModuleType().equals(Crate.LTR34)) {
                dac = module;
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
