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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.geometrycore.datastructures.priorityqueue.IndexedPriorityQueue;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author 20184261
 */
public class SwapNearby {
    GeographicMap map;
    Grid initGrid;
    Grid grid = null;
    int range = Integer.MAX_VALUE;
    boolean improvingSpace = false;
    double miUsed = 0;
    double miMaxUse = 0.01; // 0.01, 0.05, 0.1 0.15, 0.20 0.25 0.5
    double miSacrificePerUse = miMaxUse;
    

    public SwapNearby(GeographicMap map, Grid initGrid) {
        this.map = map;
        setInitGrid(initGrid);
    }
    
    public void setToSpatialGain(){
        improvingSpace = true;
        miUsed = 0;
    }
    
    public double randomSwap(int range, DrawPanel draw){
        if(range < this.range || true){
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
//                                    double gain = newMI - oldMI;
//                                    double dists = () - ()
//                                    boolean b = gain > 0 && gain >  
                                    if (oldMI < newMI){
//                                        System.out.println("Swapped: " + i + "," + j + "with" + k + "," + l);
                                        //Swap the actual locations
                                        Rectangle temp1 = t.getShape().clone();
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
    
    public void betterSwap(int range, double dataSpatial){
        if(range < this.range){
            this.range = range;
            grid = initGrid.clone();
        }
        if(this.improvingSpace){
            range = Math.max(grid.getColumns(), grid.getRows());
        }
        int maxDimension = Math.max(grid.getColumns(), grid.getRows());
        range = Math.min(range, maxDimension);
        this.range = range;
        double total = 0;
        int count = 0;
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                if(grid.get(i,j).getAssigned() != null){
                    total += grid.get(i,j).getData();
                    count++;
                }
            }
            
        }
        HashSet<Integer> inQueue = new HashSet<>();
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
                                    double gain = getSwapGain(i,j,k,l,total,count,dataSpatial);
                                    // Only add beneficial swaps
                                    if(gain > 0){
                                        Swap swap = new Swap(gain,i,j,k,l);
                                        q.add(swap);
                                        inQueue.add(swap.computeHash(maxDimension));
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
        
        int swaps = 0;
        // Work through Queue
        while(!q.isEmpty()){
            Swap s = q.poll();
            // Do the swap
            swaps++;
            if(swaps%100 == 0) System.out.println("Swap " + swaps + ", " +q.size() + " in queue");
            double oldmi = 0;
            double newmi = 0;
            if(improvingSpace){
                oldmi = grid.getMoransI();
            }
            Tile t1 = grid.get(s.getI(), s.getJ());
            Tile t2 = grid.get(s.getK(), s.getL());
            Rectangle temp1 = t1.getShape().clone();
            Vector temp2 = t1.getCenter().clone();
            t1.setShape(t2.getShape());
            t1.setCenter(t2.getCenter());
            t2.setShape(temp1);
            t2.setCenter(temp2);
            grid.set(s.getK(), s.getL(), t1);
            grid.set(s.getI(), s.getJ(), t2);
            if(improvingSpace){
                newmi = grid.getMoransI();
                miUsed += (oldmi-newmi);
                if(miUsed > miMaxUse){ //undo swap and stop
                    grid.set(s.getI(), s.getJ(), t1);
                    grid.set(s.getK(), s.getL(), t2);
                    break;
                }
            }
//            if(newmi < oldmi && false){ //debugging
//                System.out.println("Oh oh.... bad swap :(");
//                System.out.println("I went from " + oldmi + " to " + newmi);
//                System.out.println("Even though I thought it would be ");
//                double valuee = getMIatLoc(s.getI(), s.getJ(), total, count) + getMIatLoc(s.getK(), s.getL(), total, count);
//                System.out.println(s.getMiGain());
//                grid.set(s.getK(), s.getL(), t2);
//                grid.set(s.getI(), s.getJ(), t1);
//                double valueeOLD = getMIatLoc(s.getI(), s.getJ(), total, count) + getMIatLoc(s.getK(), s.getL(), total, count);
//                System.out.println(s.getMiGain());
//            }
            // Add new ones around T1 and T2
            boolean[][] aroundT1 = {{true,true,true},{true,true,true},{true,true,true}};
            boolean[][] aroundT2 = {{true,true,true},{true,true,true},{true,true,true}};
            List<Swap> swaplist = new ArrayList(q.extractContents());
            for(Swap swap : swaplist){
                if(swap == null)
                    continue;
                if(hasOverlappingTiles(s,swap)){
                    q.remove(swap);
                    inQueue.remove(swap.computeHash(maxDimension));
                }
                else if(isAdjacent(s.getK(),s.getL(),swap) || isAdjacent(s.getI(),s.getJ(),swap)){
                    double gain = getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),total,count,dataSpatial);
                    if(gain > 0) {
                        swap.setMiGain(gain);
                        q.priorityChanged(swap);
                    }
                    else {
                        q.remove(swap);
                        inQueue.remove(swap.computeHash(maxDimension));
                    }
                    
                }
            }
            // Add new possibilies
            for (int a = -1; a <= 1 ; a++) {
                for (int b = -1; b <= 1 ; b++) {
                    for (int i = -range*2; i <= range*2; i++) {
                        for (int j = -range*2; j <= range*2; j++) {
                            if(!(i == 0 && j == 0)){
                                // Do T1
                                int tx = s.getK() + a;
                                int ty = s.getL() + b;
                                if(tx < 0 || ty  < 0 || tx >= grid.getColumns() || ty >= grid.getRows()){
                                    continue;
                                }
                                Tile test1 = grid.get(tx, ty);
                                if(test1.getAssigned() == null)
                                    continue;
                                int cx = tx + i;
                                int cy = ty + j;
                                // Check if in bounds
                                if(cx < 0 || cy  < 0 || cx >= grid.getColumns() || cy >= grid.getRows()){
                                    continue;
                                }
                                Tile candidate = grid.get(cx, cy);
                                if(candidate.getAssigned() == null)
                                    continue;
                                if((Math.abs(cx-test1.getAssigned().getInitCol()) <= range && Math.abs(cy-test1.getAssigned().getInitRow())<= range) &&
                                        (Math.abs(tx-candidate.getAssigned().getInitCol()) <= range && Math.abs(ty-candidate.getAssigned().getInitRow())<= range)){
                                    double gain = getSwapGain(tx,ty,cx,cy,total,count,dataSpatial);
                                    if(gain > 0){
                                        Swap newswap = new Swap(gain,tx,ty,cx,cy);
                                        if(!inQueue.contains(newswap.computeHash(maxDimension))){
                                            q.add(newswap);
                                            inQueue.add(newswap.computeHash(maxDimension));
                                        }
                                    }
                                }
                                // Do T2
                                tx = s.getI() + a;
                                ty = s.getJ() + b;
                                if(tx < 0 || ty  < 0 || tx >= grid.getColumns() || ty >= grid.getRows()){
                                    continue;
                                }
                                Tile test2 = grid.get(tx, ty);
                                if(test2.getAssigned() == null)
                                    continue;
                                cx = tx + i;
                                cy = ty + j;
                                if(cx < 0 || cy  < 0 || cx >= grid.getColumns() || cy >= grid.getRows()){
                                    continue;
                                }
                                candidate = grid.get(cx, cy);
                                if(candidate.getAssigned() == null)
                                    continue;
                                if((test2.getAssigned() == null || Math.abs(cx-test2.getAssigned().getInitCol()) <= range && Math.abs(cy-test2.getAssigned().getInitRow())<= range) &&
                                        (candidate.getAssigned() == null || Math.abs(tx-candidate.getAssigned().getInitCol()) <= range && Math.abs(ty-candidate.getAssigned().getInitRow())<= range)){
                                    double gain = getSwapGain(tx,ty,cx,cy,total,count,dataSpatial);
                                    if(gain > 0){
                                        Swap newswap = new Swap(gain,tx,ty,cx,cy);
                                        if(!inQueue.contains(newswap.computeHash(maxDimension))){
                                            q.add(newswap);
                                            inQueue.add(newswap.computeHash(maxDimension));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public boolean isSwap(Swap s, int i, int j , int k, int l){
        return (s.getI() == i && s.getJ() == j && s.getK() == k && s.getL() == l);
    }
    public double getSwapGain(int i, int j, int k, int l, double total, int count, double dataSpatial){
        Tile t = grid.get(i, j);
        Tile t2 = grid.get(k, l);
        
        double oldMIGRID = 0;
        double newMIGRID = 0;
        
        double oldMI = getMIatLoc(i,j,total,count) + getMIatLoc(k,l,total,count);
        if(improvingSpace){
            oldMIGRID = grid.getMoransI();
        }
        
        ArrayList<Double> oldMISUM = sumLocMI(total,count);
        grid.set(i, j, t2);
        grid.set(k, l, t);
        double newMI = getMIatLoc(i,j,total,count) + getMIatLoc(k,l,total,count);
        if(improvingSpace){
            newMIGRID = grid.getMoransI();
        }
        
        ArrayList<Double> newMISUM = sumLocMI(total,count);
        grid.set(i, j, t);
        grid.set(k, l, t2);
        
        // Be careful to use local MI instead of GC when debugging
        boolean localBetter = oldMI < newMI;
        boolean sumBetter = sumdouble(oldMISUM) < sumdouble(newMISUM);
        boolean globalBetter = oldMIGRID < newMIGRID;
//        if(improvingSpace && oldMIGRID - newMIGRID < miMaxUse - miUsed){
//            return getSpatialFromSwap(i , j, k, l);
//        }
        if(improvingSpace && getSpatialFromSwap(i , j, k, l) > 0 && oldMIGRID - newMIGRID < miMaxUse - miUsed){
            return getSpatialFromSwap(i , j, k, l);
        }
        else if(improvingSpace){
            return -1;
        }
//        if(localBetter!=sumBetter){
//            System.out.println("Swap Gain FAILED");
//            for (int m = 0; m < oldMISUM.size(); m++) {
//                if(oldMISUM.get(m) != newMISUM.get(m)){
//                    System.out.println(oldMISUM.get(m) + " =?= " + newMISUM.get(m));
//                }
//            }
//            System.out.println("But which is wrong...?");
//            grid.set(i, j, t2);
//            grid.set(k, l, t);
//            System.out.println("What now?");
//            
//        }
        return newMI - oldMI;
//        double miGain = (newMI - oldMI) / Math.abs(oldMI);
//        double spGain = getSpatialFromSwap(i,j,k,l);
////        return miGain;
//        return (1-dataSpatial) * miGain - dataSpatial * spGain;
    }
    
    public boolean isAdjacent(int x, int y, Swap checked){
        // Do it based on indices
        return (x == checked.getI() && Math.abs(y - checked.getJ()) == 1) ||
        (y == checked.getJ() && Math.abs(x - checked.getI()) == 1) ||
        (x == checked.getK() && Math.abs(y - checked.getL()) == 1) ||
        (y == checked.getL() && Math.abs(x - checked.getK()) == 1);
    }
    
    public boolean hasOverlappingTiles(Swap a, Swap b){
        // Do it based on indices
        return (a.getI() == b.getI() && a.getJ() == b.getJ()) ||
        (a.getK() == b.getK() && a.getL() == b.getL()) ||
        (a.getI() == b.getK() && a.getJ() == b.getL()) ||
        (a.getK() == b.getI() && a.getL() == b.getJ());
    }
    
    // For debugging 
    private ArrayList<Double> sumLocMI(double total,int count){
        ArrayList<Double> num = new ArrayList();
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                if(grid.get(i, j).getAssigned() != null)
                    num.add(getMIatLoc(i,j,total,count));
            }
            
        }
        return num;
    }
    // For debugging
    private double sumdouble(ArrayList<Double> list){
        double sum = 0;
        for (Double d : list) {
            sum+=d;
        }
        return sum;
    }
    
    // Returns double x, because it will be used as 4/x
    public double nrNeighbours(int x, int y){
        double neighbours = 0;
        if(x > 0 && grid.get(x-1, y).getAssigned() != null){
            neighbours++;
        }
        if(y > 0 && grid.get(x, y-1).getAssigned() != null){
            neighbours++;
        }
        if(x < grid.getColumns() - 1 && grid.get(x+1, y).getAssigned() != null){
            neighbours++;
        }
        if(y < grid.getRows() - 1 && grid.get(x, y+1).getAssigned() != null){
            neighbours++;
        }
        return neighbours;
    }
    
    // Rook's adjacency computing local Moran's I
    public double getMIatLoc(int x, int y, double total, int count){
        Tile t = grid.get(x, y);
        double avg = total / count; // average over the entire grid
        double localNeighbours = nrNeighbours(x,y);
        
        double num = 0;
        double denom = Math.pow(t.getData()  - avg,2);
        
        if(x > 0 && grid.get(x-1, y).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x-1,y);
            weight = 1;
            num += weight * (t.getData() - avg) * (grid.get(x-1, y).getData() - avg);
            denom += Math.pow(grid.get(x-1, y).getData()  - avg,2);
            
        }
        if(y > 0 && grid.get(x, y-1).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x,y-1);
            weight = 1;
            num += weight * (t.getData() - avg) * (grid.get(x, y-1).getData() - avg);
            denom += Math.pow(grid.get(x, y-1).getData()  - avg,2);
        }
        if(x < grid.getColumns() - 1 && grid.get(x+1, y).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x+1,y);
            weight = 1;
            num += weight * (t.getData() - avg) * (grid.get(x+1, y).getData() - avg);
            denom += Math.pow(grid.get(x+1, y).getData()  - avg,2);
        }
        if(y < grid.getRows() - 1 && grid.get(x, y+1).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x,y+1);
            weight = 1;
            num += weight * (t.getData() - avg) * (grid.get(x, y+1).getData() - avg);
            denom += Math.pow(grid.get(x, y+1).getData()  - avg,2);
        }
        double res = (num/denom);
        return num;
//        return res;
//        if(res > 1 || res < -1){
//            System.out.println("Wrong Morans I:");
//            System.out.println(res);
//        }
//        System.out.println(res);
    }
    // Rook's adjacency computing local Geary's C (num only)
    public double getGCatLoc(int x, int y, double total, int count){
        Tile t = grid.get(x, y);
        double avg = total / count; // average over the entire grid
        double localNeighbours = nrNeighbours(x,y);
        
        double num = 0;
        double denom = Math.pow(t.getData()  - avg,2);
        
        if(x > 0 && grid.get(x-1, y).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x-1,y);
            weight = 1;
            num += weight * Math.pow(t.getData() - grid.get(x-1, y).getData(),2);
            denom += Math.pow(grid.get(x-1, y).getData()  - avg,2);
            
        }
        if(y > 0 && grid.get(x, y-1).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x,y-1);
            weight = 1;
            num += weight * Math.pow(t.getData() - grid.get(x, y-1).getData(),2);
            denom += Math.pow(grid.get(x, y-1).getData()  - avg,2);
        }
        if(x < grid.getColumns() - 1 && grid.get(x+1, y).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x+1,y);
            weight = 1;
            num += weight * Math.pow(t.getData() - grid.get(x+1, y).getData(),2);
            denom += Math.pow(grid.get(x+1, y).getData()  - avg,2);
        }
        if(y < grid.getRows() - 1 && grid.get(x, y+1).getAssigned() != null){
            double weight = 4/localNeighbours + 4/nrNeighbours(x,y+1);
            weight = 1;
            num += weight * Math.pow(t.getData() - grid.get(x, y+1).getData(),2);
            denom += Math.pow(grid.get(x, y+1).getData()  - avg,2);
        }
        double res = (num/denom);
        // Make negative so that it can be used the same as MI.. but it dont work.. it should be squared
        return -num;
//        return res;
//        if(res > 1 || res < -1){
//            System.out.println("Wrong Morans I:");
//            System.out.println(res);
//        }
//        System.out.println(res);
    }
    
    
    // Measure distance between adjacent tiles
    public double getSpatialFromSwap(int i , int j, int k, int l){
        Tile t = grid.get(i, j);
        Tile t2 = grid.get(k, l);
//        double pre = t.getCenter().distanceTo(t.getAssigned().getPos()) + t2.getCenter().distanceTo(t2.getAssigned().getPos());
//        double post = t.getCenter().distanceTo(t2.getAssigned().getPos()) + t2.getCenter().distanceTo(t.getAssigned().getPos());
//        return (post - pre) / Math.abs(pre);
        double currentDist = 0;
        double newDist = 0;
        for (int m = Math.max(0, i-1); m <= Math.min(grid.getColumns()-1, i+1); m++) {
            for (int n = Math.max(0, j-1); n <= Math.min(grid.getRows()-1, j+1); n++) {
                if(grid.get(m, n).getAssigned()!=null && (m == i || n == j)){
                    currentDist += Math.pow(t.getAssigned().getPos().distanceTo(grid.get(m, n).getAssigned().getPos()),2);
                    newDist += Math.pow(t2.getAssigned().getPos().distanceTo(grid.get(m, n).getAssigned().getPos()),2);
                }
            }
        }
        for (int o = Math.max(0, k-1); o <= Math.min(grid.getColumns()-1, k+1); o++) {
            for (int p = Math.max(0, l-1); p <= Math.min(grid.getRows()-1, l+1); p++) {
                if(grid.get(o, p).getAssigned()!=null && (o == i || p == j)){
                    currentDist += Math.pow(t2.getAssigned().getPos().distanceTo(grid.get(o, p).getAssigned().getPos()),2);
                    newDist += Math.pow(t.getAssigned().getPos().distanceTo(grid.get(o, p).getAssigned().getPos()),2);
                }
            }
        }
        return currentDist-newDist;
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
        this.grid = initGrid.clone();
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
