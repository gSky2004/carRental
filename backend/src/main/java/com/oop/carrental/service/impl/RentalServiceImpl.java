package com.oop.carrental.service.impl;

import com.oop.carrental.dto.PaymentDTO;
import com.oop.carrental.dto.RentalDTO;
import com.oop.carrental.entity.Car;
import com.oop.carrental.entity.Payment;
import com.oop.carrental.entity.Rental;
import com.oop.carrental.exception.ResourceNotFoundException;
import com.oop.carrental.repository.CarRepository;
import com.oop.carrental.repository.PaymentRepository;
import com.oop.carrental.repository.RentalRepository;
import com.oop.carrental.service.RentalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepo;
    private final CarRepository carRepo;
    private final PaymentRepository paymentRepo;

    public RentalServiceImpl(RentalRepository rentalRepo, CarRepository carRepo, PaymentRepository paymentRepo) {
        this.rentalRepo = rentalRepo;
        this.carRepo = carRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    public List<RentalDTO> findAll() {

        List<Rental> rentalList = rentalRepo.findAll();
        List<RentalDTO> dtoList = new ArrayList<>();

        for (Rental rental : rentalList) {

            RentalDTO dto = convertToDTO(rental);
            dtoList.add(dto);

        }

        return dtoList;

    }

    @Override
    public RentalDTO findById(Long id) {

        Rental rental = rentalRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rental not found with id: " + id)
        );

        return convertToDTO(rental);

    }

    @Override
    @Transactional
    public RentalDTO rentCar(RentalDTO dto) {

        Car car = carRepo.findById(dto.getCarId()).orElseThrow(
                () -> new ResourceNotFoundException("Car not found with id: " + dto.getCarId())
        );

        String carStatus = car.getStatus();

        if (!carStatus.equals("Available")) {

            throw new IllegalStateException("Car is not available for rent");

        }

        car.setStatus("Rented");
        carRepo.save(car);

        Rental rental = new Rental(
                car.getId(),
                dto.getCustomerId(),
                car.getCarName(),
                car.getPlateNumber(),
                dto.getCustomerName(),
                dto.getCustomerPhone(),
                dto.getPickupDate(),
                dto.getReturnDate(),
                dto.getRentalDays(),
                car.getRentalPricePerDay(),
                dto.getTotalCost()
        );

        Rental savedRental = rentalRepo.save(rental);

        Payment payment = new Payment();
        payment.setRentalId(savedRental.getId());
        payment.setAmount(dto.getTotalCost());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("CASH");
        payment.setStatus("PENDING");
        paymentRepo.save(payment);

        return convertToDTO(savedRental);

    }

    @Override
    @Transactional
    public RentalDTO update(Long id, RentalDTO dto) {

        Rental rental = rentalRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rental not found with id: " + id)
        );

        rental.setCustomerId(dto.getCustomerId());
        rental.setCustomerName(dto.getCustomerName());
        rental.setCustomerPhone(dto.getCustomerPhone());
        rental.setPickupDate(dto.getPickupDate());
        rental.setReturnDate(dto.getReturnDate());
        rental.setRentalDays(dto.getRentalDays());
        rental.setTotalCost(dto.getTotalCost());

        Rental updatedRental = rentalRepo.save(rental);

        return convertToDTO(updatedRental);

    }

    @Override
    @Transactional
    public void delete(Long id) {

        boolean exists = rentalRepo.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("Rental not found with id: " + id);
        }

        rentalRepo.deleteById(id);

    }

    private RentalDTO convertToDTO(Rental rental) {

        RentalDTO dto = new RentalDTO();

        dto.setId(rental.getId());
        dto.setCarId(rental.getCarId());
        dto.setCustomerId(rental.getCustomerId());
        dto.setCarName(rental.getCarName());
        dto.setPlateNumber(rental.getPlateNumber());
        dto.setCustomerName(rental.getCustomerName());
        dto.setCustomerPhone(rental.getCustomerPhone());
        dto.setPickupDate(rental.getPickupDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setRentalDays(rental.getRentalDays());
        dto.setPricePerDay(rental.getPricePerDay());
        dto.setTotalCost(rental.getTotalCost());
        dto.setCreatedAt(rental.getCreatedAt());

        return dto;

    }

}
