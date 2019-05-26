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

    public EventTypesEnum getEventType() {
        return eventType;
    }

}
