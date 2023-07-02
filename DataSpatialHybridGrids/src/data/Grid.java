/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import nl.tue.geometrycore.datastructures.list2d.List2D;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author wmeulema
 */
public class Grid extends List2D<Tile> {
    
    private double total = 0;
    private double adjacencies = 0;
    private double count = 0;
    private double MIDenom = 0;
    private boolean adjacentMI = true;

    public void resetData(){
        total = 0;
        adjacencies = 0;
        count = 0;
        MIDenom = 0;
    }
    
    public void setAdjacentMI(boolean adjacentMI) {
        this.adjacentMI = adjacentMI;
    }
    
    public Grid() {
    }

    public Grid(int columns, int rows, boolean adjacentMI) {
        super(columns, rows);
        this.adjacentMI = adjacentMI; 
    }
    
    public double getMoransI(){
        double N = getCount();
        
        
        double meank = getTotal();
        double W = getAdjacencies();
        double num = 0;
        double denom = 0;
        for (int j = 0; j < this.getColumns(); j++) {
            for (int i = 0; i < this.getRows(); i++) {
                if(this.get(j,i).getAssigned() != null){
                    denom += Math.pow(this.get(j,i).getData() - (meank/N), 2);
                    double innersum = 0;
                    for (int y = Math.max(0,j-1); y < Math.min(this.getColumns(),j+2); y++) {
                        for (int x = Math.max(0,i-1); x < Math.min(this.getRows(),i+2); x++) {
                            if(y != j || x != i){
                                // Counting Diagonal Neighbours
    //                            if(i - x >= -1 && i - x <= 1 && y - j >= -1 && y - j <= 1){
    //                                innersum += (permuted[j][i] * N - meank) * (permuted[y][x] * N - meank);
    //                                neighbours++;
    //                            }
                                // Not Counting Diagonal Neighbours
                                if(this.get(y,x).getAssigned() != null){
                                    if(i - x >= -1 && i - x <= 1 && j == y){
//                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                        innersum += (this.get(j,i).getData() - (meank/N)) * (this.get(y,x).getData() - (meank/N));
                                    }
                                    if(i == x && j - y >= -1 && j - y <= 1){
//                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                        innersum += (this.get(j,i).getData() - (meank/N)) * (this.get(y,x).getData() - (meank/N));
                                    }
                                }
                            }
                        }
                    }
//                    innersum = 8/neighbours * innersum;
//                    innersum = (4/neighbours) * innersum;
                    num += innersum;
                }
            }
        }
        if(num == 0 && denom == 0){
            return 1;
        }
//        return num;
//        return ((N/W) * (num/denom))/(N*N);
        return ((N/W) * (num/denom));
    }
    
    
    // Computes Local MI, accounts for double counting.
    public double getLocalMoransI(int i, int j){
        double N = getCount();
        double meank = getTotal();
        double W = getAdjacencies();
        double num = 0;
        if(this.get(i,j).getAssigned() != null){
            double innersum = 0;
            for (int x = Math.max(0,i-1); x < Math.min(this.getColumns(),i+2); x++) {
                for (int y = Math.max(0,j-1); y < Math.min(this.getRows(),j+2); y++) {
                    if(x != i || j != y){
                        // Counting Diagonal Neighbours
    //                            if(i - x >= -1 && i - x <= 1 && y - j >= -1 && y - j <= 1){
    //                                innersum += (permuted[j][i] * N - meank) * (permuted[y][x] * N - meank);
    //                            }
                        // Not Counting Diagonal Neighbours
                        if(this.get(x,y).getAssigned() != null){
                            if(i - x >= -1 && i - x <= 1 && j == y){
    //                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                innersum += (this.get(i,j).getData() - (meank/N)) * (this.get(x,y).getData() - (meank/N));
                            }
                            if(i == x && j - y >= -1 && j - y <= 1){
    //                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                innersum += (this.get(i,j).getData() - (meank/N)) * (this.get(x,y).getData() - (meank/N));
                            }
                        }
                    }
                }
            }
            num += innersum;
        }
        double denom = getMIDenom();
        return 2 * ((N/W) * (num/denom));
    }
    
    // Factors in double counting
    public double getLocalMIAdjacency(int i, int j, int k , int l){
        // Check if adjacent
        if((i == k && Math.abs(j-l) == 1) || (j == l && Math.abs(i-k) == 1)){
            double N = getCount();
            double meank = getTotal();
            double W = getAdjacencies();
            double denom = getMIDenom();
            double num = (this.get(i,j).getData() - (meank/N)) * (this.get(k,l).getData() - (meank/N));
            return 2 * ((N/W) * (num/denom));
        }
        return 0;
    }
        
    
    public void clearGrid(){
        for (Tile t : this){
            t.setAssigned(null);
        }
    }
    
//    public double getGearysC(){
//        double N = 0;
//        double meank = 0;
//        for (int i = 0; i < this.getColumns(); i++) {
//            for (int j = 0; j < this.getRows(); j++) {
//                if(this.get(i, j).getAssigned() != null){
//                    meank += this.get(i,j).getData();
//                    N++;
//                }
//            }
//            
//        }
//        double W = N * 4;
//        double num = 0;
//        double denom = 0;
//        for (int j = 0; j < this.getColumns(); j++) {
//            for (int i = 0; i < this.getRows(); i++) {
//                if(this.get(j,i).getAssigned() != null){
//                    denom += Math.pow(this.get(j,i).getData() - (meank/N), 2);
//                    double innersum = 0;
//                    double neighbours = 0;
//                    for (int y = Math.max(0,j-1); y < Math.min(this.getColumns(),j+2); y++) {
//                        for (int x = Math.max(0,i-1); x < Math.min(this.getRows(),i+2); x++) {
//                            if(y != j || x != i){
//                                // Not Counting Diagonal Neighbours
//                                if(this.get(y,x).getAssigned() != null){
//                                    if(i - x >= -1 && i - x <= 1 && j == y){
////                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
//                                        innersum += Math.pow(this.get(j,i).getData() - this.get(y,x).getData(),2);
//                                        neighbours++;
//                                    }
//                                    if(i == x && j - y >= -1 && j - y <= 1){
////                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
//                                        innersum += Math.pow(this.get(j,i).getData() - this.get(y,x).getData(),2);
//                                        neighbours++;
//                                    }
//                                }
//                            }
//                        }
//                    }
////                    innersum = 8/neighbours * innersum;
//                    innersum = (4/neighbours) * innersum;
//                    num += innersum;
//                }
//            }
//        }
//        if(num == 0 && denom == 0){
//            return 0;
//        }
////        return num;
////        return ((N/W) * (num/denom))/(N*N);
//        return (N-1)*num/(2*W*denom);
//    }
    
    
//    public double getSwappedMoransI(int x, int y, int p, int q){
//        return 1;
//    }
    
    // Direction oblvious metric
    public double getSpatialDistortion(){
        return getSpatialDistortion2();
//        double sum = 0;
//        double norm = this.get(0,0).getLength();
//        for(Tile t1 : this){
//            for(Tile t2 : this){
//                if(t1 != t2 && t1.getAssigned() != null && t2.getAssigned() != null){
//                    Vector tileDiff = Vector.subtract(t1.getCenter(), t2.getCenter());
//                    Vector regionDiff = Vector.subtract(t1.getAssigned().getPos(), t2.getAssigned().getPos());
//                    sum += Math.sqrt(Vector.subtract(tileDiff, regionDiff).length()/norm);
//                    
//                } 
//            }
//        }
//        return sum / (this.getColumns()*this.getRows()*this.getColumns()*this.getRows());
        

//        double totalDist = 0;
//        double adjacencies = 0;
//        double regions = 0;
//        for (int i = 0; i < this.getColumns(); i++) {
//            for (int j = 0; j < this.getRows(); j++) {
//                Tile t = this.get(i,j);
//                if(t.getAssigned() != null){
//                    regions++;
//                    for (int m = Math.max(0, i-1); m <= Math.min(this.getColumns()-1, i+1); m++) {
//                        for (int n = Math.max(0, j-1); n <= Math.min(this.getRows()-1, j+1); n++) {
//                            if(this.get(m, n).getAssigned()!=null && (m == i ^ n == j)){
//                                adjacencies++;
//                                double dist = t.getAssigned().getPos().distanceTo(this.get(m, n).getAssigned().getPos()) / t.getShape().width();
//                                totalDist += Math.pow(dist,2);
//                            }
//                        }
//                    }
//                }
//                
//            }
//            
//        }
////        totalDist = 100 * totalDist / (this.getBoundingBox().width()* adjacencies);
//        totalDist = totalDist / (adjacencies);
//        return totalDist;
    
    }
    
    public double getSpatialDistortion2(){
        double totalDist = 0;
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                Tile t = this.get(i,j);
                if(t.getAssigned() != null){
                    for (int m = Math.max(0, i-1); m <= Math.min(this.getColumns()-1, i+1); m++) {
                        for (int n = Math.max(0, j-1); n <= Math.min(this.getRows()-1, j+1); n++) {
                            if(this.get(m, n).getAssigned()!=null && (m == i ^ n == j)){
                                Vector v1 = Vector.subtract(t.getAssigned().getPos(),this.get(m, n).getAssigned().getPos());
                                Vector v2 = Vector.subtract(t.getCenter(),this.get(m, n).getCenter());
                                totalDist += Math.pow(v1.distanceTo(v2) / t.getShape().width(),2);
                            }
                        }
                    }
                }
                
            }
            
        }
        totalDist = totalDist / this.getAdjacencies();
//        totalDist = 100 * Math.sqrt(totalDist) / (this.getBoundingBox().width()* adjacencies);
        return totalDist;
    }
    
    
    // Accounts for double counting in original method
    public double getLocalSpatialDistortion2(int i,int j){
        Tile t = this.get(i,j);
        double totalDist = 0;
        if(t.getAssigned() != null){
            for (int m = Math.max(0, i-1); m <= Math.min(this.getColumns()-1, i+1); m++) {
                for (int n = Math.max(0, j-1); n <= Math.min(this.getRows()-1, j+1); n++) {
                    if(this.get(m, n).getAssigned()!=null && (m == i ^ n == j)){
                        Vector v1 = Vector.subtract(t.getAssigned().getPos(),this.get(m, n).getAssigned().getPos());
                        Vector v2 = Vector.subtract(t.getCenter(),this.get(m, n).getCenter());
                        totalDist += Math.pow(v1.distanceTo(v2) / t.getShape().width(),2);
                    }
                }
            }
        }
        return 2 * totalDist / getAdjacencies();
    }
    
    // Factors in double counting
    public double getLocalSAdjacency(int i, int j, int k , int l){
        // Check if adjacent
        if((i == k && Math.abs(j-l) == 1) || (j == l && Math.abs(i-k) == 1)){
            Tile t = this.get(i,j);
            Vector v1 = Vector.subtract(t.getAssigned().getPos(),this.get(k, l).getAssigned().getPos());
            Vector v2 = Vector.subtract(t.getCenter(),this.get(k, l).getCenter());
            double totalDist = Math.pow(v1.distanceTo(v2) / t.getShape().width(),2);
            return 2 * totalDist / getAdjacencies();
        }
        return 0;
    }
    
    public double getAdjacencies(){
        if(adjacencies > 0){
            return adjacencies;
        }
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                Tile t = this.get(i,j);
                if(t.getAssigned() != null){
                    for (int m = Math.max(0, i-1); m <= Math.min(this.getColumns()-1, i+1); m++) {
                        for (int n = Math.max(0, j-1); n <= Math.min(this.getRows()-1, j+1); n++) {
                            if(this.get(m, n).getAssigned()!=null && (m == i ^ n == j)){
                                adjacencies++;
                            }
                        }
                    }
                }
                
            }
            
        }
        return adjacencies;
    }
    
    
    public double adjacentSquaredDistances(int i, int j){
////        double dist = Double.POSITIVE_INFINITY;
//        Tile t = this.get(i, j);
//        double dist = 0;
//        ArrayList<Double> dists = new ArrayList<>();
//        for (int m = Math.max(0, i-1); m <= Math.min(this.getColumns()-1, i+1); m++) {
//            for (int n = Math.max(0, j-1); n <= Math.min(this.getRows()-1, j+1); n++) {
//                if(this.get(m, n).getAssigned()!=null && (m == i ^ n == j)){
//                    dists.add(t.getAssigned().getPos().distanceTo(this.get(m, n).getAssigned().getPos()));
////                    dist = Math.min(t.getAssigned().getPos().distanceTo(this.get(m, n).getAssigned().getPos()),dist);
////                    dist += t.getAssigned().getPos().distanceTo(this.get(m, n).getAssigned().getPos());
//                }
//            }
//        }
//        Collections.sort(dists);
//        for (int k = 0; k < dists.size(); k++) {
//            dist += Math.pow(dists.get(k), 4-k);
//            
//        }
////        dist = (4/dists.size()) * dist;
//        return dist;
        // Ring around tile, distance of original tiles
        Tile t = this.get(i, j);
        double dist = 0;
        for (int m = Math.max(0, i-1); m <= Math.min(this.getColumns()-1, i+1); m++) {
            for (int n = Math.max(0, j-1); n <= Math.min(this.getRows()-1, j+1); n++) {
                if(this.get(m, n).getAssigned()!=null){
                    int x1 = t.getAssigned().getSpatialCol();
                    int y1 = t.getAssigned().getSpatialRow();
                    int x2 = this.get(m, n).getAssigned().getSpatialCol();
                    int y2 = this.get(m, n).getAssigned().getSpatialRow();
                    dist += Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
                }
            }
        }
//        System.out.println("Tile " + i + "," + j);
//        System.out.println("Init " + t.getAssigned().getSpatialCol() + "," + t.getAssigned().getInitRow());
//        System.out.println(dist);
        return dist;
        
    }
    
    public Rectangle getBoundingBox() {
        Rectangle r = new Rectangle();
        for (Tile t : this) {
            if(t.getShape()!= null)
                r.includeGeometry(t.getShape());
        }
        return r;
    }
    
    public double getTotal(){
        if(total > 0){
            return total;
        }
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                if(this.get(i,j).getAssigned() != null){
                    total += this.get(i,j).getData();
                }
            }
            
        }
        return total;
    }
    
    public double getCount(){
        if(count > 0){
            return count;
        }
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                if(this.get(i,j).getAssigned() != null){
                    count ++;
                }
            }
            
        }
        return count;
    }
    
    public double getMIDenom(){
        if(MIDenom > 0){
            return MIDenom;
        }
        double N = getCount();
        
        
        double meank = getTotal();
        double denom = 0;
        for (int j = 0; j < this.getColumns(); j++) {
            for (int i = 0; i < this.getRows(); i++) {
                if(this.get(j,i).getAssigned() != null){
                    denom += Math.pow(this.get(j,i).getData() - (meank/N), 2);
                }
            }
        }
        MIDenom = denom;
        return MIDenom;
    }
    
    @Override
    public Grid clone(){
        Grid res = new Grid(this.getColumns(), this.getRows(),this.adjacentMI);
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                res.set(i, j, this.get(i, j).clone());
            }
            
        }
        return res;
    }
    
}
