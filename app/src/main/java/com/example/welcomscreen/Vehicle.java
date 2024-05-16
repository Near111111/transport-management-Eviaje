package com.example.welcomscreen;

public class Vehicle {
    private String name;
    private String model;
    private String plateNumber;
    private String color;
    private int sittingCapacity;
    private String coding;
    private int imageResource;

    public Vehicle(String name, String model, String plateNumber, String color, int sittingCapacity, String coding, int imageResource) {
        this.name = name;
        this.model = model;
        this.plateNumber = plateNumber;
        this.color = color;
        this.sittingCapacity = sittingCapacity;
        this.coding = coding;
        this.imageResource = imageResource;
    }

    // Getters
    public String getName() { return name; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
    public String getColor() { return color; }
    public int getSittingCapacity() { return sittingCapacity; }
    public String getCoding() { return coding; }
    public int getImageResource() { return imageResource; }
}
