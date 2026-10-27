package com.orderflow.inventoryservice.repository;

import com.orderflow.inventoryservice.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {

}
