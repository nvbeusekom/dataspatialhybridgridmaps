/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algorithms;

import data.GeographicMap;
import data.Grid;
import com.quantego.clp.*;
import data.Region;
import data.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author wmeulemans
 * From: https://github.com/tue-alga/Gridmap
 */

public class SpatialAssignment {
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
    
    /**
     * Sets up a linear program for the given component.
     *
     * @param grid
     * @param geoMap
     */
    public SpatialAssignment(Grid grid, GeographicMap geoMap) {
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

    /**
     * Creates variables for the cells and sites.
     *
     * @param sites
     * @param gridCells
     */
    private void createVariables(List<Region> sites, Grid gridCells) {
        //Each site can be assigned to a cell in the grid.
        for (Region s : sites) {
            for (Tile c : gridCells) {

                CLPVariable flow = model.addVariable();
                //flow between 0 and 1
                flow.bounds(0.0, 1.0);
                //name it and add it
                flow.name(s.getLabel() + ";" + c);
                variables.add(flow);

                //keep track of it
                List cVarList = cellVariables.getOrDefault(c, new ArrayList());
                cVarList.add(flow);
                cellVariables.put(c, cVarList);

                List sVarList = siteVariables.getOrDefault(s, new ArrayList());
                sVarList.add(flow);
                siteVariables.put(s, sVarList);

                siteMapping.put(flow, s);
                cellMapping.put(flow, c);
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
    /**
     * Solve the linear program and return the cost.
     *
     * @return
     */
    public double solveLP() {
        model.minimize();
        double epsilon = 0.001;
        for (CLPVariable v : variables) {
            if (Math.abs(model.getSolution(v) - 1) < epsilon) {
                //there is a mapping from the site to the cell.
                Region site = getSite(v);
                Tile cell = getCell(v);
                cell.setAssigned(site);
                site.setAssigned(cell);
                
//                cell.color = site.color;
//                cell.province = site.province;

            } else if (model.getSolution(v) > epsilon) {
                System.out.println("Non-integer solutions:");
                System.out.println(model.getSolution(v));
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
