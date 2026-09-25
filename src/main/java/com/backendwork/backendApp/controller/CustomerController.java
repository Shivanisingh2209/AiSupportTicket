package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.entity.Customer;
import com.backendwork.backendApp.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {

        Customer createdCustomer = customerService.createCustomer(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();

        return ResponseEntity.ok(customers);
    }
}
