/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

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
    private int count = 0;
    private boolean adjacentMI = true;

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
        if(!adjacentMI){
            return getConsiderateMoransI();
        }
        double N = 0;
        
        
        double meank = 0;
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                if(this.get(i, j).getAssigned() != null){
                    meank += this.get(i,j).getData();
                    N++;
                }
            }
            
        }
        double W = N * 4;
        double num = 0;
        double denom = 0;
        for (int j = 0; j < this.getColumns(); j++) {
            for (int i = 0; i < this.getRows(); i++) {
                if(this.get(j,i).getAssigned() != null){
                    denom += Math.pow(this.get(j,i).getData() - (meank/N), 2);
                    double innersum = 0;
                    double neighbours = 0;
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
                                        neighbours++;
                                    }
                                    if(i == x && j - y >= -1 && j - y <= 1){
//                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                        innersum += (this.get(j,i).getData() - (meank/N)) * (this.get(y,x).getData() - (meank/N));
                                        neighbours++;
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
    
    public void clearGrid(){
        for (Tile t : this){
            t.setAssigned(null);
        }
    }
    
    public double getGearysC(){
        double N = 0;
        double meank = 0;
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                if(this.get(i, j).getAssigned() != null){
                    meank += this.get(i,j).getData();
                    N++;
                }
            }
            
        }
        double W = N * 4;
        double num = 0;
        double denom = 0;
        for (int j = 0; j < this.getColumns(); j++) {
            for (int i = 0; i < this.getRows(); i++) {
                if(this.get(j,i).getAssigned() != null){
                    denom += Math.pow(this.get(j,i).getData() - (meank/N), 2);
                    double innersum = 0;
                    double neighbours = 0;
                    for (int y = Math.max(0,j-1); y < Math.min(this.getColumns(),j+2); y++) {
                        for (int x = Math.max(0,i-1); x < Math.min(this.getRows(),i+2); x++) {
                            if(y != j || x != i){
                                // Not Counting Diagonal Neighbours
                                if(this.get(y,x).getAssigned() != null){
                                    if(i - x >= -1 && i - x <= 1 && j == y){
//                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                        innersum += Math.pow(this.get(j,i).getData() - this.get(y,x).getData(),2);
                                        neighbours++;
                                    }
                                    if(i == x && j - y >= -1 && j - y <= 1){
//                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                        innersum += Math.pow(this.get(j,i).getData() - this.get(y,x).getData(),2);
                                        neighbours++;
                                    }
                                }
                            }
                        }
                    }
//                    innersum = 8/neighbours * innersum;
                    innersum = (4/neighbours) * innersum;
                    num += innersum;
                }
            }
        }
        if(num == 0 && denom == 0){
            return 0;
        }
//        return num;
//        return ((N/W) * (num/denom))/(N*N);
        return (N-1)*num/(2*W*denom);
    }
    
    public double getConsiderateMoransI(){
        double maxDist = this.get(0,0).getCenter().distanceTo(this.get(this.getColumns()-1, this.getRows()-1).getCenter());
        
        double N = 0;
        double W = 0;
        
        double meank = 0;
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                if(this.get(i, j).getAssigned() != null){
                    N++;
                    meank += this.get(i,j).getData();
                }
            }
            
        }
        double num = 0;
        double denom = 0;
        for (Tile t1 : this) {
                if(t1.getAssigned() != null){
                    denom += Math.pow(t1.getData() - (meank/N), 2);
                    double innersum = 0;
                    for (Tile t2 : this) {
                                if(t2.getAssigned() != null && t1 != t2){
                                    double weight = maxDist - t1.getCenter().distanceTo(t2.getCenter());
                                    innersum += weight * (t1.getData() - (meank/N)) * (t2.getData() - (meank/N));
                                    W += weight;
                                }
                    }
                    num += innersum;
                }
        }
        if(num == 0 && denom == 0){
            return 1;
        }
//        return ((N/W) * (num/denom))/(N*N);
        return ((N/W) * (num/denom));
    }
    
//    public double getSwappedMoransI(int x, int y, int p, int q){
//        return 1;
//    }
    
    public double getSpatialDistortion(){
        double sum = 0;
        double norm = this.get(0,0).getLength();
        for(Tile t1 : this){
            for(Tile t2 : this){
                if(t1 != t2 && t1.getAssigned() != null && t2.getAssigned() != null){
                    Vector tileDiff = Vector.subtract(t1.getCenter(), t2.getCenter());
                    Vector regionDiff = Vector.subtract(t1.getAssigned().getPos(), t2.getAssigned().getPos());
                    sum += Math.sqrt(Vector.subtract(tileDiff, regionDiff).length()/norm);
                    
                } 
            }
        }
        return sum / (this.getColumns()*this.getRows()*this.getColumns()*this.getRows());
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
    
    public int getCount(){
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
