/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import java.util.ArrayList;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author wmeulema
 */
public class GeographicMap extends ArrayList<Region> {

    public Rectangle getBoundingBox() {
        Rectangle r = new Rectangle();
        for (Region reg : this) {
            r.includeGeometry(reg.getShape());
        }
        return r;
    }

}
