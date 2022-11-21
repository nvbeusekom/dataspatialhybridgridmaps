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

/**
 *
 * @author 20184261
 * From: https://github.com/tue-alga/Gridmap
 */
public class ColorGenerator {
    public ColorGenerator(GeographicMap map, String s) {

        //get bounding box
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Region r : map) {
            minX = Math.min(minX, r.getPos().getX());
            maxX = Math.max(maxX, r.getPos().getX());
            minY = Math.min(minY, r.getPos().getY());
            maxY = Math.max(maxY, r.getPos().getY());
        }

        //get color ranges
        //start at hsl color and change luminance value
        double xRange = maxX - minX;
        double yRange = maxY - minY;

        //for each site, get the color. Different color ranges for different maps.
        for (Region r : map) {
            double xPercentage = (r.getPos().getX() - minX) / xRange;
            double yPercentage = (r.getPos().getY() - minY) / yRange;
            Color c = null;
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

    public Color getUKColor(double xPercentage, double yPercentage) {

        //hue vertical, brightness horizontal
        float huePercentage = (float) yPercentage;
        float brightnessPercentage = (float) xPercentage;

        float h = (0f + (360f - 0f) * huePercentage) / 360f;
        float s = 0.7f;
        float b = 1f - brightnessPercentage * 0.8f;

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
        double middleY = 451.0 / 666.0;

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
        float s = 0.7f;
        float b = 1f - distancePercentage * 0.8f;

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
