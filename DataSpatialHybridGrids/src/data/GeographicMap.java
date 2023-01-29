/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.util.ArrayList;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author wmeulema
 */
public class GeographicMap extends ArrayList<Region> {

    double maxData = 0;
    
    public Rectangle getBoundingBox() {
        Rectangle r = new Rectangle();
        for (Region reg : this) {
            if(reg.getShape()!= null)
                r.includeGeometry(reg.getShape());
        }
        return r;
    }
    
    public double getMaxData(){
        if(maxData == 0){
            double max = Double.MIN_VALUE;
            if(this.get(0).getLocalMap() == null){
                for(Region r : this){
                    if(r.getData() > max){
                        max = r.getData();
                    }
                }
            }
            else{
                for(Region r : this){
                    for(Region r2: r.getLocalMap()){
                        if(r2.getData() > max){
                            max = r2.getData();
                        }
                    }
                }
            }
            maxData = max;
        }
        return maxData;
    }

    public Vector centroid(){
        return null;
    }
    
}
