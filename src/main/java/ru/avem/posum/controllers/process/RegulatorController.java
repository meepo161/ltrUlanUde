package ru.avem.posum.controllers.process;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.util.Pair;
import kotlin.jvm.Volatile;
import ru.avem.posum.communication.CommunicationModel;
import ru.avem.posum.db.models.Modules;
import ru.avem.posum.hardware.Crate;
import ru.avem.posum.models.process.ChannelModel;
import ru.avem.posum.models.process.RegulatorModel;
import ru.avem.posum.models.settings.LTR34SettingsModel;
import ru.avem.posum.utils.Toast;

import java.io.IOException;
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
    private ChannelModel channelModel;

    @Volatile
    private int redZone = 0;
    @Volatile
    private int yellowZone = 0;
    @Volatile
    private boolean isNeedSmoothStop;
    @Volatile
    boolean isRegulated = false;
    @Volatile
    public static boolean isError = false;

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

    // Задает параметры регулятора
    private void setRegulatorParameters(ChannelModel channel, int channelIndex) {
        double neededAmplitude = Double.parseDouble(channel.getAmplitude());
        double neededDc = Double.parseDouble(channel.getDc());
        double neededFrequency = Double.parseDouble(channel.getFrequency());
        double neededRms = Double.parseDouble(channel.getRms());
        double pValue = Double.parseDouble(channel.getPCoefficient());
        double iValue = Double.parseDouble(channel.getICoefficient());
        double dValue = Double.parseDouble(channel.getDCoefficient());

        regulatorModel[channelIndex].setNeededAmplitude(neededAmplitude);
        regulatorModel[channelIndex].setNeededDc(neededDc);
        regulatorModel[channelIndex].setNeededFrequency(neededFrequency);
        regulatorModel[channelIndex].setNeededRms(neededRms);
        regulatorModel[channelIndex].setPCoefficient(pValue);
        regulatorModel[channelIndex].setICoefficient(iValue);
        regulatorModel[channelIndex].setCoefficient(dValue);
    }

    // Передает регулятору значение отклика
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

    // Возвращает индекс модуля АЦП
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

    // Возвращает скорректированный сигнал для генерации ЦАП'ом
    public double[] getSignalForDac() {
        Optional<Modules> dac;
        dac = getDacModule();
        parseDacSettings(dac.orElse(null));
        assert dac.orElse(null) != null;
        int signalLength = dac.orElse(null).getDataLength();
        double[] signal = new double[signalLength];

        if (checkPause()) {
            return signal;
        }

        int signalType = 6; // постоянный сигнал
        double[] amplitudes = ltr34SettingsModel.getAmplitudes();
        double[] dc = ltr34SettingsModel.getDc();
        double[] frequencies = ltr34SettingsModel.getFrequencies();

        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();
        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
            String adcChannelDescription = linkedChannels.get(channelIndex).getValue().getText();
            String dacChannelDescription = linkedChannels.get(channelIndex).getKey().getText();


            for (ChannelModel channel : channels) {
                if (channel.getName().contains(adcChannelDescription)) {
                    List<Pair<Integer, String>> dacChannels = Modules.getChannelsDescriptions(dac.get());
                    for (Pair<Integer, String> dacChannel : dacChannels) {
                        if (dacChannel.getValue().equals(dacChannelDescription)) {
                            switch ((int) Double.parseDouble(channel.getChosenParameterIndex())) {
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
                                    if (isNeedSmoothStop) {
                                        if (dc[channelIndex] > 0) {
                                            dc[channelIndex] -= 0.3;
                                            if (dc[channelIndex] < 0.5) {
                                                processController.handleStop();
                                                break;
                                            }
                                        }
                                    } else {
                                        if (!isError) {
                                            CommunicationModel.INSTANCE.getMU110Controller().onKM1();
                                        }
                                        double needFrequency = regulatorModel[channelIndex].getNeededFrequency();
                                        double measuringFrequency = regulatorModel[channelIndex].getResponseFrequency();
                                        double needAmplitude = regulatorModel[channelIndex].getNeededAmplitude();
                                        double measuringAmplitude = regulatorModel[channelIndex].getResponseAmplitude();
                                        double needStatic = regulatorModel[channelIndex].getNeededDc();
                                        double measuringStatic = regulatorModel[channelIndex].getResponseDc();

                                        if (dc[channelIndex] < 3) {
                                            dc[channelIndex] += 0.1;
                                        }
                                        if (dc[channelIndex] <= 10 || dc[channelIndex] >= 3) {
                                            if (measuringFrequency < needFrequency * 0.8) {
                                                dc[channelIndex] += 0.08;
                                            } else if (measuringFrequency > needFrequency * 1.2) {
                                                dc[channelIndex] -= 0.08;
                                            } else if (measuringFrequency < needFrequency * 0.90) {
                                                dc[channelIndex] += 0.03;
                                            } else if (measuringFrequency > needFrequency * 1.1) {
                                                dc[channelIndex] -= 0.03;
                                            } else if (measuringFrequency < needFrequency * 0.97) {
                                                dc[channelIndex] += 0.01;
                                                if (!isRegulated) {
                                                    Platform.runLater(() -> {
                                                        Toast.makeText("Регулирование окончено.").show(Toast.ToastType.INFORMATION);
                                                    });
                                                }
                                                isRegulated = true;
                                            } else if (measuringFrequency > needFrequency * 1.03) {
                                                dc[channelIndex] -= 0.01;
                                                if (!isRegulated) {
                                                    Platform.runLater(() -> {
                                                        Toast.makeText("Регулирование окончено.").show(Toast.ToastType.INFORMATION);
                                                    });
                                                }
                                                isRegulated = true;
                                            }

                                            if ((needFrequency > measuringFrequency * 1.2
                                                    || needFrequency < measuringFrequency * 0.8) && isRegulated) {
                                                CommunicationModel.INSTANCE.getMU110Controller().offKM1();
                                                CommunicationModel.INSTANCE.getMU110Controller().offKM2();
                                                isError = true;
                                                Platform.runLater(() -> {
                                                    Toast.makeText("Остановка по частоте. КРАСНАЯ ЗОНА!").show(Toast.ToastType.ERROR);
                                                });
                                                processController.handleStop();
                                                break;
                                            }

                                            if ((needAmplitude > measuringAmplitude * 1.2
                                                    || needAmplitude < measuringAmplitude * 0.8) && isRegulated) {
                                                CommunicationModel.INSTANCE.getMU110Controller().offKM1();
                                                CommunicationModel.INSTANCE.getMU110Controller().offKM2();
                                                isError = true;
                                                Platform.runLater(() -> {
                                                    Toast.makeText("Остановка по амлитуде. КРАСНАЯ ЗОНА!").show(Toast.ToastType.ERROR);
                                                });
                                                processController.handleStop();
                                                break;
                                            }

                                            if ((needStatic > measuringStatic * 1.2
                                                    || needStatic < measuringStatic * 0.8) && isRegulated) {
                                                CommunicationModel.INSTANCE.getMU110Controller().offKM1();
                                                CommunicationModel.INSTANCE.getMU110Controller().offKM2();
                                                isError = true;
                                                Platform.runLater(() -> {
                                                    Toast.makeText("Остановка по амлитуде. КРАСНАЯ ЗОНА!").show(Toast.ToastType.ERROR);
                                                });
                                                processController.handleStop();
                                                break;
                                            }

                                            if ((needFrequency > measuringFrequency * 1.1
                                                    || needFrequency < measuringFrequency * 0.9) && isRegulated) { //от 10% до 20%
                                                yellowZone++;
                                                if (yellowZone == 1) {
                                                    Platform.runLater(() -> {
                                                        Toast.makeText("Вошли в желтую зону по частоте. Отсчитываем 20 секунд.").show(Toast.ToastType.WARNING);
                                                    });
                                                }
                                            }

                                            if ((needAmplitude > measuringAmplitude * 1.1
                                                    || needAmplitude < measuringAmplitude * 0.9) && isRegulated) { //от 10% до 20%
                                                yellowZone++;
                                                if (yellowZone == 1) {
                                                    Platform.runLater(() -> {
                                                        Toast.makeText("Вошли в желтую зону по амлитуде. Отсчитываем 20 секунд.").show(Toast.ToastType.WARNING);
                                                    });
                                                }
                                            }

                                            if ((needStatic > measuringStatic * 1.1
                                                    || needStatic < measuringStatic * 0.9) && isRegulated) { //от 10% до 20%
                                                yellowZone++;
                                                if (yellowZone == 1) {
                                                    Platform.runLater(() -> {
                                                        Toast.makeText("Вошли в желтую зону по статике. Отсчитываем 20 секунд.").show(Toast.ToastType.WARNING);
                                                    });
                                                }

                                                if (yellowZone > 20) {
                                                    isNeedSmoothStop = true;
                                                    Platform.runLater(() -> {
                                                        Toast.makeText("Плавная остановка. Желтая зона.").show(Toast.ToastType.ERROR);
                                                    });
                                                    break;
                                                }

                                                if (needFrequency < measuringFrequency * 1.1 || needFrequency > measuringFrequency * 0.9 ||
                                                        needAmplitude < measuringAmplitude * 1.1 || needAmplitude > measuringAmplitude * 0.9 ||
                                                        needStatic < measuringStatic * 1.1 || needStatic > measuringStatic * 0.9) {
                                                    yellowZone = 0;
                                                }

                                            }

                                        }
                                    }
//                                    break;
                            }
                        }
                    }
                }
            }
            System.out.println(String.format("%s%.3f", "freq = ", dc[channelIndex]));
        }

        ltr34SettingsModel.calculateSignal(signalType);
        System.arraycopy(ltr34SettingsModel.getSignal(), 0, signal, 0, ltr34SettingsModel.getSignal().length);

        return signal;
    }

    // Возвращает состояние процесса испытаний
    private boolean checkPause() {
        return processController.getProcess().isPaused();
    }

    // Считывает настройки ЦАП
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

    // Выполняет плавную остановку
    public void doSmoothStop() {
//        for (ChannelModel channel : channels) {
//            channel.setChosenParameterIndex("0"); // регулировка по амплитуде
//        }
//
//        ObservableList<Pair<CheckBox, CheckBox>> linkedChannels = processController.getLinkingController().getLinkedChannels();
//        for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
//            RegulatorModel regulator = regulatorModel[channelIndex];
//            regulator.setNeededAmplitude(0);
//            regulator.setPCoefficient(1);
//        }
//
//        new Thread(() -> {
//            while (!stopped) {
//                stopped = true;
//                for (int channelIndex = 0; channelIndex < linkedChannels.size(); channelIndex++) {
//                    if (ltr34SettingsModel.getAmplitudes()[channelIndex] != 0) {
//                        stopped = false;
//                    }
//                }
//            }
//        }).start();
        isNeedSmoothStop = true;
    }

    // Возвращает объект модуля ЦАП
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
