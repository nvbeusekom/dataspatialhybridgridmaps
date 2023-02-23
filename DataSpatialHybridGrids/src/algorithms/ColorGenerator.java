/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import data.GeographicMap;
import data.Region;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author 20184261
 * From: https://github.com/tue-alga/Gridmap
 */
public class ColorGenerator {
    Color[] setColors = {   
//                            new Color(166,206,227),
                            new Color(109, 163, 191),
                            new Color(31,120,180),
//                            new Color(178,223,138),
                            new Color(151, 201, 107),
                            new Color(51,160,44),
                            new Color(251,154,153),
                            new Color(227,26,28),
                            new Color(253,191,111),
                            new Color(255,127,0),
//                            new Color(202,178,214),
                            new Color(158, 82, 151),
                            new Color(106,61,154),
                            new Color(255,255,153),
                            new Color(177,89,40)
                        };
    
    public ColorGenerator(GeographicMap map, String s) {

        //get bounding box
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        
        ArrayList<Double> xCoords = new ArrayList<>();
        ArrayList<Double> yCoords = new ArrayList<>();
        
        for (Region r : map) {
            minX = Math.min(minX, r.getPos().getX());
            maxX = Math.max(maxX, r.getPos().getX());
            minY = Math.min(minY, r.getPos().getY());
            maxY = Math.max(maxY, r.getPos().getY());
            xCoords.add(r.getPos().getX());
            yCoords.add(r.getPos().getY());
        }

        //get color ranges
        //start at hsl color and change luminance value
        double xRange = maxX - minX;
        double yRange = maxY - minY;

        //for each site, get the color. Different color ranges for different maps.
        float hueIndex = 0;
        for (Region r : map) {
            double xPercentage;
            double yPercentage;
            Color c = null;
            
            if(r.getLocalMap()!=null){
                xPercentage = getIndex(r.getPos().getX(),xCoords) / xCoords.size();
                yPercentage = getIndex(r.getPos().getY(),yCoords) / yCoords.size();
                float hue = hueIndex/(float)map.size();
                r.setSpatialColor(Color.getHSBColor(hue, 0.7f, 0.9f));
                colorLowerRegions(r,hue);
                hueIndex++;
            }
            else{
                xPercentage = (r.getPos().getX() - minX) / xRange;
                yPercentage = (r.getPos().getY() - minY) / yRange;
                if (s == "NL") {
                    c = getNlColor(xPercentage, yPercentage);
                }
                if (s == "UK") {
                    c = getUKColor(xPercentage, yPercentage);
                }
                if (s == "US") {
                    c = getUSAColor(xPercentage, yPercentage);
                }
                r.setSpatialColor(c);
            }
            
            
        }
    }
    
    
    
    public double getIndex(double d, ArrayList<Double> list){
        for (int i = 0; i < list.size(); i++) {
            if(d == list.get(i))
                return (double)i;
        }
        return -1;
    }
    
    public void colorLowerRegions(Region r, float hue){
        float hueRange = 90f / 360f;
        float bRange = 90f / 360f;
        double lowMinX = Double.MAX_VALUE;
        double lowMaxX = Double.MIN_VALUE;
        double lowMinY = Double.MAX_VALUE;
        double lowMaxY = Double.MIN_VALUE;
        for (Region r2 : r.getLocalMap()) {
            lowMinX = Math.min(lowMinX, r2.getPos().getX());
            lowMaxX = Math.max(lowMaxX, r2.getPos().getX());
            lowMinY = Math.min(lowMinY, r2.getPos().getY());
            lowMaxY = Math.max(lowMaxY, r2.getPos().getY());
        }
        double lowxRange = lowMaxX - lowMinX;
        double lowyRange = lowMaxY - lowMinY;
//        float [] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        
//        if(c.getRed() > 100 && c.getGreen() > 100 && c.getBlue() > 100){
//            hsb[2] = hsb[2] * 0.5f;
//            hsb[1] = Math.min(hsb[1] * 1.5f,1f);
//        }
        
        for(Region r2 : r.getLocalMap()){
            float xPercentage = (float) ((r2.getPos().getX() - lowMinX) / lowxRange);
            float yPercentage = (float) ((r2.getPos().getY() - lowMinY) / lowyRange);
            
//            float h = (xPercentage - 0.5f)*2f*hueRange;
            float h = hue;
//            h += hsb[0];
            // Keep it between 0 and 1 but circular
            h %= 1f;
            
//            float s = hsb[1];
            float s = 0.7f + (xPercentage - 0.5f)*2f*hueRange;
//            s += hsb[1];
            // Keep it between 0 and 1 but circular
            s = Math.min(1,Math.max(s, 0));
            
            
            float b = 0.7f + (yPercentage - 0.25f)*2f*bRange;
//            b += hsb[2];
            // Keep it between 0 and 1
            b = Math.min(1,Math.max(b, 0));
            
            Color lowerColor = Color.getHSBColor(h, s, b);
            
            r2.setSpatialColor(lowerColor);
        }
    }
    
    public Color getUKColor(double xPercentage, double yPercentage) {

        //hue vertical, brightness horizontal
        float huePercentage = (float) yPercentage;
        float brightnessPercentage = (float) xPercentage;

        float h = (1f*huePercentage + 0f) % 1f;
        float s = 0.7f;
        float b = 1f - brightnessPercentage * 0.7f;

        Color c = Color.getHSBColor(h, s, b);
        return c;
    }
    
    public Color getUSAColor(double xPercentage, double yPercentage) {
        //hue horizontal, brightness vertical
        float huePercentage = (float) xPercentage;
        float brightnessPercentage = (float) yPercentage;

        float h = (0f + (360f - 0f) * huePercentage) / 360f;
        float s = 0.7f;
        float b = 1f - brightnessPercentage * 0.8f;

        Color c = Color.getHSBColor(h, s, b);
        return c;
    }

    public Color getNlColor(double xPercentage, double yPercentage) {
        //point in the ijsselmeer in the netherlands
        double middleX = 315.0 / 573.0;
//        double middleX = 226.0 / 374.0;
        double middleY = 451.0 / 666.0;
//        double middleY = 221.0 / 371.0;

        double angle = getAngle(middleX, middleY, xPercentage, yPercentage);
        angle = (angle + 90 + 360.0) % 360.0;//rotate so opening is at the top
        double distance = getDistance(middleX, middleY, xPercentage, yPercentage);

        double maxDistance = getDistance(middleX, middleY, 1, 1);
        maxDistance = Math.max(maxDistance, getDistance(middleX, middleY, 0, 0));
        maxDistance = Math.max(maxDistance, getDistance(middleX, middleY, 0, 1));
        maxDistance = Math.max(maxDistance, getDistance(middleX, middleY, 1, 0));

        float anglePercentage = (float) angle / 360f;
        float distancePercentage = (float) (distance / maxDistance);

        float h = (0f + (360f - 0f) * anglePercentage) / 360f;
//        float s = 0.7f;
        float s = 0.4f + distancePercentage * 0.5f;;
//        float b = 1f - distancePercentage * 0.8f;
        float b = Math.min(1,0.5f + distancePercentage * 0.6f);

        Color c = Color.getHSBColor(h, s, b);
        return c;
    }

    public double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public float getAngle(double x1, double y1, double x2, double y2) {
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    
    /**
     * Prints a 100 by 100 grid in the color of the gradient to be printed.
     * Used for debugging/color calibration.
     */
    private void printGrid() {
        for (double x = 0; x < 100; x++) {
            for (double y = 0; y < 100; y++) {
                Color c = getUSAColor(x / 100.0, y / 100.0);
                double red = ((double) c.getRed()) / 255.0;
                double green = ((double) c.getGreen()) / 255.0;
                double blue = ((double) c.getBlue()) / 255.0;

                System.out.println("<path fill=\"" + red + " " + green + " " + blue + "\">\n"
                                   + "" + x + " " + y + " m\n"
                                   + "" + (x + 1) + " " + y + " l\n"
                                   + "" + (x + 1) + " " + (y + 1) + " l\n"
                                   + "" + x + " " + (y + 1) + " l\n"
                                   + "h\n"
                                   + "</path>");
            }
        }
    }
}
