package com.oop.carrental.service;

import com.oop.carrental.dto.RentalDTO;

import java.util.List;

public interface RentalService {

    List<RentalDTO> findAll();

    RentalDTO findById(Long id);

    RentalDTO rentCar(RentalDTO dto);

    RentalDTO update(Long id, RentalDTO dto);

    void delete(Long id);

}
