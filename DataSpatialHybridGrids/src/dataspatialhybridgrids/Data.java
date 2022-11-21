/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dataspatialhybridgrids;

import algorithms.ColorGenerator;
import algorithms.DataSortAssignment;
import algorithms.DataSpatialAssignment;
import algorithms.GridGenerator;
import algorithms.SpatialAssignment;
import algorithms.SwapNearby;
import data.GeographicMap;
import data.Grid;
import data.Region;
import data.Tile;
import io.IPEImport;
import io.TSVLoader;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.gui.GUIUtil;
import nl.tue.geometrycore.io.BaseReader;
import nl.tue.geometrycore.io.ipe.IPEWriter;

/**
 *
 * @author wmeulema
 */
public class Data {
    boolean gridlabels = false;
    
    boolean adjacentMI = true;
    
    DrawPanel draw = new DrawPanel(this);
    SidePanel side = new SidePanel(this);
    SwapNearby sn = null;
    double textsize = 10;
    JFileChooser choose = new JFileChooser("../figures");
    
    double tearCells = 2;
    double dataFactor = 0;
    
    int selectedx = -1;
    int selectedy = -1;
    
    GeographicMap map = null;
    Grid grid = null;
    static JFrame mainFrame;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Data data = new Data();
        mainFrame = GUIUtil.makeMainFrame("Data spatial hybrid grid maps", data.draw, data.side);

    }

    public void loadMap() {
        GeographicMap map = IPEImport.readIPE();
        if (map != null) {
            this.map = map;
            this.grid = null;
            new ColorGenerator(map, side.getColoring());
            System.out.println("Nr of Regions: " + map.size());
            draw.zoomToFit();
        }
    }
    
    public void loadData() {
        Random rand = new Random();
        for (Region r : map){
            double val = rand.nextDouble();
            r.setData(val);
        }
        
        TSVLoader.loadTSV(map, mainFrame);
        
        draw.repaint();
    }
    
    public void loadColors(String s) {
        new ColorGenerator(map, s);
        
        draw.repaint();
    }
    
    void saveIPE() {
        int r = choose.showSaveDialog(draw);
        if (r == JFileChooser.APPROVE_OPTION) {
            try (IPEWriter write = IPEWriter.fileWriter(choose.getSelectedFile())) {
                writeIPE(write);
            } catch (IOException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void writeIPE(IPEWriter write) throws IOException {
        Rectangle bbox = draw.getBoundingRectangle();
        bbox.grow(10);
        write.setView(bbox);
        write.initialize("\\renewcommand\\familydefault{\\sfdefault}");

        write.setTextSerifs(true);
        write.configureTextHandling(false, textsize, true);
        draw.render(write);
    }

    public void createGrid(int cols, int rows) {
        if (map == null) return;
        
        grid = GridGenerator.generateSquareGrid(cols, rows, map.getBoundingBox(),adjacentMI);
        draw.repaint();
        
    }
    
    public void assignToGridSpatial(){
        SpatialAssignment sa = new SpatialAssignment(grid,map);
        sa.solveLP();
        gridlabels = true;
        if(sn != null){
            sn.setInitGrid(grid);
        }
        side.setMoransI(grid.getMoransI());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
        
    }
    
    public void assignToGridData(){
        DataSortAssignment sa = new DataSortAssignment(grid,map);
        sa.assign();
        gridlabels = true;
        if(sn != null){
            sn.setInitGrid(grid);
        }
        side.setMoransI(grid.getMoransI());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
        
    }
    
    public void computeProminence(Vector loc){
        if(grid != null){
            for(Tile t : grid){
                if(t.getShape().contains(loc)){
                    if(t.getAssigned() == null){
                        side.setProminence(Double.NaN);
                        return;
                    }
                    Tile closest = null;
                    for(Tile t2 : grid) {
                        if(t2.getAssigned() == null)
                            continue;
                        if((closest == null && t2.getData() > t.getData()) || (!t.equals(t2) && t2.getData()>t.getData() && t.getCenter().distanceTo(t2.getCenter()) < t.getCenter().distanceTo(closest.getCenter())))
                            closest = t2;
                    }
                    if(closest == null){
                        side.setProminence(Double.POSITIVE_INFINITY);
                        return;
                    }
                    side.setProminence(t.getCenter().distanceTo(closest.getCenter()) / t.getLength());
                    return;
                }
            }
        }
    }
    
    public void selectTile(Vector loc){
        if(grid != null){
            for (int i = 0; i < grid.getColumns(); i++) {
                for (int j = 0; j < grid.getRows(); j++) {
                    Tile t = grid.get(i, j);
                    if(t.getShape().contains(loc)){
                        if(t.getAssigned() == null){
                            selectedx = -1;
                            selectedy = -1;
                            return;
                        }
                        if(selectedx == -1 && selectedy == -1){
                            selectedx = i;
                            selectedy = j;
                            return;
                        }
                        if(sn == null){
                            sn = new SwapNearby(map,grid);
                        }
                        System.out.println(sn.getSwapGain(i, j, selectedx, selectedy, grid.getTotal(), grid.getCount(), dataFactor));
                        return;
                    }
                }
            }
        }
    }
    
    public void improveMI(int range){
        if(sn == null){
            sn = new SwapNearby(map,grid);
        }
        sn.betterSwap(range,dataFactor);
        selectedx = -1;
        selectedy = -1;
        this.grid = sn.getGrid();
        side.setMoransI(grid.getMoransI());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
    }
    public void randomimproveMI(int range){
        if(sn == null){
            sn = new SwapNearby(map,grid);
        }
        sn.randomSwap(range,draw);
        selectedx = -1;
        selectedy = -1;
        this.grid = sn.getGrid();
        side.setMoransI(grid.getMoransI());
        System.out.println(grid.getMoransI());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
    }
    public void setDataColored(boolean b){
        draw.dataColored = b;
        draw.repaint();
    }
    public void setDrawExtras(boolean b){
        draw.drawExtras = b;
        draw.repaint();
    }
    public void setDrawLabels(boolean b){
        draw.drawLabels = b;
        draw.repaint();
    }
    public void setStrokeSize(double d){
        draw.strokeSize = d;
        draw.repaint();
    }

    public void setDrawTears(Boolean b) {
        draw.drawTears = b;
        draw.repaint();
    }
    
    public void setTearCells(double d){
        this.tearCells = d;
        draw.repaint();
    }
    
    public double getTearDist(){
        if(grid == null){
            return 0;
        }
        return grid.get(0, 0).getLength() * this.tearCells;
    }
    
    public void setDataSpatial(double d){
        this.dataFactor = d;
    }
    
    public void setAdjacentMI(boolean b){
        this.adjacentMI = b;
        this.grid.setAdjacentMI(b);
        this.side.setMoransI(this.grid.getMoransI());
    }

}
