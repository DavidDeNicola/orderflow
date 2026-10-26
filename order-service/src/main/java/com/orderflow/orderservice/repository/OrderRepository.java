package com.orderflow.orderservice.repository;

import com.orderflow.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    @Query(value = "SELECT * FROM orders WHERE details->'shipping'->>'city' = :city", nativeQuery = true)
    List<Order> findByCity(@Param("city") String city);

    @Query(value = """
        SELECT * FROM orders
        WHERE details @> jsonb_build_object(
                'items', jsonb_build_array(
                    jsonb_build_object('productCode', :productCode)))
        """, nativeQuery = true)
    List<Order> findByProductCode(@Param("productCode") String productCode);
}
