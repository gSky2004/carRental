package com.oop.carrental.controller;

import com.oop.carrental.dto.CarDTO;
import com.oop.carrental.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAll() {

        List<CarDTO> carList = service.findAll();
        return ResponseEntity.ok(carList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getById(@PathVariable Long id) {

        CarDTO car = service.findById(id);
        return ResponseEntity.ok(car);

    }

    @PostMapping
    public ResponseEntity<CarDTO> create(@Valid @RequestBody CarDTO dto) {

        CarDTO savedCar = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);

    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> update(@PathVariable Long id, @Valid @RequestBody CarDTO dto) {

        CarDTO updatedCar = service.update(id, dto);
        return ResponseEntity.ok(updatedCar);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();

    }

}
