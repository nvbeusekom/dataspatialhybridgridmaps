/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import data.GeographicMap;
import data.Grid;
import data.Swap;
import data.Tile;
import dataspatialhybridgrids.DrawPanel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.geometrycore.datastructures.priorityqueue.IndexedPriorityQueue;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;

/**
 *
 * @author 20184261
 */
public class SwapNearby {
    GeographicMap map;
    Grid initGrid;
    Grid grid = null;
    int range = Integer.MAX_VALUE;

    public SwapNearby(GeographicMap map, Grid initGrid) {
        this.map = map;
        setInitGrid(initGrid);
    }
    
    public double randomSwap(int range, DrawPanel draw){
        if(range < this.range){
            this.range = range;
            grid = initGrid.clone();
        }
        double oldMI = grid.getMoransI();
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                Tile t = grid.get(i,j);
                if(t.getAssigned() != null){
                    int initCol = t.getAssigned().getInitCol();
                    int initRow = t.getAssigned().getInitRow();
                    // Search the range for swaps
//                    for (int k = 0; k < grid.getColumns(); k++) {
//                        for (int l = 0; l < grid.getRows(); l++) {
                    for (int k = Integer.max(initCol-range,0); k <= Integer.min(initCol+range,grid.getColumns()-1); k++){
                        // Note that L and One use the same symbol...
                        for (int l = Integer.max(initRow-range,0); l <= Integer.min(initRow+range,grid.getColumns()-1); l++){
                            // Check if the swapping candidate is null or also within swapping range
                            Tile t2 = grid.get(k, l);
                            if (t.getAssigned() != null && t2.getAssigned() != null){
//                                if ((t.getAssigned() == null || t.getAssigned().getPos().distanceTo(t2.getCenter()) <= range) &&
//                                        (t2.getAssigned() == null || t2.getAssigned().getPos().distanceTo(t.getCenter()) <= range)) {
                                if (t2.getAssigned() == null || Math.abs(i-t2.getAssigned().getInitCol()) <= range && Math.abs(j-t2.getAssigned().getInitRow())<= range){
                                    // Swap
                                    grid.set(i, j, t2);
                                    grid.set(k, l, t);
                                    double newMI = grid.getMoransI();
                                    if (oldMI < newMI){
//                                        System.out.println("Swapped: " + i + "," + j + "with" + k + "," + l);
                                        //Swap the actual locations
                                        Polygon temp1 = t.getShape().clone();
                                        Vector temp2 = t.getCenter().clone();
                                        t.setShape(t2.getShape());
                                        t.setCenter(t2.getCenter());
                                        t2.setShape(temp1);
                                        t2.setCenter(temp2);

//                                        System.out.println("New Morans I: " + Double.toString(newMI));
                                        draw.repaint();
                                        return randomSwap(range,draw);
                                    }
                                    else{
                                        // Undo swap
                                        grid.set(i, j, t);
                                        grid.set(k, l, t2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        }
        
        return oldMI;
    }
    public double betterSwap(int range, DrawPanel draw){
        if(range < this.range){
            this.range = range;
            grid = initGrid.clone();
        }
        double meank = 0;
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                meank += grid.get(i,j).getData();
            }
            
        }
        int index = 0;
        double oldMI = grid.getMoransI();
        IndexedPriorityQueue<Swap> q = new IndexedPriorityQueue<>(grid.getColumns()*grid.getColumns()*range*range,new SwapComparator());
        // Initialize Queue
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                Tile t = grid.get(i,j);
                if(t.getAssigned() != null){
                    int initCol = t.getAssigned().getInitCol();
                    int initRow = t.getAssigned().getInitRow();
                    // Search the range for swaps
//                    for (int k = 0; k < grid.getColumns(); k++) {
//                        for (int l = 0; l < grid.getRows(); l++) {
                    for (int k = Integer.max(initCol-range,0); k <= Integer.min(initCol+range,grid.getColumns()-1); k++){
                        // Note that L and One use the same symbol...
                        for (int l = Integer.max(initRow-range,0); l <= Integer.min(initRow+range,grid.getRows()-1); l++){
                            // Check if the swapping candidate is null or also within swapping range
                            Tile t2 = grid.get(k, l);
                            if(t2.getAssigned() == null)
                                continue;
                            // Make sure every swap is added once
                            if (k >= i && l >= j){
//                                if ((t.getAssigned() == null || t.getAssigned().getPos().distanceTo(t2.getCenter()) <= range) &&
//                                        (t2.getAssigned() == null || t2.getAssigned().getPos().distanceTo(t.getCenter()) <= range)) {
                                if (Math.abs(i-t2.getAssigned().getInitCol()) <= range && Math.abs(j-t2.getAssigned().getInitRow())<= range){
                                    // Compute all MI values and add the swaps to the priority queue
                                    double gain = getSwapGain(i,j,k,l,meank);
                                    // Only add beneficial swaps
                                    if(gain > 0){
                                        Swap swap = new Swap(gain,index,i,j,k,l);
                                        index++;
                                        q.add(swap);
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }
            
        } 
        // Work through Queue
        double prev = -2;
        while(!q.isEmpty()){
            System.out.println("Iteration");
            Swap s = q.poll();
            // Do the swap
            Tile t1 = grid.get(s.getI(), s.getJ());
            Tile t2 = grid.get(s.getK(), s.getL());
            Polygon temp1 = t1.getShape().clone();
            Vector temp2 = t1.getCenter().clone();
            t1.setShape(t2.getShape());
            t1.setCenter(t2.getCenter());
            t2.setShape(temp1);
            t2.setCenter(temp2);
            grid.set(s.getK(), s.getL(), t1);
            grid.set(s.getI(), s.getJ(), t2);
            double newmi = grid.getMoransI();
            if(newmi < prev){
                System.out.println("New: " + newmi + " old: " + prev);
                System.out.println(String.format("I swapped " + s.getI() + "," + s.getJ() + " with " + s.getK() + "," + s.getL()));
                System.out.println(s.getMiGain());
                System.out.println(getSwapGain(s.getI(),s.getJ(),s.getK(),s.getL(),meank));
                grid.set(s.getI(), s.getJ(), t1);
                grid.set(s.getK(), s.getL(), t2);
                System.out.println(getSwapGain(s.getI(),s.getJ(),s.getK(),s.getL(),meank));
                return 0;
            }
            prev = newmi;
            System.out.println("The swap:");
            System.out.println(s.getI());
            System.out.println(s.getJ());
            System.out.println(s.getK());
            System.out.println(s.getL());
            System.out.println(isSwap(s,2,12,3,12));
            // Remove the ones that changed make index based on tile?
            List<Swap> swaplist = q.extractContents();
            for(Swap swap : swaplist){
                if(swap == null)
                    continue;
                if(isSwap(swap,2,11,3,11)){
                    System.out.println("Its in!!!!!!----------");
                }
                if(hasOverlappingTiles(s,swap)){
                    System.out.println("Removing");
                    
                    q.remove(swap);
                    index--;
                }
                else{
                    swap.setMiGain(getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),meank));
                    q.priorityChanged(swap);
                }
//                else if(isAdjacent(s,swap)){
//                    System.out.println(swap.getIndex());
//                    swap.setMiGain(getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),meank));
//                    q.priorityChanged(swap);
//                }
            }
            List<Swap> newlist = q.extractContents();
            for(Swap swap : newlist){
                if(swap == null)
                    continue;
                if(isSwap(swap,2,11,3,11)){
                    System.out.println("Its in!!!!!!----------");
                }
                if(swap.getMiGain() != getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),meank)){
                    System.out.println("Wrong Gain!!!a");
                    System.out.println(swap.getMiGain());
                    System.out.println(getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),meank));
                    System.out.println(swap.getI());
                    System.out.println(swap.getJ());
                    System.out.println(swap.getK());
                    System.out.println(swap.getL());
                    return 0;
                }
            }
            // Add new possibilies
            for (int i = -range*2; i <= range*2; i++) {
                for (int j = -range*2; j <= range*2; j++) {
                    if(!(i == 0 && j == 0)){
                        // Do T1
                        int cx = s.getK() + i;
                        int cy = s.getL() + j;
                        // Check if in bounds
                        if(cx < 0 || cy  < 0 || cx >= grid.getColumns() || cy >= grid.getRows()){
                            continue;
                        }
                        Tile candidate = grid.get(cx, cy);
                        if(candidate.getAssigned() == null)
                            continue;
                        if((Math.abs(cx-t1.getAssigned().getInitCol()) <= range && Math.abs(cy-t1.getAssigned().getInitRow())<= range) &&
                                (Math.abs(s.getK()-candidate.getAssigned().getInitCol()) <= range && Math.abs(s.getL()-candidate.getAssigned().getInitRow())<= range)){
                            double gain = getSwapGain(s.getK(),s.getL(),cx,cy,meank);
                            if(gain > 0){
                                q.add(new Swap(gain,index,s.getK(),s.getL(),cx,cy));
                                index++;
                            }
                        }
                        // Do T2

                        cx = s.getI() + i;
                        cy = s.getJ() + j;
                        if(cx < 0 || cy  < 0 || cx >= grid.getColumns() || cy >= grid.getRows()){
                            continue;
                        }
                        candidate = grid.get(cx, cy);
                        if(candidate.getAssigned() == null)
                            continue;
                        if((t2.getAssigned() == null || Math.abs(cx-t2.getAssigned().getInitCol()) <= range && Math.abs(cy-t2.getAssigned().getInitRow())<= range) &&
                                (candidate.getAssigned() == null || Math.abs(s.getI()-candidate.getAssigned().getInitCol()) <= range && Math.abs(s.getJ()-candidate.getAssigned().getInitRow())<= range)){
                            double gain = getSwapGain(s.getI(),s.getJ(),cx,cy,meank);
                            if(gain > 0){
                                q.add(new Swap(gain,index,s.getI(),s.getJ(),cx,cy));
                                index++;
                            }
                        }
                    }
                }
            }
            for(Swap swap : swaplist){
                if(swap == null)
                    continue;
                if(swap.getMiGain() != getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),meank)){
                    System.out.println("Wrong Gain!!!!");
                    return 0;
                }
            }
            System.out.println(q.size());
        }
        
        return oldMI;
    }
    public boolean isSwap(Swap s, int i, int j , int k, int l){
        return (s.getI() == i && s.getJ() == j && s.getK() == k && s.getL() == l);
    }
    public double getSwapGain(int i, int j, int k, int l, double meank){
        Tile t = grid.get(i, j);
        Tile t2 = grid.get(k, l);
        double currentMI = getMIatLoc(i,j,meank) + getMIatLoc(k,l,meank);
        grid.set(i, j, t2);
        grid.set(k, l, t);
        double newMI = getMIatLoc(i,j,meank) + getMIatLoc(k,l,meank);
        grid.set(i, j, t);
        grid.set(k, l, t2);
        return newMI - currentMI;
        
    }
    
    public boolean isAdjacent(Swap changed, Swap checked){
        // Do it based on indices
        return (changed.getI() == checked.getI() && Math.abs(changed.getJ() - checked.getJ()) == 1) ||
        (changed.getJ() == checked.getJ() && Math.abs(changed.getI() - checked.getI()) == 1) ||
        (changed.getK() == checked.getK() && Math.abs(changed.getL() - checked.getL()) == 1) ||
        (changed.getL() == checked.getL() && Math.abs(changed.getK() - checked.getK()) == 1);
    }
    
    public boolean hasOverlappingTiles(Swap a, Swap b){
        // Do it based on indices
        return (a.getI() == b.getI() && a.getJ() == b.getJ()) ||
        (a.getK() == b.getK() && a.getL() == b.getL()) ||
        (a.getI() == b.getK() && a.getJ() == b.getL()) ||
        (a.getK() == b.getI() && a.getL() == b.getJ());
    }
    
    public double getMIatLoc(int x, int y, double meank){
        Tile t = grid.get(x, y);
        double res = 0;
        if(x > 0)
            res += (t.getData() - meank) * (grid.get(x-1, y).getData() - meank);
        if(y > 0)
            res += (t.getData() - meank) * (grid.get(x, y-1).getData() - meank);
        if(x < grid.getColumns() - 1)
            res += (t.getData() - meank) * (grid.get(x+1, y).getData() - meank);
        if(y < grid.getRows() - 1)
            res += (t.getData() - meank) * (grid.get(x, y+1).getData() - meank);
        return res;
    }

    public Grid getInitGrid() {
        return initGrid;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setInitGrid(Grid initGrid) {
        System.out.println("Resetting init Grid");
        this.initGrid = initGrid;
        for (int i = 0; i < initGrid.getColumns(); i++) {
            for (int j = 0; j < initGrid.getRows(); j++) {
                if(initGrid.get(i, j).getAssigned() != null){
                    initGrid.get(i, j).getAssigned().setInitCol(i);
                    initGrid.get(i, j).getAssigned().setInitRow(j);
                }
            }
        }
    }
    
    
    class SwapComparator implements Comparator<Swap>{
        @Override
        public int compare(Swap s1, Swap s2) {
            return Double.compare(s1.getMiGain(), s2.getMiGain());
        }
    }
    
    
}
