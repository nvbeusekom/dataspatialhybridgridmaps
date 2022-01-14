/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dataspatialhybridgrids;

import algorithms.GridGenerator;
import data.GeographicMap;
import data.Grid;
import io.IPEImport;
import nl.tue.geometrycore.gui.GUIUtil;

/**
 *
 * @author wmeulema
 */
public class Data {

    DrawPanel draw = new DrawPanel(this);
    SidePanel side = new SidePanel(this);

    GeographicMap map = null;
    Grid grid = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Data data = new Data();
        GUIUtil.makeMainFrame("Data spatial hybrid grid maps", data.draw, data.side);

    }

    public void load() {
        GeographicMap map = IPEImport.readIPE();
        if (map != null) {
            this.map = map;
            this.grid = null;
            draw.zoomToFit();
        }
    }

    public void createGrid(int cols, int rows) {
        if (map == null) return;
        
        grid = GridGenerator.generateSquareGrid(cols, rows, map.getBoundingBox());
        draw.repaint();
        
    }

}
