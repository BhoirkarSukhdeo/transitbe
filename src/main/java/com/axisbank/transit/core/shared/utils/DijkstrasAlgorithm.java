package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.bus.model.DTO.BusSrcDestRouteDTO;
import com.axisbank.transit.core.model.DTO.EdgeWeighted;
import com.axisbank.transit.core.model.DTO.NodeWeighted;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DijkstrasAlgorithm {
    Set<NodeWeighted> nodes;

    public Set<NodeWeighted> getNodes() {
        return nodes;
    }

    public void setNodes(Set<NodeWeighted> nodes) {
        this.nodes = nodes;
    }

    public DijkstrasAlgorithm(Set<NodeWeighted> nodes) {
        this.nodes = nodes;
    }

    public List<BusSrcDestRouteDTO> DijkstraShortestPath(String startId, String endId) {
        // We keep track of which path gives us the shortest path for each node
        // by keeping track how we arrived at a particular node, we effectively
        // keep a "pointer" to the parent node of each node, and we follow that
        // path to the start
        HashMap<NodeWeighted, NodeWeighted> changedAt = new HashMap<>();
        Map<String, Map<String,String>> changeEdges = new HashMap<>();
        Map<String, NodeWeighted> nodeMap = new HashMap<>();

        // Keeps track of the shortest path we've found so far for every node
        HashMap<NodeWeighted, Double> shortestPathMap = new HashMap<>();
        NodeWeighted  start = null;
        NodeWeighted end;
        // Setting every node's shortest path weight to positive infinity to start
        // except the starting node, whose shortest path weight is 0
        for (NodeWeighted node : nodes) {
            if (node.getName().equals(startId)){
                start = node;
                changedAt.put(start, null);
                shortestPathMap.put(start, 0.0);
            }
            else shortestPathMap.put(node, Double.POSITIVE_INFINITY);
            nodeMap.put(node.getName(),node);
        }
        if(start==null)
            start=nodeMap.get(startId);
        end = nodeMap.get(endId);

        // Now we go through all the nodes we can go to from the starting node
        for (EdgeWeighted edge : start.getEdges()) {
            NodeWeighted dest = nodeMap.get(edge.getDestination());
            shortestPathMap.put(dest, edge.getWeight());
            changedAt.put(dest, start);
            Map<String,String> curEdge = new HashMap<>();
            curEdge.put(start.getName(), edge.getEdgeName());
            changeEdges.put(dest.getName(),curEdge);
        }

        start.visit();

        // This loop runs as long as there is an unvisited node that we can
        // reach from any of the nodes we could till then
        while (true) {
            NodeWeighted currentNode = closestReachableUnvisited(shortestPathMap);
            // If we haven't reached the end node yet, and there isn't another
            // reachable node the path between start and end doesn't exist
            // (they aren't connected)
            if (currentNode == null) {
                log.debug("There isn't a path between " + start.getName() + " and " + end.getName());
                return null;
            }

            // If the closest non-visited node is our destination, we want to print the path
            if (currentNode.getName().equals(end.getName())) {
                log.info("The path with the smallest weight between "
                        + start.getName() + " and " + end.getName() + " is:");

                NodeWeighted child = end;

                String path = end.getName();
                SortedSet<BusSrcDestRouteDTO> data = new TreeSet<>(Comparator.comparing(BusSrcDestRouteDTO::getSrNum));
                int srNum = Integer.MAX_VALUE;
                while (true) {
                    NodeWeighted parent = changedAt.get(child);
                    BusSrcDestRouteDTO route = new BusSrcDestRouteDTO();
                    if (parent == null) {
                        break;
                    }

                    // Since our changedAt map keeps track of child -> parent relations
                    // in order to print the path we need to add the parent before the child and
                    // it's descendants
                    path = parent.getName() + " " + path;
                    String edge =  changeEdges.get(child.getName()).get(parent.getName());
                    route.setSrNum(srNum);
                    route.setRouteCode(edge);
                    route.setSourceStation(parent.getName());
                    route.setDestinationStation(child.getName());
                    data.add(route);
                    child = parent;
                    srNum--;
                }
                Map<String, BusSrcDestRouteDTO> prevRoute = new HashMap<>();
                SortedSet<BusSrcDestRouteDTO> lst = new TreeSet<>(Comparator.comparing(BusSrcDestRouteDTO::getSrNum));
                for(BusSrcDestRouteDTO bsd: data){
                    BusSrcDestRouteDTO tmp = prevRoute.get(bsd.getRouteCode());
                    if(tmp!=null){
                        tmp.setDestinationStation(bsd.getDestinationStation());
                    } else {
                        tmp = new BusSrcDestRouteDTO();
                        tmp.setSourceStation(bsd.getSourceStation());
                        tmp.setDestinationStation(bsd.getDestinationStation());
                        tmp.setRouteCode(bsd.getRouteCode());
                        tmp.setSrNum(bsd.getSrNum());
                    }
                    prevRoute.put(bsd.getRouteCode(), tmp);
                }
                prevRoute.forEach((k,v)->{
                    lst.add(v);
                });
                return new ArrayList<>(lst);
            }
            currentNode.visit();

            // Now we go through all the unvisited nodes our current node has an edge to
            // and check whether its shortest path value is better when going through our
            // current node than whatever we had before
            for (EdgeWeighted edge : currentNode.getEdges()) {
                NodeWeighted dest = nodeMap.get(edge.getDestination());
                if (dest.isVisited())
                    continue;

                if (shortestPathMap.get(currentNode)
                        + edge.getWeight()
                        < shortestPathMap.get(dest)) {
                    shortestPathMap.put(dest,
                            shortestPathMap.get(currentNode) + edge.getWeight());
                    changedAt.put(dest, currentNode);
                    Map<String,String> curEdge = new HashMap<>();
                    curEdge.put(currentNode.getName(), edge.getEdgeName());
                    changeEdges.put(dest.getName(),curEdge);
                }
            }
        }
    }
    private NodeWeighted closestReachableUnvisited(HashMap<NodeWeighted, Double> shortestPathMap) {

        double shortestDistance = Double.POSITIVE_INFINITY;
        NodeWeighted closestReachableNode = null;
        for (NodeWeighted node : nodes) {
            if (node.isVisited())
                continue;

            double currentDistance = shortestPathMap.get(node);
            if (currentDistance == Double.POSITIVE_INFINITY)
                continue;

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                closestReachableNode = node;
            }
        }
        return closestReachableNode;
    }
}
