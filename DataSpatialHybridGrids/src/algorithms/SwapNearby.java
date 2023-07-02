/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import data.GeographicMap;
import data.Grid;
import data.Region;
import data.Swap;
import data.Tile;
import dataspatialhybridgrids.DrawPanel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

    public void setRange(int range) {
        this.range = range;
    }
    boolean improvingSpace = false;
    double miUsed = 0;
    double miMaxUse = 0.01; // 0.01, 0.05, 0.1 0.15, 0.20 0.25 0.3 0.5
    double miSacrificePerUse = miMaxUse;
    double similarity = 0.01;
    

    
    public SwapNearby(GeographicMap map, Grid initGrid, double spatialSlack) {
        this.similarity = spatialSlack;
        this.map = map;
        setInitGrid(initGrid);
    }
    
    public void setToSpatialGain(){
        improvingSpace = true;
        miUsed = 0;
    }
    
    // Terrible
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
    
    // Good
    public void simAn(double startT, double endT, int maxIterations){
        // INSTRUCTION: Uncomment this for the range implementation
//        if(range == 0){
//            return;
//        }
        if(this.improvingSpace){
            range = Math.max(grid.getColumns(), grid.getRows());
//            range = 1;
        }
        double initialMI = grid.getMoransI();
        double initialS = grid.getSpatialDistortion();
        int maxDimension = Math.max(grid.getColumns(), grid.getRows());
        range = Math.min(range, maxDimension);
        System.out.println("Similarity: " + this.similarity);
        double fac = Math.pow(endT / startT, 1.0/maxIterations);
        double T = startT;
        Random rand = new Random(10);
        double curCost = this.improvingSpace? initialS : initialMI;
        double curS = initialS;
        double curMI = initialMI;
        double bestCost = curCost;
        Grid bestSolution = grid.clone();
        for (int iter = 0; iter < maxIterations; iter++) {
            
            // INSTRUCTION: For the range implementation
//            Swap s = this.improvingSpace? getRandomSwap(rand) : getRandomSwap(rand,this.range);
            // INSTRUCTION: For the S deviation implementation (takes the similarity as the that it may increase S)
            Swap s = getRandomSwap(rand);
            
            double deltaS = grid.getLocalSpatialDistortion2(s.getI(), s.getJ()) + grid.getLocalSpatialDistortion2(s.getK(), s.getL()) - grid.getLocalSAdjacency(s.getI(), s.getJ(), s.getK(), s.getL());
            double deltaMI = grid.getLocalMoransI(s.getI(), s.getJ()) + grid.getLocalMoransI(s.getK(), s.getL()) - grid.getLocalMIAdjacency(s.getI(), s.getJ(), s.getK(), s.getL());
            
            Tile t1 = grid.get(s.getI(), s.getJ());
            Tile t2 = grid.get(s.getK(), s.getL());
            
            // Do swap
            Region r1 = t1.getAssigned();
            t1.setAssigned(t2.getAssigned());
            t2.setAssigned(r1);
            
            double newS = curS - deltaS + grid.getLocalSpatialDistortion2(s.getI(), s.getJ()) + grid.getLocalSpatialDistortion2(s.getK(), s.getL())  - grid.getLocalSAdjacency(s.getI(), s.getJ(), s.getK(), s.getL());
            double newMI = curMI - deltaMI + grid.getLocalMoransI(s.getI(), s.getJ()) + grid.getLocalMoransI(s.getK(), s.getL())  - grid.getLocalMIAdjacency(s.getI(), s.getJ(), s.getK(), s.getL());
            
//            if(Math.abs(newS - grid.getSpatialDistortion()) > 0.0001 ){
//                System.out.println("===");
//                System.out.println(newS);
//                System.out.println(grid.getSpatialDistortion());
//                t2.setAssigned(t1.getAssigned());
//                t1.setAssigned(r1);
//                System.out.println("---");
//                return;
//            }
//            if(Math.abs(newMI - grid.getMoransI()) > 0.0001){
//                System.out.println("===");
//                System.out.println(newMI);
//                System.out.println(grid.getMoransI());
//                t2.setAssigned(t1.getAssigned());
//                t1.setAssigned(r1);
//                System.out.println("---");
//                return;
//            }
            
            double newCost = this.improvingSpace? newS : newMI;
            double prob = this.improvingSpace? Math.min(1.0, Math.exp((newCost - curCost)/T)) : Math.min(1.0, Math.exp((curCost - newCost)/T));
            // INSTRUCTION: Remove last or clause when using range implementation
            if (rand.nextDouble() >= prob || (this.improvingSpace && initialMI - newMI > similarity) || (!this.improvingSpace && newS - initialS > similarity)) {
                //Undo swap
                t2.setAssigned(t1.getAssigned());
                t1.setAssigned(r1);
            }
            else { 
                curCost = newCost;
                curS = newS;
                curMI = newMI;
            }
            boolean better = this.improvingSpace ? curCost < bestCost : bestCost < curCost;
            if(better){
                bestCost = curCost;
                bestSolution = grid.clone();
            }
            T *= fac;

            if (iter%1000000 == 0) System.out.println(curCost);
            
        }
        grid = bestSolution;
        System.out.println("Final answer");
        System.out.println(bestCost);
    }
    
    public Swap getRandomSwap(Random rand){
        int i = rand.nextInt(grid.getColumns());
        int j = rand.nextInt(grid.getRows());
        int k = rand.nextInt(grid.getColumns());
        int l = rand.nextInt(grid.getRows());
        while(grid.get(i, j).getAssigned() == null){
            i = rand.nextInt(grid.getColumns());
            j = rand.nextInt(grid.getRows());
        }
        while(grid.get(k, l).getAssigned() == null || (i == k && j == l)){
            k = rand.nextInt(grid.getColumns());
            l = rand.nextInt(grid.getRows());
        }
        return new Swap(0,i,j,k,l);
    }
    
    public Swap getRandomSwap(Random rand, double slack){
        int i = rand.nextInt(grid.getColumns());
        int j = rand.nextInt(grid.getRows());
        int k = rand.nextInt(grid.getColumns());
        int l = rand.nextInt(grid.getRows());
        while(grid.get(i, j).getAssigned() == null){
            i = rand.nextInt(grid.getColumns());
            j = rand.nextInt(grid.getRows());
        }
        while(grid.get(k, l).getAssigned() == null || (i == k && j == l)){
            k = rand.nextInt(grid.getColumns());
            l = rand.nextInt(grid.getRows());
        }
        if(Math.abs(grid.get(i, j).getData() - grid.get(k, l).getData()) > slack){
            return getRandomSwap(rand,slack);
        }
        return new Swap(0,i,j,k,l);
    }
    
    public Swap getRandomSwap(Random rand, int range){
        int i = rand.nextInt(grid.getColumns());
        int j = rand.nextInt(grid.getRows());
        
        while(grid.get(i, j).getAssigned() == null){
            i = rand.nextInt(grid.getColumns());
            j = rand.nextInt(grid.getRows());
        }
        Region r = grid.get(i, j).getAssigned();
        int initCol = r.getInitCol();
        int initRow = r.getInitRow();
        
        //Effective range
        int lowerColBound = Math.max(initCol - range, 0);
        int lowerRowBound = Math.max(initRow - range, 0);
        int colRange = Math.min(initCol + range, grid.getColumns() - 1) - lowerColBound;
        int rowRange = Math.min(initRow + range, grid.getRows() - 1) - lowerRowBound;
        
        int k = rand.nextInt(colRange);
        int l = rand.nextInt(rowRange);
        k += lowerColBound;
        l += lowerRowBound;
        if(grid.get(k, l).getAssigned() == null || (i == k && j == l) || Math.abs(grid.get(k, l).getAssigned().getInitCol() - i) > range || Math.abs(grid.get(k, l).getAssigned().getInitRow() - j) > range){
            return getRandomSwap(rand,range);
        }
        return new Swap(0,i,j,k,l);
    }
    
    // Local search - not recommended
    public void betterSwap(int range, double dataSpatial){
        if(range < this.range){
            this.range = range;
            grid = initGrid.clone();
        }
        if(this.improvingSpace){
            range = Math.max(grid.getColumns(), grid.getRows());
//            range = 1;
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
                addPossibleSwaps(q,inQueue,i,j,range,total,count);
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
//                System.out.println(s.toString());
                if(getSpatialFromSwap(s.getI(),s.getJ(),s.getK(),s.getL()) < 0){
                    System.out.println("This is a shit swap");
                    continue;
                }
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
            if(false && improvingSpace){
                newmi = grid.getMoransI();
                miUsed += (oldmi-newmi);
                if(miUsed > miMaxUse || s.getMiGain() < 0){ //undo swap and stop
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
            List<Swap> swaplist = new ArrayList(q.extractContents());
            for(Swap swap : swaplist){
                if(swap == null)
                    continue;
                if(hasOverlappingTiles(s,swap)){
                    q.remove(swap);
                    inQueue.remove(swap.computeHash(maxDimension));
                }
                else if(true || isAdjacent(s.getK(),s.getL(),swap) || isAdjacent(s.getI(),s.getJ(),swap)){
                    double gain = getSwapGain(swap.getI(),swap.getJ(),swap.getK(),swap.getL(),total,count);
                    if(false && gain > 0) {
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
            for (int i = 0; i < grid.getColumns(); i++) {
                for (int j = 0; j < grid.getRows(); j++) {
                    addPossibleSwaps(q,inQueue,i,j,range,total,count);
                }
            }
            for (int a = -1; a <= 1 ; a++) {
                for (int b = -1; b <= 1 ; b++) {
//                    if(a == 0 || b == 0){
//                    addPossibleSwaps(q,inQueue,s.getI()+a,s.getJ()+b,range,total,count);
//                    addPossibleSwaps(q,inQueue,s.getK()+a,s.getL()+b,range,total,count);}
////                    for (int i = -range; i <= range; i++) {
////                        for (int j = -range; j <= range; j++) {
////                            if(a == 0 || b == 0){
////                                // Do T1
////                                int tx = s.getK() + a;
////                                int ty = s.getL() + b;
////                                if(tx < 0 || ty  < 0 || tx >= grid.getColumns() || ty >= grid.getRows()){
////                                    continue;
////                                }
////                                Tile test1 = grid.get(tx, ty);
////                                if(test1.getAssigned() == null)
////                                    continue;
////                                int cx = tx + i;
////                                int cy = ty + j;
////                                // Check if in bounds
////                                if(cx < 0 || cy  < 0 || cx >= grid.getColumns() || cy >= grid.getRows()){
////                                    continue;
////                                }
////                                Tile candidate = grid.get(cx, cy);
////                                if(candidate.getAssigned() == null)
////                                    continue;
////                                if((Math.abs(cx-test1.getAssigned().getInitCol()) <= range && Math.abs(cy-test1.getAssigned().getInitRow())<= range) &&
////                                        (Math.abs(tx-candidate.getAssigned().getInitCol()) <= range && Math.abs(ty-candidate.getAssigned().getInitRow())<= range)){
////                                    double gain = getSwapGain(tx,ty,cx,cy,total,count);
////                                    if(gain > 0){
////                                        Swap newswap = new Swap(gain,tx,ty,cx,cy);
////                                        if(!inQueue.contains(newswap.computeHash(maxDimension))){
////                                            q.add(newswap);
////                                            inQueue.add(newswap.computeHash(maxDimension));
////                                        }
////                                    }
////                                }
////                                // Do T2
////                                tx = s.getI() + a;
////                                ty = s.getJ() + b;
////                                if(tx < 0 || ty  < 0 || tx >= grid.getColumns() || ty >= grid.getRows()){
////                                    continue;
////                                }
////                                Tile test2 = grid.get(tx, ty);
////                                if(test2.getAssigned() == null)
////                                    continue;
////                                cx = tx + i;
////                                cy = ty + j;
////                                if(cx < 0 || cy  < 0 || cx >= grid.getColumns() || cy >= grid.getRows()){
////                                    continue;
////                                }
////                                candidate = grid.get(cx, cy);
////                                if(candidate.getAssigned() == null)
////                                    continue;
////                                if((Math.abs(cx-test2.getAssigned().getInitCol()) <= range && Math.abs(cy-test2.getAssigned().getInitRow())<= range) &&
////                                        (candidate.getAssigned() == null || Math.abs(tx-candidate.getAssigned().getInitCol()) <= range && Math.abs(ty-candidate.getAssigned().getInitRow())<= range)){
////                                    double gain = getSwapGain(tx,ty,cx,cy,total,count);
////                                    if(gain > 0){
////                                        Swap newswap = new Swap(gain,tx,ty,cx,cy);
////                                        if(!inQueue.contains(newswap.computeHash(maxDimension))){
////                                            q.add(newswap);
////                                            inQueue.add(newswap.computeHash(maxDimension));
////                                        }
////                                    }
////                                }
////                            }
////                        }
////                    }
                }
            }
        }
    }
    
    public void addPossibleSwaps(IndexedPriorityQueue<Swap> q, HashSet<Integer> inQueue, int i, int j, int range, double total,int count){
        int maxDimension = Math.max(grid.getColumns(), grid.getRows());
        if(i < 0 || i >= grid.getColumns() || j < 0 || j >= grid.getRows()){
            return;
        }
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
                            double gain = getSwapGain(i,j,k,l,total,count);
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
    
    public boolean isSwap(Swap s, int i, int j , int k, int l){
        return (s.getI() == i && s.getJ() == j && s.getK() == k && s.getL() == l);
    }
    
    public boolean similarData(Tile t, Tile t2){
        return Math.abs(t.getData() - t2.getData()) < (map.getAvgData()) * similarity;
    }
    
    public double getSwapGain(int i, int j, int k, int l, double total, int count){
        if(i == k && j == l){
            return -1;
        }
        Tile t = grid.get(i, j);
        Tile t2 = grid.get(k, l);
        
        if(improvingSpace){
            if(Math.abs(getSpatialFromSwap(i , j, k, l) - getSpatialFromSwap(k , l, i, j)) > 0.1){
                System.out.println("Not symmetric:");
                System.out.println(getSpatialFromSwap(i , j, k, l));
                System.out.println(getSpatialFromSwap(k , l, i, j));
                System.out.println(Math.abs(getSpatialFromSwap(i , j, k, l) - getSpatialFromSwap(k , l, i, j)));
            }
            if(getSpatialFromSwap(i, j, i, j) != 0){
                System.out.println("Not reflexive");
            }
            // CHeck gain > 0 and data similar enough
            if(similarData(t,t2)){
                return getSpatialFromSwap(i , j, k, l);
            }
            else{
                return -1;
            }
        }
        
        double oldMIGRID = 0;
        double oldS = 0;
        double newMIGRID = 0;
        double newS = 0;
        
        double oldMI = getMIatLoc(i,j,total,count) + getMIatLoc(k,l,total,count);
        if(improvingSpace){
            oldMIGRID = grid.getMoransI();
            oldS = grid.getSpatialDistortion();
        }
        
        ArrayList<Double> oldMISUM = sumLocMI(total,count);
        grid.set(i, j, t2);
        grid.set(k, l, t);
        double newMI = getMIatLoc(i,j,total,count) + getMIatLoc(k,l,total,count);
        if(improvingSpace){
            newMIGRID = grid.getMoransI();
            newS = grid.getSpatialDistortion();
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

        if(!improvingSpace){
            return newMI - oldMI;
        }
        double gain = getSpatialFromSwap(i , j, k, l);
//        double gain = oldS-newS;
        if(gain > 0 && oldMIGRID - newMIGRID < miMaxUse - miUsed){
            if(newS > oldS){
//                System.out.println("Error code 489: Spatial assesment failure");
                getSpatialFromSwap(i , j, k, l);
            }
            return gain;
        }
        return -1;
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
        
        currentDist = distancesAroundTiles(i,j,k,l);
        
        grid.set(i, j, t2);
        grid.set(k, l, t);
        
        newDist = distancesAroundTiles(i,j,k,l);
        
        grid.set(i, j, t);
        grid.set(k, l, t2);
//        currentDist = currentDist / (grid.getBoundingBox().width() * 430);
//        newDist = newDist / (grid.getBoundingBox().width() * 430);
        return currentDist-newDist;
    }
    
    public double distancesAroundTiles(int i, int j, int k, int l){
        double dists = 0;
        for (int a = -1; a <= 1 ; a++) {
                for (int b = -1; b <= 1 ; b++) {
                    if(a == 0 || b == 0 || true){
                        int x = i + a;
                        int y = j + b;
                        if(x >= 0 && x < grid.getColumns() && y >= 0 && y < grid.getRows() && grid.get(x, y).getAssigned() != null){
                            dists += grid.adjacentSquaredDistances(x, y);
        
                        }
                        x = k + a;
                        y = l + b;
                        if(x >= 0 && x < grid.getColumns() && y >= 0 && y < grid.getRows() && grid.get(x, y).getAssigned() != null){
                            dists += grid.adjacentSquaredDistances(x, y);
                        }
                    }
                }
        }
        return dists;
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
