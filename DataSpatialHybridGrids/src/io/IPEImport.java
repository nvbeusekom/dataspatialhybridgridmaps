/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

import data.GeographicMap;
import data.Region;
import static io.GeoJSONReader.addRegion;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import nl.tue.geometrycore.geometry.BaseGeometry;
import nl.tue.geometrycore.geometry.GeometryType;
import nl.tue.geometrycore.geometry.OrientedGeometry;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.curved.BezierCurve;
import nl.tue.geometrycore.geometry.linear.LineSegment;
import nl.tue.geometrycore.geometry.linear.PolyLine;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.geometry.mix.GeometryCycle;
import nl.tue.geometrycore.geometry.mix.GeometryGroup;
import nl.tue.geometrycore.io.ReadItem;
import nl.tue.geometrycore.io.ipe.IPEReader;

/**
 *
 * @author wmeulema
 */
public class IPEImport {

    static JFileChooser choose = new JFileChooser("../data");

    public static GeographicMap readIPE(boolean loadFrance,boolean loadHierarchical) {
        return readIPE(null,loadFrance,loadHierarchical);
    }
    
    public static GeographicMap readIPE(GeographicMap upperMap,boolean loadFrance,boolean loadHierarchical) {
        File file = null;
        if(upperMap == null && loadHierarchical){
            file = new File("..\\data\\provinces_labeled.ipe");
        }
        else if(loadFrance){
            file = new File("..\\data\\france_departements.ipe");
        }
        else{
            file = new File("..\\data\\gem-2017-simplified.ipe");
        }
        try {
            IPEReader read = IPEReader.fileReader(file);
            read.setBezierSampling(2);
            List<ReadItem> items = read.read();

            GeographicMap map = new GeographicMap();
            int count = 0;
            for (ReadItem it : items) {
                if (it.getString() != null) {
                    Region r = new Region();
                    r.setLabel(it.getString());
//                        System.out.println("Region " + count);
                    count++;
                    r.setPos((Vector) it.getGeometry());
                    map.add(r);
                    if(upperMap!=null){
                        addRegion(r,upperMap);
                    }
                    for(ReadItem it2 : items){
                        if(findShape(r, it2.getGeometry())){
//                                System.out.println("Found shape :)");
                            break;
                        }
                    }
                }
            }
//                for(Region r : map){
//                    if(r != null && r.getShape() == null){
//                        System.out.println(r.getLabel());
//                    }
//                }

            return map;

        } catch (IOException ex) {
            Logger.getLogger(IPEImport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }
    
    public static boolean findShape(Region r, BaseGeometry geom){
        return findShape(r,geom,false);
    }
    
    public static boolean findShape(Region r, BaseGeometry geom, boolean force){
//        System.out.println(geom.getGeometryType());
        if (geom.getGeometryType() == GeometryType.POLYGON) {
            Polygon p = (Polygon) geom;
            if (p.contains(r.getPos()) || force) {
                r.setShape(p);
                return true;
            }
            else{
                return false;
            }
        }
        if(geom.getGeometryType() == GeometryType.POLYLINE){
            Polygon p = new Polygon();
            for(Vector v : ((PolyLine) geom).vertices()){
                p.addVertex(v);
            }
            return findShape(r,p,force);
        }
        else if(geom.getGeometryType() == GeometryType.GEOMETRYGROUP){
            boolean inGroup = false;
            for(BaseGeometry geom2 : ((GeometryGroup<? extends BaseGeometry>)geom).getParts()){

                if(findShape(r,geom2)){
                    inGroup = true;
                }
            }
            // Add all other shapes in the group except the one already added
            if(inGroup){
                r.removeLastShape();
                for(BaseGeometry geom2 : ((GeometryGroup<? extends BaseGeometry>)geom).getParts()){
                    findShape(r,geom2,true);
                }
                return true;
            }
        }
        else if(geom.getGeometryType() == GeometryType.GEOMETRYCYCLE){
            Polygon p = new Polygon();
//            System.out.println("In cycle: ------------------------");
            for(BaseGeometry oriented : ((GeometryCycle<? extends BaseGeometry>)geom).edges()){
//                System.out.println(oriented.getGeometryType());
                if(oriented.getGeometryType() == GeometryType.BEZIERCURVE){
                    BezierCurve bc = (BezierCurve) oriented;
                    p.addVertex(bc.getStart());
                    p.addVertex(bc.getPointAt(0.25));
                    p.addVertex(bc.getPointAt(0.5));
                    p.addVertex(bc.getPointAt(0.75));
                    p.addVertex(bc.getEnd());
                    
                }
                if(oriented.getGeometryType() == GeometryType.LINESEGMENT){
                    LineSegment ls = (LineSegment) oriented;
                    p.addVertex(ls.getStart());
                    p.addVertex(ls.getEnd());
                    
                }
                if(oriented.getGeometryType() == GeometryType.POLYLINE){
                    for(Vector v : ((PolyLine) oriented).vertices()){
                        p.addVertex(v);
                    }
                }
            }
            return findShape(r,p,force);
        }
        return false;
        
    }
        
}
