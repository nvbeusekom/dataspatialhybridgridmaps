/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import algorithms.GridGenerator;
import data.Grid;
import data.Region;
import data.Tile;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.curved.BezierCurve;
import nl.tue.geometrycore.geometry.curved.Circle;
import nl.tue.geometrycore.geometry.linear.LineSegment;
import nl.tue.geometrycore.geometry.linear.Polygon;
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
    boolean drawTears = false;
    double strokeSize = 0.4;
    boolean drawOverlay = false;
    boolean drawHigherBorders = true;
    boolean normalHierarchical = true;
    
    @Override
    protected void drawScene() {
        Grid grid = data.grid;
        if(data.innerGrid != null){
            grid = data.innerGrid;
        }
        if (data.map == null) {
            return;
        }
        
        setLayer("grid");
        
        Color legendColors[] = {ExtendedColors.lightBlue,ExtendedColors.lightGreen,ExtendedColors.lightOrange,ExtendedColors.lightPurple,ExtendedColors.lightRed,ExtendedColors.darkBlue,ExtendedColors.darkGreen,ExtendedColors.darkOrange,ExtendedColors.darkPurple,ExtendedColors.darkRed,Color.CYAN,Color.YELLOW};
        int ci = 0;
        for(Region r : data.map){
//            r.setSpatialColor(legendColors[ci]);
            ci++;
        } 
        Color beige = ExtendedColors.fromUnitRGB(0.96, 0.91, 0.84);
        this.setBackground(beige);
        setSizeMode(SizeMode.VIEW);

        setStroke(Color.black, strokeSize, Dashing.SOLID);
        setTextStyle(TextAnchor.CENTER, 10);
        for (Region reg : data.map) {
            if(grid == null)
                setFill(reg.getSpatialColor(),Hashures.SOLID);
            //draw(reg.getShape());
            if(!data.gridlabels && drawLabels)
                draw(reg.getPos(), reg.getLabel());
        }
        
        if (grid != null) {
            double diagonalDistance = grid.get(0, 0).getCenter().distanceTo(grid.get(grid.getColumns()-1, grid.getRows()-1).getCenter());
            
            setStroke(ExtendedColors.darkBlue, strokeSize, Dashing.SOLID);
            int count = 0;
            for (Tile tile : grid) {
                if(tile.getAssigned()==null) {
                    setStroke(ExtendedColors.darkBlue, strokeSize, Dashing.SOLID);
                    setFill(null,Hashures.SOLID);
                }
                else {
                    Color c;
                    if(dataColored){
                        double d;
                        if(data.lowerMap != null){
                            d = tile.getData()/data.lowerMap.getMaxData();
                        }else{
                            d = tile.getData()/data.map.getMaxData();
                        }
//                        System.out.println(d);
                        c = ExtendedColors.fromUnitGray(d);
                    }
                    else{
                        c = tile.getAssigned().getSpatialColor();
//                        c = tile.getAssigned().getParent().getSpatialColor();
                    }
                    setStroke(c, strokeSize, Dashing.SOLID);
                    setFill(c,Hashures.SOLID);
                    
                }
                
                count++;
                
                draw(tile.getShape());
                if(data.gridlabels && drawLabels){
                    setStroke(ExtendedColors.black,strokeSize,Dashing.SOLID);
                    draw(tile.getCenter(), tile.getLabel());
                }
                setFill(null,Hashures.SOLID);
            }
            // Drawing the shaded overlay map
            if(drawOverlay){
                setStroke(ExtendedColors.lightGreen,strokeSize,Dashing.SOLID);
                setFill(ExtendedColors.black,Hashures.SOLID);
                setAlpha(0.5);
                for (Region reg : data.map) {
                    draw(reg.getShape());
                    if(reg.getLocalMap()!=null){
                        for (Region inner : reg.getLocalMap()) {
                            draw(inner.getShape());
                            if(!data.gridlabels && drawLabels)
                                draw(inner.getPos(), inner.getLabel());
                        }
                    }
                    if(!data.gridlabels && drawLabels)
                        draw(reg.getPos(), reg.getLabel());
                }
                setAlpha(1);
            }
            // Draw extra hierarchical labels
            
            setStroke(ExtendedColors.black,strokeSize,Dashing.SOLID);
            if(dataColored){
                setStroke(ExtendedColors.darkOrange,strokeSize,Dashing.SOLID);
            }
            if(drawTears){
                for (int i = 0; i < grid.getColumns(); i++) {
                    for (int j = 0; j < grid.getRows(); j++) {
                        if(grid.get(i, j).getAssigned() == null)
                            continue;
                        Rectangle r = grid.get(i, j).getShape();
                        if(i<grid.getColumns()-1){
                            if(grid.get(i+1, j).getAssigned() != null && grid.get(i, j).getAssigned().getPos().distanceTo(grid.get(i+1, j).getAssigned().getPos()) > data.getTearDist()){
                                //Set thickness based on distance
                                double thickness = 5*strokeSize*(grid.get(i, j).getAssigned().getPos().distanceTo(grid.get(i+1, j).getAssigned().getPos()))/diagonalDistance;
                                setStroke(beige,thickness,Dashing.SOLID);
                                draw(r.rightSide());
                            }
                            
                        }
                        if(j<grid.getRows()-1){
                            if(grid.get(i, j+1).getAssigned() != null && grid.get(i, j).getAssigned().getPos().distanceTo(grid.get(i, j+1).getAssigned().getPos()) > data.getTearDist()){
                                //Set thickness based on distance
                                double thickness = 5*strokeSize*(grid.get(i, j).getAssigned().getPos().distanceTo(grid.get(i, j+1).getAssigned().getPos()))/diagonalDistance;
                                setStroke(beige,thickness,Dashing.SOLID);
                                draw(r.bottomSide());
                            }
                        }
                    }
                }
            }
            setLayer("boundaries");
            if(drawHigherBorders){
                for (int i = 0; i < grid.getColumns(); i++) {
                    for (int j = 0; j < grid.getRows(); j++) {
                        if(grid.get(i, j).getAssigned() == null)
                            continue;
                        Rectangle r = grid.get(i, j).getShape();
                        if(i<grid.getColumns()-1){
                            if(grid.get(i+1, j).getAssigned() != null && grid.get(i+1, j).getAssigned().getParent() != null && grid.get(i, j).getAssigned().getParent() != null && grid.get(i, j).getAssigned().getParent() != grid.get(i+1, j).getAssigned().getParent()){
                                //Set thickness based on distance
                                double thickness = 3 * strokeSize;
                                setStroke(beige,thickness,Dashing.SOLID);
                                draw(r.rightSide());
                            }
                            
                        }
                        if(j<grid.getRows()-1){
                            if(grid.get(i, j+1).getAssigned() != null && grid.get(i, j+1).getAssigned().getParent() != null && grid.get(i, j).getAssigned().getParent() != null && grid.get(i, j).getAssigned().getParent() != grid.get(i, j+1).getAssigned().getParent()){
                                //Set thickness based on distance
                                double thickness = 3 * strokeSize;
                                setStroke(beige,thickness,Dashing.SOLID);
                                draw(r.bottomSide());
                            }
                        }
                    }
                }
            }
            setLayer("data");
            setStroke(ExtendedColors.lightGray,0,Dashing.SOLID);
            setFill(ExtendedColors.black,Hashures.SOLID);
            setAlpha(0.5);
            if(drawExtras){
                for (Tile tile : grid){
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
                                d = Math.sqrt(d);
                                double size = tile.getLength()*d * 0.9;
    //                            Rectangle r = Circle.byCenterAndSize(tile.getCenter(),size,size);
                                if(tile.getLabel() == data.labelOfSpecialRegion){
                                    setFill(ExtendedColors.white,Hashures.SOLID);
                                }
                                
                                Circle r = new Circle(tile.getCenter(),size/2);
                                draw(r);
                                if(tile.getLabel() == data.labelOfSpecialRegion){
                                    setFill(ExtendedColors.black,Hashures.SOLID);
                                }
                            }
                        }
                    }
                    else{
//                        draw(new Circle(tile.getCenter(),tile.getLength()/2));
                    }
                }
            }
            setStroke(ExtendedColors.lightGray,0.4,Dashing.SOLID);
            setLayer("grid");
            setAlpha(1);
            
            Rectangle bbox1 = data.grid.getBoundingBox().clone();
            bbox1.scale(0.25,bbox1.center());
            bbox1.translate(bbox1.width() * 3.5, 0);
            
            Rectangle bbox2 = data.grid.getBoundingBox().clone();
            if(data.innerGrid != null && normalHierarchical){
                bbox2.scale(0.25,bbox2.center());
                bbox2.translate(bbox2.width()*3.5, bbox1.height()+bbox1.height()/10);
            }
            else{
                bbox2.translate(-bbox2.width()*1.1, 0);
            }
            
            Rectangle bbox3 = data.grid.getBoundingBox().clone();
            bbox3.scale(0.25,bbox3.center());
            bbox3.translate(bbox3.width()*3.5, -bbox1.height()-bbox1.height()/10);
            
            for(Region r : data.map){
                for(Polygon p : r.getShape()){
                    Vector toCenter = Vector.subtract(p.centroid(),data.map.getBoundingBox().center());
                    Vector c2c = Vector.subtract(bbox2.center(),p.centroid());
                    Polygon p2 = p.clone();
                    p2.translate(c2c);
                    
                    if(data.innerGrid != null && normalHierarchical){
                        toCenter=Vector.divide(toCenter, 4);
                        p2.scale(0.25,p2.centroid());
                    }
                    p2.translate(toCenter);
                    setFill(r.getSpatialColor(),null);
                    draw(p2);
                    if(drawLabels){
                        draw(p2.centroid(), r.getLabel());
                    }
                }
                ci++;
            }
            
            if(data.innerGrid != null && normalHierarchical){
                Grid legendGrid = GridGenerator.generateSquareGrid(data.grid.getColumns(), data.grid.getRows(), bbox1, true);
                for (int i = 0; i < legendGrid.getColumns(); i++) {
                    for (int j = 0; j < legendGrid.getRows(); j++) {
                        if(data.grid.get(i,j).getAssigned() != null){
                            setFill(data.grid.get(i,j).getAssigned().getSpatialColor(),null);
                            draw(legendGrid.get(i, j).getShape());
                        }
                    }

                }
                setStroke(ExtendedColors.black,0,Dashing.SOLID);
                
                for(Region r : data.map){
                    for(Region r2: r.getLocalMap()){
                        for(Polygon p : r2.getShape()){
                            Vector toCenter = Vector.subtract(p.centroid(),data.map.getBoundingBox().center());
                            Vector c2c = Vector.subtract(bbox3.center(),p.centroid());
                            Polygon p2 = p.clone();
                            p2.translate(c2c);
                            p2.translate(Vector.divide(toCenter, 4));
                            p2.scale(0.25,p2.centroid());
                            setFill(r2.getSpatialColor(),null);
                            draw(p2);
                        }
                        ci++;
                    }
                } 
            }
            setStroke(ExtendedColors.black,20,Dashing.SOLID);
            this.setTextStyle(TextAnchor.TOP_LEFT, 11);
            Rectangle box = grid.getBoundingBox();
            Vector labelLoc = box.leftBottom().clone();
            
            labelLoc.translate(Vector.down(50));
            draw(labelLoc,String.format(Locale.US,"$S = %.3f$", grid.getSpatialDistortion()));
            this.setTextStyle(TextAnchor.TOP_RIGHT, 11);
            labelLoc.translate(Vector.right(box.width()));
            draw(labelLoc,String.format(Locale.US,"$D = %.3f$", grid.getMoransI()));
        }

    }

    
    @Override
    public Rectangle getBoundingRectangle() {
        if (data.map == null) {
            return null;
        }
        Rectangle r = data.map.getBoundingBox();
        r.include(Vector.add(r.center(),Vector.down(r.height()/2 + 200)));
        if(data.innerGrid== null){
            r.grow(data.map.getBoundingBox().width()*1.1, 0, 0, 0);
        }
        else{
            r.grow(0, data.map.getBoundingBox().width(), 0, 0);
            r.includeGeometry(data.innerGrid.getBoundingBox());
        }
        return r;

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
                data.loadIPEMap(false);
                break;
            case KeyEvent.VK_D:
                data.loadTSV(true,false);
                break;
        }
    }

}
