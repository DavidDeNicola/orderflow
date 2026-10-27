package com.orderflow.inventoryservice.repository;

import com.orderflow.inventoryservice.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface StockRepository extends JpaRepository<Stock, String> {

    @Modifying
    @Query(value = "INSERT INTO stock(product_code, available_quantity, updated_at) VALUES(:productCode, :delta, now()) on conflict(product_code) do update set available_quantity=stock.available_quantity + :delta, updated_at=excluded.updated_at", nativeQuery = true)
    void upsertQuantity(@Param("productCode") String productCode , @Param("delta") int delta);
}
