package com.g3g4x5x6.nuclei.panel.search;

import com.g3g4x5x6.nuclei.panel.search.fofa.Fofa;
import com.g3g4x5x6.nuclei.panel.search.hunter.Hunter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT;

@Slf4j
public class SearchTabbedPanel extends JTabbedPane {
    private Fofa fofa = new Fofa();
    private Hunter hunter = new Hunter();

    public SearchTabbedPanel() {
        customComponents();

        this.addTab(fofa.getTitle(), fofa.getIcon(), fofa, fofa.getTips());
        this.addTab(hunter.getTitle(), hunter.getIcon(), hunter, hunter.getTips());
    }


    private void customComponents() {
        JToolBar trailing;
        trailing = new JToolBar();
        trailing.setFloatable(false);
        trailing.setBorder(null);

        // TODO add tool item for this panel

        this.putClientProperty(TABBED_PANE_TRAILING_COMPONENT, trailing);
    }

}
