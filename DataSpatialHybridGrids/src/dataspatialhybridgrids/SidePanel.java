/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import javax.swing.JSpinner;
import nl.tue.geometrycore.gui.sidepanel.SideTab;
import nl.tue.geometrycore.gui.sidepanel.TabbedSidePanel;

/**
 *
 * @author wmeulema
 */
public class SidePanel extends TabbedSidePanel {

    private final Data data;

    SidePanel(Data data) {
        this.data = data;

        makeIOTab();
    }

    private void makeIOTab() {
        SideTab tab = addTab("IO");
        tab.addButton("Open map [O]", (e) -> data.load());

        tab.makeSplit(2, 2);
        JSpinner spinCols = tab.addIntegerSpinner(20, 1, Integer.MAX_VALUE, 1, null);
        JSpinner spinRows = tab.addIntegerSpinner(20, 1, Integer.MAX_VALUE, 1, null);

        tab.addButton("Create grid", (e) -> data.createGrid((int) spinCols.getValue(), (int) spinRows.getValue()));
    }
}
