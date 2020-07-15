package com.demo2.demo.entities;

public class Point implements Comparable<Point> {
    private int num;
    private double x;
    private double y;
    private boolean isVisit;
    private int cluster;
    private boolean isNoised;

    public Point(double x,double y) {
        this.x = x;
        this.y = y;
        this.isVisit = false;
        this.cluster = 0;
        this.isNoised = false;
    }



    public double getDistance(Point point) {
        return Math.sqrt((x-point.x)*(x-point.x)+(y-point.y)*(y-point.y));
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setVisit(boolean isVisit) {
        this.isVisit = isVisit;
    }

    public boolean getVisit() {
        return isVisit;
    }

    public int getCluster() {
        return cluster;
    }

    public void setNoised(boolean isNoised) {
        this.isNoised = isNoised;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public boolean getNoised() {
        return this.isNoised;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return y+","+x;
    }


    @Override
    public int compareTo(Point point){
        int a=this.cluster-point.cluster;
        int b = Double.compare(this.x, point.x);
        int c = Double.compare(point.y,this.y);

        if (a==0){
            if (c==0){
                return b;
            }
            return c;
        }else {
            return a;
        }


    }
}
