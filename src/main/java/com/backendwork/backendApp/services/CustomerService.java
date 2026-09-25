package com.backendwork.backendApp.services;


import com.backendwork.backendApp.entity.Customer;
import com.backendwork.backendApp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    public boolean customerExists(String customerId) {
        return customerRepository.existsById(customerId);
    }

}
