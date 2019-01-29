package com.sgu.magistr.imitationmodel;

public class Event {

    private double time;
    private double genTime;
    private EventTypesEnum eventType;

    public Event(double time, EventTypesEnum eventType) {
        this.time = time;
        this.eventType = eventType;
    }

    public Event(double time, double genTime, EventTypesEnum eventType) {
        this.time = time;
        this.genTime = genTime;
        this.eventType = eventType;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getGenTime() {
        return genTime;
    }

    public void setGenTime(double genTime) {
        this.genTime = genTime;
    }

    public EventTypesEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypesEnum eventType) {
        this.eventType = eventType;
    }

}
