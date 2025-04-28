package com.example.welcomscreen;

public class Event {
    private String name;
    private String speaker;
    private String group;
    private String startTime;
    private String endTime;

    public Event(String name, String speaker, String group, String startTime, String endTime) {
        this.name = name;
        this.speaker = speaker;
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getGroup() {
        return group;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}