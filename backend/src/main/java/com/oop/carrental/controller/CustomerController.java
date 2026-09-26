package com.oop.carrental.controller;

import com.oop.carrental.dto.CustomerDTO;
import com.oop.carrental.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAll() {

        List<CustomerDTO> customerList = service.findAll();
        return ResponseEntity.ok(customerList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {

        CustomerDTO customer = service.findById(id);
        return ResponseEntity.ok(customer);

    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO dto) {

        CustomerDTO savedCustomer = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);

    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {

        CustomerDTO updatedCustomer = service.update(id, dto);
        return ResponseEntity.ok(updatedCustomer);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();

    }

}
