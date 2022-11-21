/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algorithms;

import algorithms.PatchMover.Pair;
import data.GeographicMap;
import data.Grid;
import com.quantego.clp.*;
import data.Region;
import data.Tile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author wmeulemans
 * From: https://github.com/tue-alga/Gridmap
 */

public class DataAssignment {
    Grid grid;
    GeographicMap geoMap;
    
    CLP model;

    List<CLPVariable> variables = new ArrayList();
    //all variables that correspond to the site
    HashMap<Region, List<CLPVariable>> siteVariables = new HashMap();
    //all variables that hold the coordinate
    HashMap<Tile, List<CLPVariable>> cellVariables = new HashMap();
    
    HashMap<Region,Tile> currentAssignment;

    //holds for each flow variable between a cell and a site, to which site it maps
    HashMap<CLPVariable, Region> siteMapping = new HashMap();
    //holds for each flow variable between a cell and a site, to which cite it maps
    HashMap<CLPVariable, Tile> cellMapping = new HashMap();
    
    //holds for each flow variable between a cell and a site, to which site it maps
    HashMap<CLPVariable, Region> siteMapping2 = new HashMap();
    //holds for each flow variable between a cell and a site, to which cite it maps
    HashMap<CLPVariable, Tile> cellMapping2 = new HashMap();
    
    // holds for each tile the variable to the tiles around it
    HashMap<Tile, List<CLPVariable>> adjacencyVariables = new HashMap();
    
    /**
     * Sets up a linear program for the given component.
     *
     * @param component
     */
    public DataAssignment(Grid grid, GeographicMap geoMap) {
        this.grid = grid;
        this.geoMap = geoMap;
        setupLP();
    }

    /**
     * Initializes all the variables
     */
    private void setupLP() {
        model = new CLP();
        createVariables(geoMap, grid);
        addSiteConstraint();
        addCellConstraint();
        addOptimization();
    }
    
    private void setupMovementLP(List<Tile> patch) {
        model = new CLP();
        variables = new ArrayList();
        siteVariables = new HashMap();
        cellVariables = new HashMap();
        siteMapping = new HashMap();
        cellMapping = new HashMap();
        
//        createMovementVariables(patch, grid);
        addSiteConstraint();
        addCellConstraint();
        addOptimization();
    }

    /**
     * Creates variables for the cells and sites.
     *
     * @param sites
     * @param gridCells
     */
    private void createVariables(List<Region> sites, Grid gridCells) {
        //Each site can be assigned to a cell in the grid.
        for (int i = 0; i < grid.getColumns(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                Tile t1 = grid.get(i,j);
                if(i < grid.getColumns()-1){
                    Tile t2 = grid.get(i+1, j);
                    for (Region r1 : sites) {
                        for (Region r2 : sites){
                            if(!r1.equals(r2)){
                                CLPVariable flow = model.addVariable();
                                //flow between 0 and 1
                                flow.bounds(0.0, 1.0);
                                //name it and add it
                                flow.name(r1.getLabel() + ";" + t1 + "-" + r2.getLabel() + ";" + t2);
                                variables.add(flow);

                                //keep track of it
                                List cVarList = cellVariables.getOrDefault(t1, new ArrayList());
                                cVarList.add(flow);
                                cellVariables.put(t1, cVarList);

                                List sVarList = siteVariables.getOrDefault(r1, new ArrayList());
                                sVarList.add(flow);
                                siteVariables.put(r1, sVarList);

                                siteMapping.put(flow, r1);
                                cellMapping.put(flow, t1);
                                siteMapping.put(flow, r2);
                                cellMapping.put(flow, t2);
                                
                            }
                        }
                    }
                    
                }
                if(j < grid.getRows()-1){
                    Tile t3 = grid.get(i, j+1);
                }
            }
            
        }
    }
    
    // TODO Do it so that the cells and region in the patch are not assigned, then do them by hand..... ------------------------------------------------------
    private void createMovementVariables(List<Pair> patch, Grid gridCells) {
        currentAssignment = new HashMap<>();
        //Each site can be assigned to a cell in the grid.
        for (Tile t : gridCells) {
            if(t.getAssigned() != null){
                for (Tile c : gridCells) {

                    CLPVariable flow = model.addVariable();
                    //flow between 0 and 1
                    flow.bounds(0.0, 1.0);
                    //name it and add it
                    flow.name(t.getLabel() + ";" + c);
                    variables.add(flow);

                    //keep track of it
                    List cVarList = cellVariables.getOrDefault(c, new ArrayList());
                    cVarList.add(flow);
                    cellVariables.put(c, cVarList);

                    List sVarList = siteVariables.getOrDefault(t.getAssigned(), new ArrayList());
                    sVarList.add(flow);
                    siteVariables.put(t.getAssigned(), sVarList);

                    siteMapping.put(flow, t.getAssigned());
                    cellMapping.put(flow, c);
                    currentAssignment.put(t.getAssigned(), c);
                }
            }
        }
    }

    /**
     * Creates the constraint that every site maps to exactly 1 cell.
     */
    private void addSiteConstraint() {
        //every site maps to a total (exactly) one cell.
        for (Collection<CLPVariable> variableList : siteVariables.values()) {
            HashMap<CLPVariable, Double> lhs = new HashMap<>();
            for (CLPVariable v : variableList) {
                lhs.put(v, 1.0);
            }
            model.addConstraint(lhs, CLPConstraint.TYPE.EQ, 1);
        }
    }

    /**
     * Creates the constraint that every cell has at most one site mapped to it.
     */
    private void addCellConstraint() {
        //every cell has at most one site mapped to it.
        for (Collection<CLPVariable> variableList : cellVariables.values()) {
            HashMap<CLPVariable, Double> lhs = new HashMap<>();
            for (CLPVariable v : variableList) {
                lhs.put(v, 1.0);
            }
            model.addConstraint(lhs, CLPConstraint.TYPE.LEQ, 1);
        }
    }
    
    public void adjacencyConstraints() {
         // For each edge pair var t1r1 t2r2 make sure that t2r2 is the same in the 
    }

    /**
     * Adds the optimization term: Minimize sum of squared distances.
     */
    private void addOptimization() {
        //cost for a site s to be assigned to cell c is the squared squaredDistance between their centroids.
        HashMap<CLPVariable, Double> objective = new HashMap<>();
        for (CLPVariable v : variables) {
            Region site = getSite(v);
            Tile c = getCell(v);

            //cell can be a square or hexagon, need to calculate the squaredDistance
            double squaredDistance = c.getCenter().squaredDistanceTo(site.getPos());

            objective.put(v, squaredDistance);
        }

        //minimize the sum of squared distances
        model.addObjective(objective, 0.0);
    }
    
    private void addOptimization1() {
        //cost for a site s to be assigned to cell c is the squared squaredDistance between their centroids.
        HashMap<CLPVariable, Double> objective = new HashMap<>();
        for (CLPVariable v : variables) {
            Region site = getSite(v);
            Tile c = getCell(v);

            //cell can be a square or hexagon, need to calculate the squaredDistance
            double squaredDistance = c.getCenter().squaredDistanceTo(site.getPos());

            objective.put(v, squaredDistance);
        }

        //minimize the sum of squared distances
        model.addObjective(objective, 0.0);
    }
    

    /**
     * Solve the linear program and return the cost.
     *
     * @return
     */
    public double solveLP() {
        model.minimize();
        for (CLPVariable v : variables) {
            if (model.getSolution(v) == 1) {
                //there is a mapping from the site to the cell.
                Region site = getSite(v);
                Tile cell = getCell(v);
                cell.setAssigned(site);
                site.setAssigned(cell);
                
//                cell.color = site.color;
//                cell.province = site.province;

            } else if (model.getSolution(v) > 0) {
                System.out.println("Non-integer solutions");
            }
        }
        return model.getObjectiveValue();
    }

    /**
     * Returns the site associated to a given variable,
     *
     * @param v
     * @return
     */
    private Region getSite(CLPVariable v) {
        return siteMapping.get(v);
    }

    /**
     * Returns the cell associated to a given variable.
     *
     * @param v
     * @return
     */
    private Tile getCell(CLPVariable v) {
        return cellMapping.get(v);
    }
}
