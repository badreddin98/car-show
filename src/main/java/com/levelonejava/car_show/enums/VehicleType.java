package com.levelonejava.car_show.enums;

public enum VehicleType {
    SEDAN("Sedan"),
    TRUCK("Truck"),
    SUV("SUV"),
    RV("RV");

    private String name;

    VehicleType(String name){
        this.name = name;

    }

    public String getName(){
        return name;
    }
}
