/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import data.Region;
import data.Tile;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.curved.BezierCurve;
import nl.tue.geometrycore.geometry.linear.LineSegment;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.geometryrendering.GeometryPanel;
import nl.tue.geometrycore.geometryrendering.styling.Dashing;
import nl.tue.geometrycore.geometryrendering.styling.Hashures;
import nl.tue.geometrycore.geometryrendering.styling.ExtendedColors;
import nl.tue.geometrycore.geometryrendering.styling.SizeMode;
import nl.tue.geometrycore.geometryrendering.styling.TextAnchor;

/**
 *
 * @author wmeulema
 */
public class DrawPanel extends GeometryPanel {

    private final Data data;

    DrawPanel(Data data) {
        this.data = data;
    }
    boolean dataColored = true;
    boolean drawExtras = true;
    boolean drawLabels = false;
    
    @Override
    protected void drawScene() {
        if (data.map == null) {
            return;
        }

        setSizeMode(SizeMode.VIEW);

        setStroke(Color.black, 1, Dashing.SOLID);
        setTextStyle(TextAnchor.CENTER, 10);
        for (Region reg : data.map) {
            if(data.grid == null)
                setFill(reg.getSpatialColor(),Hashures.SOLID);
            draw(reg.getShape());
            if(!data.gridlabels && drawLabels)
                draw(reg.getPos(), reg.getLabel());
        }
        if (data.grid != null) {
            setStroke(ExtendedColors.darkBlue, 1, Dashing.SOLID);
            int count = 0;
            for (Tile tile : data.grid) {
                if(tile.getAssigned()==null) {
                    setStroke(ExtendedColors.darkBlue, 1, Dashing.SOLID);
                    setFill(null,Hashures.SOLID);
                }
                else {
                    double d = tile.getData()/data.map.getMaxData();
                    Color c;
                    if(dataColored)
                        c = ExtendedColors.fromUnitRGB(1-d, 0.1,d);
                    else
                        c = tile.getAssigned().getSpatialColor();
                    setStroke(c, 1, Dashing.SOLID);
                    setFill(c,Hashures.SOLID);
                    
                }
                
                count++;
                
                draw(tile.getShape());
                if(data.gridlabels && drawLabels){
                    setStroke(ExtendedColors.black,1,Dashing.SOLID);
                    draw(tile.getCenter(), tile.getLabel());
                }
                setFill(null,Hashures.SOLID);
            }
            setStroke(ExtendedColors.lightGray,1,Dashing.SOLID);
            if(drawExtras){
                for (Tile tile : data.grid){
                    if(tile.getAssigned() != null){
                        if(dataColored){
                            Vector ref = tile.getAssigned().getPos().clone();
                            Vector cp = Vector.divide(Vector.add(Vector.multiply(3, tile.getCenter().clone()), ref),4);
                            cp.rotate(Math.PI/2, tile.getCenter().clone());
                            BezierCurve bc = new BezierCurve(tile.getCenter(),cp,tile.getAssigned().getPos());
                            draw(bc);
                        }
                        else{
                            double d = tile.getData()/data.map.getMaxData();
                            double size = tile.getLength()*d;
                            Rectangle r = Rectangle.byCenterAndSize(tile.getCenter(),size,size);
                            draw(r);
                        }
                    }
                }
            }
        }

    }

    @Override
    public Rectangle getBoundingRectangle() {
        if (data.map == null) {
            return null;
        }
        return data.map.getBoundingBox();

    }

    @Override
    protected void mousePress(Vector loc, int button, boolean ctrl, boolean shift, boolean alt) {
        if (button == MouseEvent.BUTTON1) {
            data.computeProminence(loc);
        }
    }

    @Override
    protected void keyPress(int keycode, boolean ctrl, boolean shift, boolean alt) {
        switch (keycode) {
            case KeyEvent.VK_O:
                data.loadMap();
                break;
            case KeyEvent.VK_D:
                data.loadData();
                break;
        }
    }

}
