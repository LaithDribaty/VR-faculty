package com.laith.sparkvr;

import javafx.util.Pair;

public class Line {
    private Pair p1;
    private Pair p2;

    public Line(Pair p1, Pair p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Pair getP1() {
        return p1;
    }
    
    public int getI1() {
        return (int) p1.getKey();
    }

    public int getJ1() {
        return (int) p1.getValue();
    }

    public Pair getP2() {
        return p2;
    }
    
    public int getI2() {
        return (int) p2.getKey();
    }

    public int getJ2() {
        return (int) p2.getValue();
    }
    
    public boolean equals(Line line){
        return (int) this.p1.getKey() == (int) line.p1.getKey() 
            && (int) this.p1.getValue()== (int) line.p1.getValue()
            && (int) this.p2.getKey() == (int) line.p2.getKey() 
            && (int) this.p2.getValue()== (int) line.p2.getValue();
    }
}
