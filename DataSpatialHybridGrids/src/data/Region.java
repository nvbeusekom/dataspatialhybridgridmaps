/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.awt.Color;
import java.util.ArrayList;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;

/**
 *
 * @author wmeulema
 */
public class Region {

    String label;
    Vector pos = null;
    ArrayList<Polygon> shapes = new ArrayList();
    Tile assigned = null;
    int initRow;
    int initCol;
    double data;
    Color spatialColor = null;
    GeographicMap map = null;
    Region parent = null;
    
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
        if(pos == null && shapes.size() > 0){
            Polygon largest = null;
            for(Polygon p : this.shapes){
                if(largest == null || p.areaUnsigned() > largest.areaUnsigned()){
                    largest = p;
                }
            }
            this.pos = largest.centroid();
        }
        return pos;
    }

    public void setPos(Vector pos) {
        this.pos = pos;
    }

    public ArrayList<Polygon> getShape() {
        return shapes;
    }

    public void setShape(Polygon shape) {
        this.shapes.add(shape);
    }
    
    public void removeLastShape() {
        this.shapes.remove(this.shapes.size()-1);
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
    public void addRegion(Region r){
        if(map == null){
            map = new GeographicMap();
        }
        r.setParent(this);
        map.add(r);
    }
    public GeographicMap getLocalMap(){
        return this.map;
    
    }

    public Region getParent() {
        return parent;
    }

    public void setParent(Region parent) {
        this.parent = parent;
    }
    
    public Region clone(){
        Region c = new Region();
        c.setAssigned(assigned);
        c.setData(data);
        c.setInitCol(initCol);
        c.setInitRow(initRow);
        c.setPos(pos.clone());
        for(Polygon p : shapes){
            c.setShape(p.clone());
        }
        c.setSpatialColor(spatialColor);
        return c;
    }

}
