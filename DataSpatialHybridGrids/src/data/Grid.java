/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data;

import nl.tue.geometrycore.datastructures.list2d.List2D;

/**
 *
 * @author wmeulema
 */
public class Grid extends List2D<Tile> {

    public Grid() {
    }

    public Grid(int columns, int rows) {
        super(columns, rows);
    }
    
    
}
