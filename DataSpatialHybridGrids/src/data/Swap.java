/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import nl.tue.geometrycore.datastructures.priorityqueue.BasicIndexable;

/**
 *
 * @author 20184261
 */
public class Swap extends BasicIndexable{
    double miGain;
    int i;
    int j;
    int k;
    int l;
    

    public Swap(double miGain, int i, int j, int k, int l) {
        this.miGain = miGain;
        // Was t1 before
        this.i = i;
        this.j = j;
        // Was t2 before
        this.k = k;
        this.l = l;
    }

    public double getMiGain() {
        return miGain;
    }

    public void setMiGain(double miGain) {
        this.miGain = miGain;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getK() {
        return k;
    }

    public int getL() {
        return l;
    }
    
    public int computeHash(int maxDimension){
        return i + maxDimension * j + (int)Math.pow(maxDimension,2) * k + (int)Math.pow(maxDimension,3) * l;
    }
    
}
