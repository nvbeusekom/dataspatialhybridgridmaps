/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import data.GeographicMap;
import data.Region;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.geometryrendering.styling.ExtendedColors;
import org.json.*;

/**
 *
 * @author 20184261
 */
public class GeoJSONReader {
    public static GeographicMap loadGeoJSON(){
        return loadGeoJSON(null);
    }
    public static GeographicMap loadGeoJSON(GeographicMap upperMap){
        File file = null;
        if(upperMap == null){
            file = new File("..\\data\\region.geojson");
        }
        else{
            file = new File("..\\data\\LALowerTier.geojson");
        }
        String content = readFile(file);
        JSONObject obj = new JSONObject(content);
        JSONArray regions = obj.getJSONArray("features");
        GeographicMap map = new GeographicMap();
        // For every region
        for (int i = 0; i < regions.length(); i++) {
            JSONObject regObj = regions.getJSONObject(i);
            Region r = new Region();
            JSONArray coords = regObj.getJSONObject("geometry").getJSONArray("coordinates");
            Polygon p = new Polygon();
            if(regObj.getJSONObject("geometry").getString("type").equals("MultiPolygon")){
                // For every polygon in the MultiPolygon
                for (int j = 0; j < coords.length(); j++) {
                    Polygon check = new Polygon();
                    JSONArray polyArray = coords.getJSONArray(j).getJSONArray(0);
                    // For every point in the polygon
                    for (int k = 0; k < polyArray.length(); k++) {
                        check.addVertex(new Vector(polyArray.getJSONArray(k).getDouble(0),polyArray.getJSONArray(k).getDouble(1)));
                    }
                    r.setShape(check);
                }
            }
            else if(regObj.getJSONObject("geometry").getString("type").equals("Polygon")){
                // For every point in the polygon
                coords = coords.getJSONArray(0);
                for (int k = 0; k < coords.length(); k++) {
                    p.addVertex(new Vector(coords.getJSONArray(k).getDouble(0),coords.getJSONArray(k).getDouble(1)));
                }
                r.setShape(p);
            }
            else{
                System.out.println("ERROR: SOME OTHER GEOMETRY APPEARED");
            }
            r.setPos(p.centroid());
            r.setLabel(regObj.getJSONObject("properties").getString("label"));
            if(upperMap!=null){
                addRegion(r,upperMap);
            }
            map.add(r);
        }
        return map;
    }
    
    public static void addRegion(Region r, GeographicMap upperMap){
        outer:
        for(Region check : upperMap){
            for(Polygon p : check.getShape()){
                if(p.contains(r.getPos())){
                    check.addRegion(r);
                   return;
                }
            }
        }
        System.out.println("Could not find any region that contains centroid of " + r.getLabel() + " finding closest region...");
        Region closest = null;
        double closestDist = Double.MAX_VALUE;
        for(Region check : upperMap){
            for(Polygon p : check.getShape()){
                if(p.distanceTo(r.getPos()) < closestDist){
                    closestDist = p.distanceTo(r.getPos());
                    closest = check;
                }
            }
        }
        closest.addRegion(r);
    }
    
    public static String readFile(File file){
        String res = "";
        try{
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                    res += sc.nextLine();
            }
        } catch(IOException e){
            System.out.println("Can't read file: " + file.toString());
        }
        return res;
    }
}
