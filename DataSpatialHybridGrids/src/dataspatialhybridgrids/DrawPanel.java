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
import nl.tue.geometrycore.geometry.curved.Circle;
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
    boolean dataColored = false;
    boolean drawExtras = true;
    boolean drawLabels = false;
    boolean drawTears = true;
    double strokeSize = 1;
    
    @Override
    protected void drawScene() {
        if (data.map == null) {
            return;
        }
        Color beige = ExtendedColors.fromUnitRGB(0.96, 0.91, 0.84);
        this.setBackground(beige);
        setSizeMode(SizeMode.VIEW);

        setStroke(Color.black, strokeSize, Dashing.SOLID);
        setTextStyle(TextAnchor.CENTER, 10);
        for (Region reg : data.map) {
            if(data.grid == null)
                setFill(reg.getSpatialColor(),Hashures.SOLID);
            //draw(reg.getShape());
            if(!data.gridlabels && drawLabels)
                draw(reg.getPos(), reg.getLabel());
        }
        if (data.grid != null) {
            double diagonalDistance = data.grid.get(0, 0).getCenter().distanceTo(data.grid.get(data.grid.getColumns()-1, data.grid.getRows()-1).getCenter());
            
            setStroke(ExtendedColors.darkBlue, 1, Dashing.SOLID);
            int count = 0;
            for (Tile tile : data.grid) {
                if(tile.getAssigned()==null) {
                    setStroke(ExtendedColors.darkBlue, 1, Dashing.SOLID);
                    setFill(null,Hashures.SOLID);
                }
                else {
                    Color c;
                    if(dataColored){
                        double d = tile.getData()/data.map.getMaxData();
//                        System.out.println(d);
                        c = ExtendedColors.fromUnitGray(d);
                    }
                    else{
                        c = tile.getAssigned().getSpatialColor();
                    }
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
            // Drawing the shaded overlay map
//            setStroke(ExtendedColors.gray,1,Dashing.SOLID);
//            setFill(ExtendedColors.black,Hashures.SOLID);
//            setAlpha(0.5);
//            for (Region reg : data.map) {
//                draw(reg.getShape());
//                if(!data.gridlabels && drawLabels)
//                    draw(reg.getPos(), reg.getLabel());
//            }
//            setAlpha(1);
            setStroke(ExtendedColors.black,strokeSize,Dashing.SOLID);
            if(dataColored){
                setStroke(ExtendedColors.darkOrange,strokeSize,Dashing.SOLID);
            }
            if(drawTears){
                for (int i = 0; i < data.grid.getColumns(); i++) {
                    for (int j = 0; j < data.grid.getRows(); j++) {
                        if(data.grid.get(i, j).getAssigned() == null)
                            continue;
                        Rectangle r = data.grid.get(i, j).getShape();
                        if(i<data.grid.getColumns()-1){
                            if(data.grid.get(i+1, j).getAssigned() != null && data.grid.get(i, j).getAssigned().getPos().distanceTo(data.grid.get(i+1, j).getAssigned().getPos()) > data.getTearDist()){
                                //Set thickness based on distance
                                double thickness = 5*strokeSize*(data.grid.get(i, j).getAssigned().getPos().distanceTo(data.grid.get(i+1, j).getAssigned().getPos()))/diagonalDistance;
                                setStroke(beige,thickness,Dashing.SOLID);
                                draw(r.rightSide());
                            }
                            
                        }
                        if(j<data.grid.getRows()-1){
                            if(data.grid.get(i, j+1).getAssigned() != null && data.grid.get(i, j).getAssigned().getPos().distanceTo(data.grid.get(i, j+1).getAssigned().getPos()) > data.getTearDist()){
                                //Set thickness based on distance
                                double thickness = 5*strokeSize*(data.grid.get(i, j).getAssigned().getPos().distanceTo(data.grid.get(i, j+1).getAssigned().getPos()))/diagonalDistance;
                                setStroke(beige,thickness,Dashing.SOLID);
                                draw(r.bottomSide());
                            }
                        }
                    }
                }
            }
            setStroke(ExtendedColors.lightGray,strokeSize,Dashing.SOLID);
            if(drawExtras){
                for (Tile tile : data.grid){
                    if(tile.getAssigned() != null){
                        if(dataColored){
                            Vector ref = tile.getAssigned().getPos().clone();
                            Vector cp = Vector.divide(Vector.add(Vector.multiply(3, tile.getCenter().clone()), ref),4);
                            cp.rotate(Math.PI/2, tile.getCenter().clone());
                            BezierCurve bc = new BezierCurve(tile.getCenter(),cp,tile.getAssigned().getPos());
//                            BezierCurve bc = new BezierCurve(tile.getCenter(),tile.getAssigned().getPos());
                            draw(bc);
                            // Drawing the hairpin
                            setFill(ExtendedColors.lightGray,Hashures.SOLID);
                            draw(new Circle(tile.getCenter(),strokeSize));
                        }
                        else{
                            double d = tile.getData()/data.map.getMaxData();
                            if(d > 0){
    //                            System.out.println(d);
                                double size = tile.getLength()*d * 0.9;
    //                            Rectangle r = Circle.byCenterAndSize(tile.getCenter(),size,size);
                                Circle r = new Circle(tile.getCenter(),size/2);
                                draw(r);
                            }
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
            data.selectTile(loc);
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
