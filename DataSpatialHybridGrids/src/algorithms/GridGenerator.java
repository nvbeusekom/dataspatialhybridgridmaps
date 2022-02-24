/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algorithms;

import data.Grid;
import data.Tile;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author wmeulema
 */
public class GridGenerator {

    public static Grid generateSquareGrid(int cols, int rows, Rectangle rect) {

        Grid grid = new Grid(cols, rows);
        rect = rect.clone();
        rect.growToAspectRatio((double)cols/(double)rows);
        Rectangle cell = rect.clone();
        
        cell.scale(1.0/cols, 1.0/rows, rect.leftTop());
        
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                
                Rectangle p = cell.clone();
                p.translate(c * cell.width(), -r * cell.height());
                
                Tile t = new Tile(p.toPolygon(),p.center());
                grid.set(c, r, t);
                
                
            }
        }

        return grid;
    }
}
