/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import nl.tue.geometrycore.datastructures.priorityqueue.Indexable;

/**
 *
 * @author 20184261
 */
public class Swap implements Indexable{
    double miGain;
    int index;
    int i;
    int j;
    int k;
    int l;
    

    public Swap(double miGain, int index, int i, int j, int k, int l) {
        this.miGain = miGain;
        this.index = index;
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

    
    
    @Override
    public void setIndex(int i) {
        index = i;
    }

    @Override
    public int getIndex() {
        return index;
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
    
}
