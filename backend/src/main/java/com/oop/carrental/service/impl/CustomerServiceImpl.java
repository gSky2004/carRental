package com.oop.carrental.service.impl;

import com.oop.carrental.dto.CustomerDTO;
import com.oop.carrental.entity.Customer;
import com.oop.carrental.exception.ResourceNotFoundException;
import com.oop.carrental.repository.CustomerRepository;
import com.oop.carrental.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CustomerDTO> findAll() {

        List<Customer> customerList = repository.findAll();
        List<CustomerDTO> dtoList = new ArrayList<>();

        for (Customer customer : customerList) {

            CustomerDTO dto = convertToDTO(customer);
            dtoList.add(dto);

        }

        return dtoList;

    }

    @Override
    public CustomerDTO findById(Long id) {

        Customer customer = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with id: " + id)
        );

        return convertToDTO(customer);

    }

    @Override
    @Transactional
    public CustomerDTO save(CustomerDTO dto) {

        Customer customer = convertToEntity(dto);
        Customer savedCustomer = repository.save(customer);

        return convertToDTO(savedCustomer);

    }

    @Override
    @Transactional
    public CustomerDTO update(Long id, CustomerDTO dto) {

        Customer customer = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with id: " + id)
        );

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setLicenseNumber(dto.getLicenseNumber());
        customer.setAddress(dto.getAddress());

        Customer updatedCustomer = repository.save(customer);

        return convertToDTO(updatedCustomer);

    }

    @Override
    @Transactional
    public void delete(Long id) {

        boolean exists = repository.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }

        repository.deleteById(id);

    }

    private CustomerDTO convertToDTO(Customer customer) {

        CustomerDTO dto = new CustomerDTO();

        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setLicenseNumber(customer.getLicenseNumber());
        dto.setAddress(customer.getAddress());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;

    }

    private Customer convertToEntity(CustomerDTO dto) {

        Customer customer = new Customer();

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setLicenseNumber(dto.getLicenseNumber());
        customer.setAddress(dto.getAddress());

        return customer;

    }

}
