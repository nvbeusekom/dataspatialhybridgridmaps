/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import data.GeographicMap;
import data.Region;
import static io.IPEImport.choose;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import nl.tue.geometrycore.geometry.GeometryType;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.geometryrendering.styling.ExtendedColors;
import nl.tue.geometrycore.io.ReadItem;
import nl.tue.geometrycore.io.ipe.IPEReader;

/**
 *
 * @author 20184261
 */
public class TSVLoader {
    static JFileChooser choose = new JFileChooser("../data");
    public static void loadTSV(GeographicMap map, JFrame frame){
        if(map == null)
            return;
        int result = choose.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                Scanner sc = new Scanner(choose.getSelectedFile());
                String[] categories = sc.nextLine().split("\t");
                String labelID = (String)JOptionPane.showInputDialog(
                    frame,
                    "Pick the label identifier",
                    "Customized Dialog",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    categories,
                    categories[0]);
                String dataID = (String)JOptionPane.showInputDialog(
                    frame,
                    "Pick the data identifier",
                    "Customized Dialog",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    categories,
                    categories[0]);
                int labelIndex = 0;
                int dataIndex = 0;
                for (int i = 0; i < categories.length; i++) {
                    if(categories[i].equals(labelID)){
                        labelIndex = i;
                    }
                    if(categories[i].equals(dataID)){
                        dataIndex = i;
                    }
                }
                while (sc.hasNextLine()) {
                    String[] data = sc.nextLine().split("\t");
                    for(Region r : map){
                        if(r.getLabel().equals(data[labelIndex])){
                            r.setData(Double.parseDouble(data[dataIndex]));
                        }
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(IPEImport.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    public static void loadColors(GeographicMap map){
        if(map == null)
            return;
        int result = choose.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                Scanner sc = new Scanner(choose.getSelectedFile());
                while (sc.hasNextLine()) {
                    String[] data = sc.nextLine().split("\t");
                    Color c = ExtendedColors.fromUnitRGB(Double.parseDouble(data[3])/255, Double.parseDouble(data[4])/255, Double.parseDouble(data[5])/255);
                    for(Region r : map){
                        if(r.getLabel().equals(data[0])){
                            r.setSpatialColor(c);
                        }
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(IPEImport.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
