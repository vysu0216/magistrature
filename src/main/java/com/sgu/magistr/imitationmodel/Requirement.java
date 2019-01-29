package com.sgu.magistr.imitationmodel;

public class Requirement {
    private double procStartTime;   //                фактическое время начала обслуживания
    private double releaseTime;     //                время завершения обслуживания
    private double procTime;        //                время обслуживания
    private double generationTime;  //                время поступления требования в систему

    private int reqClass;

    public Requirement(double generationTime, int reqClass) {
        this.reqClass = reqClass;
        this.generationTime = generationTime;
    }

    public Requirement(int reqClass) {
        this.reqClass = reqClass;
    }

    public double getProcStartTime() {
        return procStartTime;
    }

    public void setProcStartTime(double procStartTime) {
        this.procStartTime = procStartTime;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(double releaseTime) {
        this.releaseTime = releaseTime;
    }

    public double getProcTime() {
        return procTime;
    }

    public void setProcTime(double procTime) {
        this.procTime = procTime;
    }

    public double getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(double generationTime) {
        this.generationTime = generationTime;
    }

    public int getReqClass() {
        return reqClass;
    }

    public void setReqClass(int reqClass) {
        this.reqClass = reqClass;
    }

    @Override
    public String toString() {
        return "Class: " + reqClass + " Generated at: " + String.format("%.10f", generationTime) + " Process started: " + String.format("%.10f", procStartTime) + " Processing time: " + String.format("%.10f", procTime) + " Release time: " + String.format("%.10f", releaseTime);
    }
}
