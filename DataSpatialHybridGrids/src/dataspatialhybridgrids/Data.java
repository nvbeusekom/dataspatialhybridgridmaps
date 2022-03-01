/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dataspatialhybridgrids;

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
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFrame;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.gui.GUIUtil;
import nl.tue.geometrycore.io.BaseReader;

/**
 *
 * @author wmeulema
 */
public class Data {
    boolean gridlabels = false;
    
    DrawPanel draw = new DrawPanel(this);
    SidePanel side = new SidePanel(this);
    SwapNearby sn = null;

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
    
    public void loadColors() {
        TSVLoader.loadColors(map);
        
        draw.repaint();
    }

    public void createGrid(int cols, int rows) {
        if (map == null) return;
        
        grid = GridGenerator.generateSquareGrid(cols, rows, map.getBoundingBox());
        draw.repaint();
        
    }
    
    public void assignToGrid(){
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
    
    public void computeProminence(Vector loc){
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
    
    public void improveMI(int range){
        if(sn == null){
            sn = new SwapNearby(map,grid);
        }
        double mi = sn.betterSwap(range,draw);
        this.grid = sn.getGrid();
        side.setMoransI(grid.getMoransI());
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
}
