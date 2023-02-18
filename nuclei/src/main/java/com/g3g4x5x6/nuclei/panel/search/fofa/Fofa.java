package com.g3g4x5x6.nuclei.panel.search.fofa;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class Fofa extends JPanel {
    private String title;
    private String tips;
    private FlatSVGIcon icon;

    public Fofa() {
        this.title = "Fofa";
        this.tips = "Fofa 网络空间搜索引擎";
        this.icon = new FlatSVGIcon("icons/pinTab.svg");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public FlatSVGIcon getIcon() {
        return icon;
    }

    public void setIcon(FlatSVGIcon icon) {
        this.icon = icon;
    }
}
