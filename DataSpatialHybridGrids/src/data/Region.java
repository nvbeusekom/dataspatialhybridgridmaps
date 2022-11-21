/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.awt.Color;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;

/**
 *
 * @author wmeulema
 */
public class Region {

    String label;
    Vector pos;
    Polygon shape;
    Tile assigned = null;
    int initRow;
    int initCol;
    double data;
    Color spatialColor = null;
    
    public Tile getAssigned() {
        return assigned;
    }

    public void setAssigned(Tile assigned) {
        this.assigned = assigned;
    }
    
    

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Vector getPos() {
        if(shape != null){
            return shape.centroid();
        }
        return pos;
    }

    public void setPos(Vector pos) {
        this.pos = pos;
    }

    public Polygon getShape() {
        return shape;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
    }
    
    public void setData(double data){
        this.data = data;
    }
    
    public double getData(){
        return data;
    }

    public int getInitRow() {
        return initRow;
    }

    public int getInitCol() {
        return initCol;
    }

    public void setInitRow(int initRow) {
        this.initRow = initRow;
    }

    public void setInitCol(int initCol) {
        this.initCol = initCol;
    }

    public void setSpatialColor(Color spatialColor) {
        this.spatialColor = spatialColor;
    }

    public Color getSpatialColor() {
        return spatialColor;
    }

}
