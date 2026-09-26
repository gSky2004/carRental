package com.oop.carrental.service;

import com.oop.carrental.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> findAll();

    CustomerDTO findById(Long id);

    CustomerDTO save(CustomerDTO dto);

    CustomerDTO update(Long id, CustomerDTO dto);

    void delete(Long id);

}
