/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
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
    JLabel labeli;
    JLabel labelc;
    JLabel label2;
    JLabel label3;
    JLabel labeli2;
    JLabel labelc2;
    JLabel label22;
    JLabel label32;
    JRadioButton[] coloring;
    JRadioButton[] higherLevel;
    boolean higherLevelSpatial = true;

    SidePanel(Data data) {
        this.data = data;

        makeIOTab();
        makeHierarchTab();
    }

    private void makeIOTab() {
        SideTab tab = addTab("Regular");
        tab.addButton("Open France map and data", (e) -> {data.loadIPEMap(true);data.loadTSV(false,true);});
        tab.addButton("Open NL map and data", (e) -> {data.loadIPEMap(false);data.loadTSV(false,false);});
        tab.addButton("Open UK map and data", (e) -> {data.loadGeoJSONMap();data.loadCSV(false);});
        String[] options = {"NL","UK","US"};
        coloring = tab.addRadioButtonList(options, "NL", (a,s) -> data.loadColors(s));
        

        tab.makeSplit(2, 2);
        JSpinner spinColsOuter = tab.addIntegerSpinner(18, 1, Integer.MAX_VALUE, 1, null);
        JSpinner spinRowsOuter = tab.addIntegerSpinner(19, 1, Integer.MAX_VALUE, 1, null);

        tab.addButton("Create grid", (e) -> data.createGrid((int) spinColsOuter.getValue(), (int) spinRowsOuter.getValue()));
        
        tab.addButton("Assign to grid (spatial)", (e) -> data.assignToGridSpatial());
        tab.addButton("Assign to grid (data-sorted)", (e) -> data.assignToGridData());
        tab.addButton("Random Simulated Annealing", (e) -> data.randomSimAn());
        
        tab.addButton("Save to Ipe", (e) -> data.saveIPE());
        
        tab.makeSplit(2,2);
        tab.addLabel("Range");
//        JSlider rangeSlider = tab.addIntegerSlider(0, 0, 20,null);
        JSpinner spinRange = tab.addIntegerSpinner(0, 0, 100, 1, null);
        tab.addButton("Compute (efficient)", (e) -> data.improveMI((int)spinRange.getValue()));
        tab.addButton("Post process spatial", (e) -> data.improveSpatial((int)spinRange.getValue()));
        tab.addButton("Compute (with selectedMI) - StackOverflows..", (e) -> data.randomimproveMI((int)spinRange.getValue()));
        tab.makeSplit(2, 1);
        tab.addButton("Save", (e) -> data.saveGrid());
        tab.addButton("Load", (e) -> data.loadGrid());
        tab.makeSplit(2, 1);
        tab.addCheckbox("Color by data", false, (a,b) -> data.setDataColored(b));
        tab.addCheckbox("Draw extras", true, (a,b) -> data.setDrawExtras(b));
        tab.addCheckbox("Show labels", false, (a,b) -> data.setDrawLabels(b));
        tab.addCheckbox("Show Map Tears", false, (a,b) -> data.setDrawTears(b));
        tab.addCheckbox("Adjacent Moran's I", true, (a,b) -> data.setAdjacentMI(b));
        tab.addLabel("Stroke size");
        tab.addDoubleSpinner(0.4,0,Double.MAX_VALUE,0.1, (a,b) -> data.setStrokeSize(b));
        tab.makeSplit(2,1);
        tab.addLabel("Tear distance");
        tab.addDoubleSpinner(2,0,Double.MAX_VALUE,1, (a,b) -> data.setTearCells(b));
        tab.makeSplit(2,1);
        tab.addLabel("Spatial slack");
        tab.addDoubleSpinner(0.01,0,40,0.01, (a,b) -> data.setSpatialSlack(b));
        tab.makeSplit(2,1);
        labeli = tab.addLabel("Morans I: 0");
        labeli.setSize(this.getSize().width,labeli.getSize().height);
        labelc = tab.addLabel("Morans I: 0");
        labelc.setSize(this.getSize().width,labelc.getSize().height);
        tab.makeSplit(2,1);
        label2 = tab.addLabel("Spatial distortion: 0");
        label2.setSize(this.getSize().width,label2.getSize().height);
        tab.makeSplit(2,1);
        label3 = tab.addLabel("Prominence: 0");
        label3.setSize(this.getSize().width,label3.getSize().height);
        
    }
    
    private void makeHierarchTab() {
        SideTab tab = addTab("Hierarchical");
        tab.addButton("Do all UK", (e) -> {data.loadHierarchicalGeoJSON(); data.createGrid(3,4);data.createInnerGrid(18,19);data.hierarch_SpatialSpatial();data.loadColors("UK");});
        tab.addButton("Open UK", (e) -> data.loadHierarchicalGeoJSON());
        
        tab.addButton("Do all NL", (e) -> {data.loadHierarchicalIPE();data.createGrid(3,4);data.createInnerGrid(17,23);data.hierarch_SpatialSpatial();data.loadColors("UK");});
        tab.addButton("Open NL", (e) -> data.loadHierarchicalIPE());
        
        tab.makeSplit(2, 2);
        
        String[] options = {"NL","UK","US"};
        coloring = tab.addRadioButtonList(options, "NL", (a,s) -> data.loadColors(s));
        

        tab.makeSplit(2, 2);
        JSpinner spinColsOuter = tab.addIntegerSpinner(3, 1, Integer.MAX_VALUE, 1, null);
        JSpinner spinRowsOuter = tab.addIntegerSpinner(4, 1, Integer.MAX_VALUE, 1, null);

        tab.addButton("Create outer grid", (e) -> data.createGrid((int) spinColsOuter.getValue(), (int) spinRowsOuter.getValue()));
        
        JSpinner spinColsInner = tab.addIntegerSpinner(19, 1, Integer.MAX_VALUE, 1, null);
        JSpinner spinRowsInner = tab.addIntegerSpinner(19, 1, Integer.MAX_VALUE, 1, null);

        
        tab.addButton("Create inner grid", (e) -> data.createInnerGrid((int) spinColsInner.getValue(), (int) spinRowsInner.getValue()));
        
        tab.makeSplit(2, 2);
        
        tab.addLabel("Higher level:");
        String[] higherLvlOptions = {"Spatial","Data"};
        higherLevel = tab.addRadioButtonList(higherLvlOptions, "Spatial", (a,s) -> higherLevelSpatial = s.equals("Spatial"));
       
        tab.makeSplit(2,2);
        tab.addLabel("Higher Range");
//        JSlider rangeSlider = tab.addIntegerSlider(0, 0, 20,null);
        JSpinner spinRange = tab.addIntegerSpinner(10, 0, 100, 1, null);
        tab.addLabel("Lower Range");
//        JSlider rangeSlider = tab.addIntegerSlider(0, 0, 20,null);
        JSpinner lowSpinRange = tab.addIntegerSpinner(10, 0, 100, 1, null);
        tab.addButton("Compute (efficient)", (e) -> data.improveMI((int)spinRange.getValue()));
        
        tab.makeSplit(2, 2);
        tab.addLabel("Lower level:");
        tab.addButton("Assign to grid (spatial)", (e) -> {if(higherLevelSpatial){ 
                                                            data.hierarch_SpatialSpatial();
                                                         } else {
                                                            data.hierarch_DataSpatial((int)spinRange.getValue());}});
        tab.addButton("Assign to grid (data-sorted)", (e) -> {if(higherLevelSpatial){ 
                                                            data.hierarch_SpatialData((int)lowSpinRange.getValue());
                                                         } else {
                                                            data.hierarch_DataData((int)spinRange.getValue(),(int)lowSpinRange.getValue());}});
        
        tab.addButton("Save to Ipe", (e) -> data.saveIPE());
        
        
        tab.makeSplit(2, 1);
        labeli2 = tab.addLabel("Morans I: 0");
        labeli2.setSize(this.getSize().width,labeli.getSize().height);
        labelc2 = tab.addLabel("Gearys C: 0");
        labelc2.setSize(this.getSize().width,labelc.getSize().height);
        tab.makeSplit(2,1);
        label22 = tab.addLabel("Spatial distortion: 0");
        label22.setSize(this.getSize().width,label2.getSize().height);
        tab.makeSplit(2,1);
        label32 = tab.addLabel("Prominence: 0");
        label32.setSize(this.getSize().width,label3.getSize().height);
        
    }
    
    public void setSpatialDistortion(double d){
        label2.setText("Spatial distortion: " + Double.toString(d));
        label22.setText("Spatial distortion: " + Double.toString(d));
    }
    public void setMoransI(double d){
        labeli.setText("Morans I: " + Double.toString(d));
        labeli2.setText("Morans I: " + Double.toString(d));
    }
    public void setGearysC(double d){
        labelc.setText("Geary's C: " + Double.toString(d));
        labelc2.setText("Geary's C: " + Double.toString(d));
    }
    public void setProminence(double d){
        label3.setText("Prominence: " + Double.toString(d));
        label32.setText("Prominence: " + Double.toString(d));
    }
    public String getColoring(){
        for(JRadioButton b : coloring){
            if(b.isSelected())
                return b.getText();
        }
        return null;
    }
}
