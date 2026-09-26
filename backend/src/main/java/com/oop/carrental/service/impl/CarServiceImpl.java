package com.oop.carrental.service.impl;

import com.oop.carrental.dto.CarDTO;
import com.oop.carrental.entity.Car;
import com.oop.carrental.exception.ResourceNotFoundException;
import com.oop.carrental.repository.CarRepository;
import com.oop.carrental.repository.RentalRepository;
import com.oop.carrental.service.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository repository;
    private final RentalRepository rentalRepo;

    public CarServiceImpl(CarRepository repository, RentalRepository rentalRepo) {
        this.repository = repository;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<CarDTO> findAll() {

        List<Car> carList = repository.findAll();
        List<CarDTO> dtoList = new ArrayList<>();

        for (Car car : carList) {

            CarDTO dto = convertToDTO(car);
            dtoList.add(dto);

        }

        return dtoList;

    }

    @Override
    public CarDTO findById(Long id) {

        Car car = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Car not found with id: " + id)
        );

        return convertToDTO(car);

    }

    @Override
    @Transactional
    public CarDTO save(CarDTO dto) {

        Car car = convertToEntity(dto);
        Car savedCar = repository.save(car);

        return convertToDTO(savedCar);

    }

    @Override
    @Transactional
    public CarDTO update(Long id, CarDTO dto) {

        Car car = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Car not found with id: " + id)
        );

        car.setCarName(dto.getCarName());
        car.setPlateNumber(dto.getPlateNumber());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setManufactureYear(dto.getManufactureYear());
        car.setRentalPricePerDay(dto.getRentalPricePerDay());
        car.setStatus(dto.getStatus());
        car.setBranchId(dto.getBranchId());

        Car updatedCar = repository.save(car);

        return convertToDTO(updatedCar);

    }

    @Override
    @Transactional
    public void delete(Long id) {

        Car car = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Car not found with id: " + id)
        );

        if ("Rented".equals(car.getStatus())) {
            throw new IllegalStateException("Cannot delete: car is currently rented. Please process the return first.");
        }

        rentalRepo.detachCarId(id);
        repository.deleteById(id);

    }

    private CarDTO convertToDTO(Car car) {

        CarDTO dto = new CarDTO();

        dto.setId(car.getId());
        dto.setCarName(car.getCarName());
        dto.setPlateNumber(car.getPlateNumber());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setManufactureYear(car.getManufactureYear());
        dto.setRentalPricePerDay(car.getRentalPricePerDay());
        dto.setStatus(car.getStatus());
        dto.setBranchId(car.getBranchId());
        dto.setCreatedAt(car.getCreatedAt());
        dto.setUpdatedAt(car.getUpdatedAt());

        return dto;

    }

    private Car convertToEntity(CarDTO dto) {

        Car car = new Car();

        car.setCarName(dto.getCarName());
        car.setPlateNumber(dto.getPlateNumber());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setManufactureYear(dto.getManufactureYear());
        car.setRentalPricePerDay(dto.getRentalPricePerDay());
        car.setStatus(dto.getStatus());
        car.setBranchId(dto.getBranchId());

        return car;

    }

}
