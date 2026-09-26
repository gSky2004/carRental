package com.oop.carrental.service.impl;

import com.oop.carrental.dto.PaymentDTO;
import com.oop.carrental.entity.Payment;
import com.oop.carrental.exception.ResourceNotFoundException;
import com.oop.carrental.repository.PaymentRepository;
import com.oop.carrental.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PaymentDTO> findAll() {

        List<Payment> paymentList = repository.findAll();
        List<PaymentDTO> dtoList = new ArrayList<>();

        for (Payment payment : paymentList) {

            PaymentDTO dto = convertToDTO(payment);
            dtoList.add(dto);

        }

        return dtoList;

    }

    @Override
    public PaymentDTO findById(Long id) {

        Payment payment = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Payment not found with id: " + id)
        );

        return convertToDTO(payment);

    }

    @Override
    @Transactional
    public PaymentDTO save(PaymentDTO dto) {

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("PENDING");
        }

        Payment payment = convertToEntity(dto);
        Payment savedPayment = repository.save(payment);

        return convertToDTO(savedPayment);

    }

    @Override
    @Transactional
    public PaymentDTO update(Long id, PaymentDTO dto) {

        Payment payment = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Payment not found with id: " + id)
        );

        payment.setRentalId(dto.getRentalId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());

        Payment updatedPayment = repository.save(payment);

        return convertToDTO(updatedPayment);

    }

    @Override
    @Transactional
    public void delete(Long id) {

        boolean exists = repository.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }

        repository.deleteById(id);

    }

    private PaymentDTO convertToDTO(Payment payment) {

        PaymentDTO dto = new PaymentDTO();

        dto.setId(payment.getId());
        dto.setRentalId(payment.getRentalId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;

    }

    private Payment convertToEntity(PaymentDTO dto) {

        Payment payment = new Payment();

        payment.setRentalId(dto.getRentalId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(dto.getStatus());

        return payment;

    }

}
