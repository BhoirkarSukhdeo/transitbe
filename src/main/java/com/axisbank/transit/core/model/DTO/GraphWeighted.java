package com.axisbank.transit.core.model.DTO;

import java.util.HashSet;
import java.util.Set;

public class GraphWeighted {
    private Set<NodeWeighted> nodes;
    private boolean directed;
    private long edgeCount = 0;

    public GraphWeighted(boolean directed) {
        this.directed = directed;
        nodes = new HashSet<>();
    }

    public GraphWeighted(Set<NodeWeighted> nodes, boolean directed, long edgeCount) {
        this.nodes = nodes;
        this.directed = directed;
        this.edgeCount = edgeCount;
    }

    public GraphWeighted(){}

    public Set<NodeWeighted> getNodes() {
        return nodes;
    }

    public void setNodes(Set<NodeWeighted> nodes) {
        this.nodes = nodes;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public long getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(long edgeCount) {
        this.edgeCount = edgeCount;
    }

    public void addEdge(NodeWeighted source, NodeWeighted destination, double weight, String edgeName) {
        // Since we're using a Set, it will only add the nodes
        // if they don't already exist in our graph
        nodes.add(source);
        nodes.add(destination);

        // We're using addEdgeHelper to make sure we don't have duplicate edges
        addEdgeHelper(source, destination, weight, edgeName);

        if (!directed && source != destination) {
            addEdgeHelper(destination, source, weight, edgeName);
        }
    }

    private void addEdgeHelper(NodeWeighted a, NodeWeighted b, double weight, String edgeName) {
        // Go through all the edges and see whether that edge has
        // already been added
        for (EdgeWeighted edge : a.edges) {
            if (edge.source.equals(a.getName()) && edge.destination.equals(b.getName())) {
                // Update the value in case it's a different one now
                edge.weight = weight;
                return;
            }
        }
        // If it hasn't been added already (we haven't returned
        // from the for loop), add the edge
        edgeCount++;
        a.edges.add(new EdgeWeighted(a.getName(), b.getName(), weight, edgeName, edgeCount));
    }
}