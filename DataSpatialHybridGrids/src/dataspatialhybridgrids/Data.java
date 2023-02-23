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
import io.CSVLoader;
import io.GeoJSONReader;
import io.IPEImport;
import io.TSVLoader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;
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
    Grid savedGrid = null;
    GeographicMap map = null;
    GeographicMap lowerMap = null;
    Grid grid = null;
    Grid innerGrid = null;
    static JFrame mainFrame;
    
    double spatialSlack = 0.01;

    public void setSpatialSlack(double spatialSlack) {
        this.spatialSlack = spatialSlack;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Data data = new Data();
        mainFrame = GUIUtil.makeMainFrame("Data spatial hybrid grid maps", data.draw, data.side);

    }

    public void loadIPEMap(boolean france) {
        GeographicMap map = IPEImport.readIPE(france,false);
        if (map != null) {
            this.map = map;
            this.grid = null;
            new ColorGenerator(map, side.getColoring());
            System.out.println("Nr of Regions: " + map.size());
            draw.zoomToFit();
        }
    }
    
    public void loadGeoJSONMap() {
        GeographicMap map = GeoJSONReader.loadGeoJSON(null,true);
        if (map != null) {
            this.map = map;
            this.grid = null;
            new ColorGenerator(map, side.getColoring());
            System.out.println("Nr of Regions: " + map.size());
            draw.zoomToFit();
        }
    }
    
    public void loadHierarchicalGeoJSON() {
        GeographicMap map = GeoJSONReader.loadGeoJSON(); 
        if (map != null) {
            GeographicMap detailedMap = GeoJSONReader.loadGeoJSON(map);
            this.map = map;
            this.grid = null;
            this.lowerMap = detailedMap;
            new ColorGenerator(map, side.getColoring());
            this.loadCSV(false);
            this.loadCSV(true);
            System.out.println("Nr of Regions: " + detailedMap.size());
            draw.zoomToFit();
        }
    }
    
    public void loadHierarchicalIPE() {
        GeographicMap map = IPEImport.readIPE(false,true); 
        if (map != null) {
            GeographicMap detailedMap = IPEImport.readIPE(map,false,true);
            this.map = map;
            this.grid = null;
            this.lowerMap = detailedMap;
//            scaleToFranceHeight();
            new ColorGenerator(map, side.getColoring());
            this.loadTSV(true,false);
            this.inferHighLevelData();
            System.out.println("--- Data value ---: " + map.get(5).getData());
            System.out.println("Nr of Regions: " + detailedMap.size());
            draw.zoomToFit();
            System.out.println("BBox: " + draw.getBoundingRectangle().height());
        }
    }
    public void inferHighLevelData(){
        for(Region r : map){
            double d = 0;
            for(Region r2 : r.getLocalMap()){
                d += r2.getData();
            }
            r.setData(d);
        }
    }
    
    public void loadTSV(boolean loadLowerLevel,boolean loadFrance) {
        Random rand = new Random();
        for (Region r : map){
            double val = rand.nextDouble();
            r.setData(val);
        }
        
        TSVLoader.loadTSV(map, mainFrame, loadLowerLevel, loadFrance);
        
        draw.repaint();
    }
    
    public void loadCSV(boolean loadLowerLevel) {
        
        CSVLoader.loadCSV(map, mainFrame, loadLowerLevel);
        
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
        write.setView(new Rectangle(0,398,0,IPEWriter.getA4Size().getTop()));
        write.setWorldview(bbox);
        write.initialize("\\renewcommand\\familydefault{\\sfdefault}");
        
        write.newPage("grid", "data", "boundaries");
        write.setTextSerifs(true);
        write.configureTextHandling(false, textsize, true);
        draw.render(write);
    }

    public void createGrid(int cols, int rows) {
        if (map == null) return;
        
        grid = GridGenerator.generateSquareGrid(cols, rows, map.getBoundingBox(),adjacentMI);
        draw.repaint();
        
    }
    
    public void createInnerGrid(int cols, int rows) {
        if (map == null) return;
        
        innerGrid = GridGenerator.generateSquareGrid(cols, rows, map.getBoundingBox(),adjacentMI);
        draw.repaint();
        
    }
    
    public void assignToGridSpatial(){
        SpatialAssignment sa = new SpatialAssignment(grid,map);
        sa.solveLP();
        gridlabels = true;
        if(sn != null){
            sn.setInitGrid(grid);
        }
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                if(grid.get(i, j).getAssigned() != null){
                    grid.get(i, j).getAssigned().setSpatialCol(i);
                    grid.get(i, j).getAssigned().setSpatialRow(j);
                }
                
            }
            
        }
        side.setMoransI(grid.getMoransI());
        side.setGearysC(grid.getGearysC());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
        
    }
    
    public void assignToGridData(){
        assignToGridData(this.grid);
    }
    
    public void assignToGridData(Grid grid){
        DataSortAssignment sa = new DataSortAssignment(grid,map);
        sa.assign();
        gridlabels = true;
        if(sn != null){
            sn.setInitGrid(grid);
        }
        side.setMoransI(grid.getMoransI());
        side.setGearysC(grid.getGearysC());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
        
    }
    
    public void randomSimAn(){
        assignToGridSpatial();
        double bestMI = -1;
        Grid bestGrid = null;
        for (int i = 10000000; i <= 10000000; i+=10000000) {
            for (double j = 0.01; j <= 0.01; j+=0.05) {
                for (double k = 0.0001; k <= 0.0001; k+=0.0005) {
                    for (int l = 0; l < 1; l++) {
                        Collections.shuffle(map);
                        int index = 0;
                        for(Tile t: grid){
                            if(t.getAssigned() != null){
                                t.setAssigned(map.get(index));
                                index++;
                            }
                        }
                        int maxIterations = i;
                        double startT = j / Math.log(0.5);
                        double endT = k / Math.log(0.000000001);
                        sn = new SwapNearby(map,grid,spatialSlack);
                        System.out.println("Running j = " + j + ", k = " + k + ", i = " + i);
                        sn.simAn(startT, endT, maxIterations);
                        System.out.println("MI: " + sn.getGrid().getMoransI());
                        if(sn.getGrid().getMoransI() > bestMI){
                            bestMI = sn.getGrid().getMoransI();
                            bestGrid = sn.getGrid().clone();
                        }   
                    }
                }
                
            }
        }
        
        selectedx = -1;
        selectedy = -1;
        this.grid = bestGrid;
        side.setMoransI(grid.getMoransI());
        side.setGearysC(grid.getGearysC());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
        
    }
    
    public void computeProminence(Vector loc){
        if(grid != null){
            for (int i = 0; i < grid.getColumns(); i++) {
                for (int j = 0; j < grid.getRows(); j++) {
                Tile t = grid.get(i, j);
                if(t.getShape().contains(loc)){
                    if(t.getAssigned() == null){
                        side.setProminence(Double.NaN);
                        return;
                    }
                    
                    // For debug
                    if(sn!= null){
                        side.setProminence(sn.getMIatLoc(i, j, grid.getTotal(), grid.getCount()));
                        return;
                    }
                    // End debug
                    
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
                            sn = new SwapNearby(map,grid,spatialSlack);
                        }
                        System.out.println(sn.getSwapGain(i, j, selectedx, selectedy, grid.getTotal(), grid.getCount()));
                        System.out.println(grid.adjacentSquaredDistances(i, j));
                        return;
                    }
                }
            }
        }
    }
    
    public void improveMI(int range){
        this.grid = improveMI(range, grid, map);
        side.setMoransI(grid.getMoransI());
        side.setGearysC(grid.getGearysC());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
    }
    
    public Grid improveMI(int range, Grid grid, GeographicMap map){
        sn = new SwapNearby(map,grid,spatialSlack);
//        sn.betterSwap(range,dataFactor);
        int maxIterations = 3000000;
        double startT = 0.01 / Math.log(0.5);
        double endT = 0.0001 / Math.log(0.000000001);
        sn.setRange(range);
        sn.simAn(startT, endT, maxIterations);
        selectedx = -1;
        selectedy = -1;
//        this.grid = sn.getGrid();
        ;
        return sn.getGrid();
    }
    
    public void improveSpatial(int range){
        this.grid = improveSpatial(range,grid,map);
        side.setMoransI(grid.getMoransI());
        side.setGearysC(grid.getGearysC());
        System.out.println("S:");
        System.out.println(grid.getSpatialDistortion());
        side.setSpatialDistortion(grid.getSpatialDistortion());
        draw.repaint();
    }
    
    public Grid improveSpatial(int range, Grid grid, GeographicMap map){
//        if(sn == null){
//            System.out.println("No SwapNearby instance!");
//            sn = new SwapNearby(map,grid,spatialSlack);
//        }
        sn = new SwapNearby(map,grid,spatialSlack);
        sn.setToSpatialGain();
        int maxIterations = 3000000;
        double startT = 1 / Math.log(0.5);
        double endT = 0.01 / Math.log(0.000000001);
        sn.simAn(startT, endT, maxIterations);
        selectedx = -1;
        selectedy = -1;
        return sn.getGrid();
        
    }
    
    public void randomimproveMI(int range){
        if(sn == null){
            sn = new SwapNearby(map,grid,spatialSlack);
        }
        sn.randomSwap(range,draw);
        selectedx = -1;
        selectedy = -1;
        this.grid = sn.getGrid();
        side.setMoransI(grid.getMoransI());
        side.setGearysC(grid.getGearysC());
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
    
    public void saveGrid(){
        this.savedGrid = this.grid.clone();
    }
    public void loadGrid(){
        this.grid = this.savedGrid;
        System.out.println("Sigma: " + spatialSlack);
        sn = new SwapNearby(map,grid,spatialSlack);
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
    
    // High level Spatial
    // Low level Spatial
    public void hierarch_SpatialSpatial(){
        assignToGridSpatial();
        lowLevelSpatial();
    }
    
    // High level Spatial
    // Low level Data
    public void hierarch_SpatialData(int lowRange){
        assignToGridSpatial();
        lowLevelData(lowRange);
    }
    
    // High level Data
    // Low level Spatial
    public void hierarch_DataSpatial(int highRange){
        this.spatialSlack = 100;
        assignToGridData();
        this.sn = null;
        improveMI(highRange);
        lowLevelSpatial();
    }
    
    // High level Data
    // Low level Data
    public void hierarch_DataData(int highRange, int lowRange){
        this.spatialSlack = 100;
        assignToGridData();
        this.sn = null;
        improveMI(highRange);
        lowLevelData(lowRange);
    }

    
    public void lowLevelSpatial(){
        this.spatialSlack = 100;
        GeographicMap alteredMap = new GeographicMap();
        HashMap<String,ArrayList> allPreservedCenters = new HashMap<>();
        for (Tile t : grid) {
            if(t.getAssigned() != null){
                Region r = t.getAssigned();
                GeographicMap localMap = r.getLocalMap();
                // Translate each local region to be near the center of r
                Rectangle localBox = localMap.getBoundingBox();
                // Compute relative vector from the center of the boundingBox, use that on the center of r.
                ArrayList<Vector> preservedCenters = new ArrayList();
                for (Region innerRegion : localMap) {
                    preservedCenters.add(innerRegion.getPos().clone());
                    Vector translation = Vector.divide(Vector.subtract(innerRegion.getPos(),localBox.center()),Math.max(localBox.height()/t.getShape().height(),localBox.width()/t.getShape().width())*1 );//innerGrid.getColumns()+innerGrid.getRows());
//                    Vector translation = Vector.divide(Vector.subtract(innerRegion.getPos(),localBox.center()),innerGrid.getColumns()+innerGrid.getRows());
//                    Vector newPos = Vector.add(r.getPos(), translation);
                    Vector newPos = Vector.add(t.getCenter(), translation);
//                    Vector newPos = Vector.add(closerToCenter, translation);
                    innerRegion.setPos(newPos);
                    alteredMap.add(innerRegion);
                }
                allPreservedCenters.put(r.getLabel(), preservedCenters);
            }
        }
        innerGrid.clearGrid();
        SpatialAssignment sa2 = new SpatialAssignment(innerGrid,alteredMap);
        sa2.solveLP();
        for(Region r : map){
            int index = 0;
            ArrayList<Vector> preservedCenters = allPreservedCenters.get(r.getLabel());
            for (Region innerRegion : r.getLocalMap()) {
                innerRegion.setPos(preservedCenters.get(index));
                index++;
            }
        }
        draw.repaint();
    }
    
    public void lowLevelData(int range){
        // Divide inner grid into copied grids based on high level
        this.spatialSlack = 100;
        HashMap<String,Grid> smallGrids = new HashMap();
        for(Region r: this.map){
            Grid subGrid = GridGenerator.generateSquareGrid(innerGrid.getColumns(), innerGrid.getRows(), map.getBoundingBox(),adjacentMI);
            // Assign Tiles from innerGrid only with parent r.
            for (int i = 0; i < innerGrid.getColumns(); i++) {
                for (int j = 0; j < innerGrid.getRows(); j++) {
                    if(innerGrid.get(i, j).getAssigned() != null && innerGrid.get(i, j).getAssigned().getParent().getLabel().equals(r.getLabel())){
                        // Add to subGrid
                        subGrid.get(i, j).setAssigned(innerGrid.get(i, j).getAssigned());
                        
                    }
                    
                }
                
            }
            DataSortAssignment sa = new DataSortAssignment(subGrid,r.getLocalMap());
            sa.assign();
            this.sn = null;
            subGrid = improveMI(range,subGrid,r.getLocalMap());
//            subGrid = improveSpatial(range,subGrid,r.getLocalMap());
            smallGrids.put(r.getLabel(), subGrid);
            
        }
        for(Region r : this.map){
            Grid subGrid = smallGrids.get(r.getLabel());
            for (int i = 0; i < subGrid.getColumns(); i++) {
                for (int j = 0; j < subGrid.getRows(); j++) {
                    if(subGrid.get(i, j).getAssigned() != null){
                        this.innerGrid.get(i, j).setAssigned(subGrid.get(i, j).getAssigned());
                    }
                    
                }
                
            }
        }
        draw.repaint();
    }
}
