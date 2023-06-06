package com.example.demo;

public class MyEvent {
    private final int id;
    private final String type;

    public MyEvent(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}

