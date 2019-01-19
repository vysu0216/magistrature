package com.sgu.magistr.imitationmodel.refactored;

public class Event {

    private double time;
    private EventTypesEnum eventType;

    public Event(double time, EventTypesEnum eventType) {
        this.time = time;
        this.eventType = eventType;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public EventTypesEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypesEnum eventType) {
        this.eventType = eventType;
    }

}
