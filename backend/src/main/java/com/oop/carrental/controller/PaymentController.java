package com.oop.carrental.controller;

import com.oop.carrental.dto.PaymentDTO;
import com.oop.carrental.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll() {

        List<PaymentDTO> paymentList = service.findAll();
        return ResponseEntity.ok(paymentList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getById(@PathVariable Long id) {

        PaymentDTO payment = service.findById(id);
        return ResponseEntity.ok(payment);

    }

    @PostMapping
    public ResponseEntity<PaymentDTO> create(@Valid @RequestBody PaymentDTO dto) {

        PaymentDTO savedPayment = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);

    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> update(@PathVariable Long id, @Valid @RequestBody PaymentDTO dto) {

        PaymentDTO updatedPayment = service.update(id, dto);
        return ResponseEntity.ok(updatedPayment);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();

    }

}
