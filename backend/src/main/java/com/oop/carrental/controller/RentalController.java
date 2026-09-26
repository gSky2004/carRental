package com.oop.carrental.controller;

import com.oop.carrental.dto.RentalDTO;
import com.oop.carrental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService service;

    public RentalController(RentalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAll() {

        List<RentalDTO> rentalList = service.findAll();
        return ResponseEntity.ok(rentalList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getById(@PathVariable Long id) {

        RentalDTO rental = service.findById(id);
        return ResponseEntity.ok(rental);

    }

    @PostMapping
    public ResponseEntity<RentalDTO> rentCar(@Valid @RequestBody RentalDTO dto) {

        RentalDTO newRental = service.rentCar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRental);

    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> update(@PathVariable Long id, @Valid @RequestBody RentalDTO dto) {

        RentalDTO updatedRental = service.update(id, dto);
        return ResponseEntity.ok(updatedRental);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();

    }

}
