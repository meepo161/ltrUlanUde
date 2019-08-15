package ru.avem.posum.utils;

import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

/**
* Класс для отображения уведомлений в виде всплывающих окон
*/

public class Toast {
    private Notifications notifications;

    private Toast(Notifications notifications) {
        this.notifications = notifications;
    }

    public void show(ToastType type) {
        switch (type) {
            case INFORMATION:
                notifications.title("Информация");
                notifications.showInformation();
                break;
            case CONFIRM:
                notifications.title("Подтверждение");
                notifications.showConfirm();
                break;
            case ERROR:
                notifications.title("Ошибка");
                notifications.showError();
                break;
            case WARNING:
                notifications.title("Внимание");
                notifications.showWarning();
                break;
            default:
                notifications.show();
                break;
        }
    }

    public enum ToastType {
        INFORMATION,
        CONFIRM,
        ERROR,
        WARNING
    }

    public static Toast makeText(String text) {
        return new Toast(Notifications.create().text(text).position(Pos.TOP_CENTER));
    }
}
