package com.oop.carrental.service;

import com.oop.carrental.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {

    List<PaymentDTO> findAll();

    PaymentDTO findById(Long id);

    PaymentDTO save(PaymentDTO dto);

    PaymentDTO update(Long id, PaymentDTO dto);

    void delete(Long id);

}
