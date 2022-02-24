/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

import data.GeographicMap;
import data.Region;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import nl.tue.geometrycore.geometry.GeometryType;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.io.ReadItem;
import nl.tue.geometrycore.io.ipe.IPEReader;

/**
 *
 * @author wmeulema
 */
public class IPEImport {

    static JFileChooser choose = new JFileChooser("../data");

    public static GeographicMap readIPE() {
        int result = choose.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                IPEReader read = IPEReader.fileReader(choose.getSelectedFile());

                List<ReadItem> items = read.read();

                GeographicMap map = new GeographicMap();

                for (ReadItem it : items) {
                    if (it.getString() != null) {
                        Region r = new Region();
                        r.setLabel(it.getString());
                        r.setPos((Vector) it.getGeometry());
                        map.add(r);

                        for (ReadItem it2 : items) {
                            if (it2.getGeometry().getGeometryType() == GeometryType.POLYGON) {
                                // contains?
                                Polygon p = (Polygon) it2.getGeometry();
                                if (p.contains(r.getPos())) {
                                    r.setShape(p);
                                    break;
                                }
                            }
                        }
                    }
                }

                return map;

            } catch (IOException ex) {
                Logger.getLogger(IPEImport.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return null;

    }
}
