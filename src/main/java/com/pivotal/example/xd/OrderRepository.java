package com.pivotal.example.xd;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, String> {

    Order findByTransactionId(String name);
    
    Iterable<Order> findByCreditCardType(String creditCardType);
    
    Iterable<Order> findByZip(int zip);

    Iterable<Order> findByState(String state);

//    Iterable<Order> findByAgeGreaterThanAndAgeLessThan(int age1, int age2);
    
    Order save(Order order);
}
