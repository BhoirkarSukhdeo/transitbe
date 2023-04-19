package com.axisbank.transit.core.model.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

public class EdgeWeighted implements Comparable<EdgeWeighted> {
    long id;
    String source;
    String destination;
    double weight;
    String edgeName;

    EdgeWeighted(String s, String d, double w, String en, long id) {
        source = s;
        destination = d;
        weight = w;
        edgeName = en;
        this.id=id;
    }

    EdgeWeighted(){}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getEdgeName() {
        return edgeName;
    }

    public void setEdgeName(String edgeName) {
        this.edgeName = edgeName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toString() {
        return String.format("(%s -> %s, %f)", source, destination, weight);
    }


    public int compareTo(EdgeWeighted otherEdge) {
        if (this.weight > otherEdge.weight) {
            return 1;
        }
        else return -1;
    }
}