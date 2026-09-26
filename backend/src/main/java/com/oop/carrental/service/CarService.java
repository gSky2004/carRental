package com.oop.carrental.service;

import com.oop.carrental.dto.CarDTO;

import java.util.List;

public interface CarService {

    List<CarDTO> findAll();

    CarDTO findById(Long id);

    CarDTO save(CarDTO dto);

    CarDTO update(Long id, CarDTO dto);

    void delete(Long id);

}
