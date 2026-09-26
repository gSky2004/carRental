package com.oop.carrental;

import com.oop.carrental.entity.Admin;
import com.oop.carrental.entity.Branch;
import com.oop.carrental.entity.Car;
import com.oop.carrental.entity.Customer;
import com.oop.carrental.repository.AdminRepository;
import com.oop.carrental.repository.BranchRepository;
import com.oop.carrental.repository.CarRepository;
import com.oop.carrental.repository.CustomerRepository;
import com.oop.carrental.service.impl.AuthServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(CarRepository carRepo, BranchRepository branchRepo, CustomerRepository customerRepo, AdminRepository adminRepo) {

        return args -> {

            if (adminRepo.count() == 0) {

                Admin admin = new Admin("admin", AuthServiceImpl.hashPassword("admin123"), "System Administrator");
                adminRepo.save(admin);

            }

            if (branchRepo.count() == 0) {

                Branch branch1 = new Branch("Main Branch", "Dar es Salaam", "123 India Street, Dar es Salaam", "+255-712-000-111");
                Branch branch2 = new Branch("Arusha Branch", "Arusha", "45 Africa Avenue, Arusha", "+255-712-000-222");

                branchRepo.save(branch1);
                branchRepo.save(branch2);

            }

            if (customerRepo.count() == 0) {

                Customer cust1 = new Customer("Juma", "Mohamed", "juma@email.com", "+255-712-345-678", "DL-2022-001", "12 Kinondoni, Dar es Salaam");
                Customer cust2 = new Customer("Aisha", "Salim", "aisha@email.com", "+255-713-456-789", "DL-2023-002", "45 Mbezi, Dar es Salaam");

                customerRepo.save(cust1);
                customerRepo.save(cust2);

            }

            if (carRepo.count() == 0) {

                Car car1 = new Car("Toyota Axio", "TZA-101X", "Toyota", "Axio", 2022, 45000.00, "Available", 1L);
                Car car2 = new Car("Honda Fit", "TZA-202Y", "Honda", "Fit", 2023, 35000.00, "Available", 1L);
                Car car3 = new Car("Suzuki Swift", "TZA-303Z", "Suzuki", "Swift", 2024, 30000.00, "Rented", 2L);

                carRepo.save(car1);
                carRepo.save(car2);
                carRepo.save(car3);

            }

        };

    }

}
