package com.axisbank.transit.journey.model.DTO;

public class JourneyRouteDTO {
    private String name;
    private String type;
    private String nextType;
    private String next;
    private double duration;
    private double distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNextType() {
        return nextType;
    }

    public void setNextType(String nextType) {
        this.nextType = nextType;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "JourneyRouteDTO{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", nextType='" + nextType + '\'' +
                ", next='" + next + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                '}';
    }
}
