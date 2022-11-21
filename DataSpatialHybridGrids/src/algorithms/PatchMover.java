/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import data.Grid;
import data.Tile;

/**
 *
 * @author 20184261
 */
public class PatchMover {
    Grid grid;

    public PatchMover(Grid grid) {
        this.grid = grid;
    }
    
    
    public void movePatch(Pair[] init, Pair[] target){
        // Add to spatial assignment
        // Set some of the solution variables
        System.out.println("not implemented");
    }
    
    public class Pair {
        int x;
        int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
    }
}
