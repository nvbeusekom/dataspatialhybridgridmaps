/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataspatialhybridgrids;

import data.Region;
import data.Tile;
import java.awt.Color;
import java.awt.event.KeyEvent;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.geometryrendering.GeometryPanel;
import nl.tue.geometrycore.geometryrendering.styling.Dashing;
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

    @Override
    protected void drawScene() {
        if (data.map == null) {
            return;
        }

        setSizeMode(SizeMode.VIEW);

        setStroke(Color.black, 1, Dashing.SOLID);
        setTextStyle(TextAnchor.CENTER, 10);
        for (Region reg : data.map) {
            draw(reg.getShape());
            draw(reg.getPos(), reg.getLabel());
        }

        if (data.grid != null) {

            setStroke(ExtendedColors.darkOrange, 1, Dashing.SOLID);

            for (Tile tile : data.grid) {
                draw(tile.getShape());
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

    }

    @Override
    protected void keyPress(int keycode, boolean ctrl, boolean shift, boolean alt) {
        switch (keycode) {
            case KeyEvent.VK_O:
                data.load();
                break;
        }
    }

}
