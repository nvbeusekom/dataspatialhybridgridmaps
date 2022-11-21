/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import nl.tue.geometrycore.gui.sidepanel.NewValueListener;
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
    JRadioButton[] coloring;

    SidePanel(Data data) {
        this.data = data;

        makeIOTab();
    }

    private void makeIOTab() {
        SideTab tab = addTab("IO");
        tab.addButton("Open map", (e) -> data.loadMap());
        tab.addButton("Load Data", (e) -> data.loadData());
        String[] options = {"NL","UK","US"};
        coloring = tab.addRadioButtonList(options, "NL", (a,s) -> data.loadColors(s));
        

        tab.makeSplit(2, 2);
        JSpinner spinCols = tab.addIntegerSpinner(10, 1, Integer.MAX_VALUE, 1, null);
        JSpinner spinRows = tab.addIntegerSpinner(10, 1, Integer.MAX_VALUE, 1, null);

        tab.addButton("Create grid", (e) -> data.createGrid((int) spinCols.getValue(), (int) spinRows.getValue()));
        
        tab.addButton("Assign to grid (spatial)", (e) -> data.assignToGridSpatial());
        tab.addButton("Assign to grid (data-sorted)", (e) -> data.assignToGridData());
        
        tab.addButton("Save to Ipe", (e) -> data.saveIPE());
        
        tab.makeSplit(2,2);
        tab.addLabel("Range");
//        JSlider rangeSlider = tab.addIntegerSlider(0, 0, 20,null);
        JSpinner spinRange = tab.addIntegerSpinner(0, 0, 100, 1, null);
        tab.addButton("Compute (efficient)", (e) -> data.improveMI((int)spinRange.getValue()));
        tab.addButton("Compute (with selectedMI)", (e) -> data.randomimproveMI((int)spinRange.getValue()));
        tab.makeSplit(2, 1);
        tab.addCheckbox("Color by data", false, (a,b) -> data.setDataColored(b));
        tab.addCheckbox("Draw extras", true, (a,b) -> data.setDrawExtras(b));
        tab.addCheckbox("Show labels", false, (a,b) -> data.setDrawLabels(b));
        tab.addCheckbox("Show Map Tears", true, (a,b) -> data.setDrawTears(b));
        tab.addCheckbox("Adjacent Moran's I", true, (a,b) -> data.setAdjacentMI(b));
        tab.addLabel("Stroke size");
        tab.addDoubleSpinner(1,0,Double.MAX_VALUE,1, (a,b) -> data.setStrokeSize(b));
        tab.makeSplit(2,1);
        tab.addLabel("Tear distance");
        tab.addDoubleSpinner(2,0,Double.MAX_VALUE,1, (a,b) -> data.setTearCells(b));
        tab.makeSplit(2,1);
        tab.addLabel("Data <-> Spatial");
        tab.addDoubleSpinner(0,0,1,0.1, (a,b) -> data.setDataSpatial(b));
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
    public String getColoring(){
        for(JRadioButton b : coloring){
            if(b.isSelected())
                return b.getText();
        }
        return null;
    }
}
