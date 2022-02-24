/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;

/**
 *
 * @author wmeulema
 */
public class Tile {
    Polygon shape;
    Vector center;
    Region assigned = null;
    double currentMI;

    public Tile(Polygon shape, Vector center) {
        this.shape = shape;
        this.center = center;
    }
    
    public Tile(Polygon shape, Vector center, Region assigned) {
        this.shape = shape;
        this.center = center;
        this.assigned = assigned;
    }

    
    
    public Region getAssigned() {
        return assigned;
    }

    public void setAssigned(Region assigned) {
        this.assigned = assigned;
    }

    public Polygon getShape() {
        return shape;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
    }

    public Vector getCenter() {
        return center;
    }

    public void setCenter(Vector center) {
        this.center = center;
    }
    
    public String getLabel() {
        if(assigned == null){
            return "";
        }
        return assigned.getLabel();
    }
    
    public double getData(){
        if(assigned == null){
            return -1;
        }
        return assigned.getData();
    }

    public double getCurrentMI() {
        return currentMI;
    }

    public void setCurrentMI(double currentMI) {
        this.currentMI = currentMI;
    }
    
    public double getLength(){
        return this.getShape().edge(0).length();
    }
    
    @Override 
    public Tile clone(){
        return new Tile(this.shape.clone(),this.center.clone(), this.assigned);
    }
    
    public boolean equals(Tile t){
        return this.getCenter().isApproximately(t.getCenter());
    }

}
