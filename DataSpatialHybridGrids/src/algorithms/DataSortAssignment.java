/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import data.GeographicMap;
import data.Grid;
import data.Region;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author 20184261
 */
public class DataSortAssignment {
    Grid grid;
    GeographicMap geoMap;

    public DataSortAssignment(Grid grid, GeographicMap geoMap) {
        this.grid = grid;
        this.geoMap = geoMap;
    }
    
    public void assign(){
        ArrayList<Region> regions = new ArrayList<>();
        for(Region r : geoMap){
            regions.add(r);
        }
        regions.sort(new DataSort());
        
        int regIndex = 0;
        int diagCount = 0;
        int i,j = 0;
        while(diagCount < (grid.getColumns() + grid.getRows())-1 ){
            if(diagCount < grid.getColumns()){
                i = diagCount;
                j = 0;
            }
            else{// todo figure out exacts
                i = grid.getColumns() - 1;
                j = (diagCount - grid.getColumns()) + 1;
            }
            while(j < grid.getRows() && i >= 0 && i < grid.getColumns() && j >= 0){
                if(grid.get(i, j).getAssigned() != null){
                    grid.get(i, j).setAssigned(regions.get(regIndex));
                    regIndex++;
                }
                i--;
                j++;
            }
            diagCount++;
            
        }
        
    }
    
    public class DataSort implements Comparator<Region>{
        public int compare(Region a, Region b){
            return Double.compare(a.getData(), b.getData());
        }
    }
}
