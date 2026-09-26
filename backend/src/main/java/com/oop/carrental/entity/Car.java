package com.oop.carrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cars")
public class Car extends BaseEntity {

    private String carName;
    private String plateNumber;
    private String brand;
    private String model;
    private Integer manufactureYear;
    private Double rentalPricePerDay;
    private String status;
    private Long branchId;

    public Car() {

    }

    public Car(String carName, String plateNumber, String brand, String model, Integer manufactureYear, Double rentalPricePerDay, String status, Long branchId) {

        this.carName = carName;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.manufactureYear = manufactureYear;
        this.rentalPricePerDay = rentalPricePerDay;
        this.status = status;
        this.branchId = branchId;

    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public Double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(Double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

}
