package com.g3g4x5x6.nuclei.action;

import com.g3g4x5x6.NucleiApp;
import com.g3g4x5x6.nuclei.panel.dialog.GroupDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GroupByAction extends AbstractAction {

    public GroupByAction(String name){
        super(name);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        GroupDialog editorDialog = new GroupDialog(NucleiApp.nuclei);
        editorDialog.setLocationRelativeTo(null);
        editorDialog.setVisible(true);
    }
}
