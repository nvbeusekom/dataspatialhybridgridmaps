/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author wmeulema
 */
public class Tile {
    Rectangle shape;
    Vector center;
    Region assigned = null;
    double currentMI;

    public Tile(Rectangle shape, Vector center) {
        this.shape = shape;
        this.center = center;
    }
    
    public Tile(Rectangle shape, Vector center, Region assigned) {
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

    public Rectangle getShape() {
        return shape;
    }

    public void setShape(Rectangle shape) {
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
        return assigned.getData();
    }

    public double getCurrentMI() {
        return currentMI;
    }

    public void setCurrentMI(double currentMI) {
        this.currentMI = currentMI;
    }
    
    public double getLength(){
        return this.getShape().width();
    }
    
    public boolean isAdjacent(Tile t2){
        if(this == t2){
            return false;
        }
        return this.getCenter().distanceTo(t2.getCenter()) < this.getLength() + 0.001;
    }
    
    @Override 
    public Tile clone(){
        return new Tile(this.shape.clone(),this.center.clone(), this.assigned);
    }
    
    public boolean equals(Tile t){
        return this.getCenter().isApproximately(t.getCenter());
    }

}
