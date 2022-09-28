package com.g3g4x5x6.nuclei.ultils;

import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.NucleiFrame;

import javax.swing.*;
import java.awt.*;

public class DialogUtil {
    private DialogUtil() {

    }

    public static int yesOrNo(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "提示", JOptionPane.YES_NO_OPTION);
    }

    public static void msg(String title, String msg) {
        JOptionPane.showMessageDialog(NucleiApp.nuclei, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void info(String msg) {
        JOptionPane.showMessageDialog(NucleiApp.nuclei, msg, "信息", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(String msg) {
        JOptionPane.showMessageDialog(NucleiApp.nuclei, msg, "警告", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(String msg) {
        JOptionPane.showMessageDialog(NucleiApp.nuclei, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }
}
