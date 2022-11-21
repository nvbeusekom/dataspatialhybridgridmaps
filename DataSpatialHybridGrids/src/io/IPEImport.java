/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

import data.GeographicMap;
import data.Region;
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

    public static GeographicMap readIPE() {
        int result = choose.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                IPEReader read = IPEReader.fileReader(choose.getSelectedFile());
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

        }
        return null;

    }
    
    public static boolean findShape(Region r, BaseGeometry geom){
        boolean found = false;
//        System.out.println(geom.getGeometryType());
        if (geom.getGeometryType() == GeometryType.POLYGON) {
            Polygon p = (Polygon) geom;
            if (p.contains(r.getPos())) {
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
            findShape(r,p);
        }
        else if(geom.getGeometryType() == GeometryType.GEOMETRYGROUP){
//            System.out.println("In group: ------------------------");
            for(BaseGeometry geom2 : ((GeometryGroup<? extends BaseGeometry>)geom).getParts()){
//                System.out.println(geom2.getGeometryType());
                if(geom2.getGeometryType() == GeometryType.GEOMETRYGROUP){
                    if(findShape(r,geom2)){
                        return true;
                    }
                }
                else if(geom2.getGeometryType() == GeometryType.GEOMETRYCYCLE){
                    if(findShape(r,geom2)){
                        return true;
                    }
                }
                else if(geom2.getGeometryType() == GeometryType.POLYGON){
                    if(findShape(r,geom2)){
                        return true;
                    }
                }
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
            return findShape(r,p);
        }
        return found;
        
    }
        
}
