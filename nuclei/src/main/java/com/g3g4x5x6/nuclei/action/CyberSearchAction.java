package com.g3g4x5x6.nuclei.action;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.g3g4x5x6.nuclei.panel.search.SearchTabbedPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.g3g4x5x6.nuclei.NucleiFrame.frameTabbedPane;

public class CyberSearchAction extends AbstractAction {

    private final SearchTabbedPanel searchTabbedPanel = new SearchTabbedPanel();

    public CyberSearchAction(String name) {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frameTabbedPane.insertTab("Searching", new FlatSVGIcon("icons/search.svg"), searchTabbedPanel, "网络空间搜索", 4);
        frameTabbedPane.setSelectedIndex(4);
    }
}
