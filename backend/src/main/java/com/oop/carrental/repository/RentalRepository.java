package com.oop.carrental.repository;

import com.oop.carrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByCarId(Long carId);

    @Modifying
    @Query("UPDATE Rental r SET r.carId = NULL WHERE r.carId = :carId")
    void detachCarId(Long carId);

}
