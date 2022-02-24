/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import nl.tue.geometrycore.gui.sidepanel.SideTab;
import nl.tue.geometrycore.gui.sidepanel.TabbedSidePanel;

/**
 *
 * @author wmeulema
 */
public class SidePanel extends TabbedSidePanel {

    private final Data data;
    JLabel label;
    JLabel label2;
    JLabel label3;

    SidePanel(Data data) {
        this.data = data;

        makeIOTab();
    }

    private void makeIOTab() {
        SideTab tab = addTab("IO");
        tab.addButton("Open map", (e) -> data.loadMap());
        tab.addButton("Load Data", (e) -> data.loadData());
        tab.addButton("Load Colors", (e) -> data.loadColors());

        tab.makeSplit(2, 2);
        JSpinner spinCols = tab.addIntegerSpinner(20, 1, Integer.MAX_VALUE, 1, null);
        JSpinner spinRows = tab.addIntegerSpinner(20, 1, Integer.MAX_VALUE, 1, null);

        tab.addButton("Create grid", (e) -> data.createGrid((int) spinCols.getValue(), (int) spinRows.getValue()));
        
        tab.addButton("Assign to grid", (e) -> data.assignToGrid());
        
        tab.makeSplit(2,2);
        tab.addLabel("Range");
//        JSlider rangeSlider = tab.addIntegerSlider(0, 0, 20,null);
        JSpinner spinRange = tab.addIntegerSpinner(0, 0, 100, 1, null);
        tab.addButton("Compute", (e) -> data.improveMI((int)spinRange.getValue()));
        tab.makeSplit(2, 1);
        tab.addCheckbox("Color by data", true, (a,b) -> data.setDataColored(b));
        tab.addCheckbox("Draw extras", true, (a,b) -> data.setDrawExtras(b));
        tab.addCheckbox("Show labels", false, (a,b) -> data.setDrawLabels(b));
        tab.makeSplit(2,1);
        label = tab.addLabel("Morans I: 0");
        label.setSize(this.getSize().width,label.getSize().height);
        tab.makeSplit(2,1);
        label2 = tab.addLabel("Spatial distortion: 0");
        label2.setSize(this.getSize().width,label.getSize().height);
        tab.makeSplit(2,1);
        label3 = tab.addLabel("Prominence: 0");
        label3.setSize(this.getSize().width,label.getSize().height);
        
    }
    
    public void setSpatialDistortion(double d){
        label.setText("Spatial distortion: " + Double.toString(d));
    }
    public void setMoransI(double d){
        label2.setText("Morans I: " + Double.toString(d));
    }
    public void setProminence(double d){
        label3.setText("Prominence: " + Double.toString(d));
    }
}
