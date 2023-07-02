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
import java.io.File;
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
public class CSVLoader {
    public static void loadCSV(GeographicMap map, JFrame frame, boolean loadForLowerLevel){
        if(map == null)
            return;
        File file = null;
        if(!loadForLowerLevel && false){
            file = new File("..\\data\\regionData.csv");
        }
        else{
            file = new File("..\\data\\LALowerTierData.csv");
        }

        try {
            Scanner sc = new Scanner(file);
            String[] categories = sc.nextLine().split(",");
//                String labelID = (String)JOptionPane.showInputDialog(
//                    frame,
//                    "Pick the label identifier",
//                    "Customized Dialog",
//                    JOptionPane.PLAIN_MESSAGE,
//                    null,
//                    categories,
//                    categories[0]);
//                String dataID = (String)JOptionPane.showInputDialog(
//                    frame,
//                    "Pick the data identifier",
//                    "Customized Dialog",
//                    JOptionPane.PLAIN_MESSAGE,
//                    null,
//                    categories,
//                    categories[0]);
            String labelID = "label";
            String dataID = "pop";

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
                String[] data = sc.nextLine().split(",");
                for(Region r : map){
                    if(loadForLowerLevel){
                        for(Region r2 : r.getLocalMap()){
                            if(r2.getLabel().equals(data[labelIndex])){
                                r2.setData(Double.parseDouble(data[dataIndex]));
                            }
                        }
                    }
                    else{
                        if(r.getLabel().equals(data[labelIndex])){
                            r.setData(Double.parseDouble(data[dataIndex]));
                        }
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(IPEImport.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
