package com.axisbank.transit.core.model.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.io.Serializable;
import java.util.LinkedList;

public class NodeWeighted {
    // The int n and String name are just arbitrary attributes
    // we've chosen for our nodes these attributes can of course
    // be whatever you need
    int n;
    String name;
    private boolean visited;
    LinkedList<EdgeWeighted> edges;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public LinkedList<EdgeWeighted> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<EdgeWeighted> edges) {
        this.edges = edges;
    }

    public NodeWeighted(int n, String name) {
        this.n = n;
        this.name = name;
        visited = false;
        edges = new LinkedList<>();
    }

    public NodeWeighted(int n, String name, boolean visited, LinkedList<EdgeWeighted> edges) {
        this.n = n;
        this.name = name;
        this.visited = visited;
        this.edges = edges;
    }

    public NodeWeighted(){

    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    public void unvisit() {
        visited = false;
    }
}