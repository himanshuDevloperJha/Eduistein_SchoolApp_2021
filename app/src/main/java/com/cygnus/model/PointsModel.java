package com.cygnus.model;

public class PointsModel {
    String name;
    Integer points;
    Integer usertotalpoints;

    public PointsModel(String name, Integer points,Integer usertotalpoints) {
       this.setName(name);
       this.setPoints(points);
       this.setUsertotalpoints(usertotalpoints);
    }

    public Integer getUsertotalpoints() {
        return usertotalpoints;
    }

    public void setUsertotalpoints(Integer usertotalpoints) {
        this.usertotalpoints = usertotalpoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
