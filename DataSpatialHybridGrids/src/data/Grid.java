/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.util.Iterator;
import java.util.List;
import nl.tue.geometrycore.datastructures.list2d.List2D;
import nl.tue.geometrycore.geometry.Vector;

/**
 *
 * @author wmeulema
 */
public class Grid extends List2D<Tile> {

    public Grid() {
    }

    public Grid(int columns, int rows) {
        super(columns, rows);
    }
    
    public double getMoransI(){
        double N = this.getColumns() * this.getRows();
        double W = (this.getColumns()-2) * (this.getRows()-2) * 4 + (this.getColumns()-2) * 3 * 2 + (this.getRows()-2) * 3 * 2 + 8;
        
        double meank = 0;
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                if(this.get(i, j).getAssigned() != null)
                    meank += this.get(i,j).getData();
            }
            
        }
        double num = 0;
        double denom = 0;
        for (int j = 0; j < this.getColumns(); j++) {
            for (int i = 0; i < this.getRows(); i++) {
                if(this.get(j,i).getAssigned() != null){
                    denom += Math.pow(this.get(j,i).getData() - meank/N, 2);
                    double innersum = 0;
                    for (int y = Math.max(0,j-1); y < Math.min(this.getColumns(),j+2); y++) {
                        for (int x = Math.max(0,i-1); x < Math.min(this.getRows(),i+2); x++) {
                            if(y != j || x != i){
                                // Counting Diagonal Neighbours
    //                            if(i - x >= -1 && i - x <= 1 && y - j >= -1 && y - j <= 1){
    //                                innersum += (permuted[j][i] * N - meank) * (permuted[y][x] * N - meank);
    //                            }
                                // Not Counting Diagonal Neighbours
                                if(this.get(y,x).getAssigned() != null){
                                    if(i - x >= -1 && i - x <= 1 && j == y){
                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                    }
                                    if(i == x && j - y >= -1 && j - y <= 1){
                                        innersum += (this.get(j,i).getData() * N - meank) * (this.get(y,x).getData() * N - meank);
                                    }
                                }
                            }
                        }
                    }
                    num += innersum;
                }
            }
        }
        if(num == 0 && denom == 0){
            return 1;
        }
        return ((N/W) * (num/denom))/(N*N);
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
    
    @Override
    public Grid clone(){
        Grid res = new Grid(this.getColumns(), this.getRows());
        for (int i = 0; i < this.getColumns(); i++) {
            for (int j = 0; j < this.getRows(); j++) {
                res.set(i, j, this.get(i, j).clone());
            }
            
        }
        return res;
    }
    
}
