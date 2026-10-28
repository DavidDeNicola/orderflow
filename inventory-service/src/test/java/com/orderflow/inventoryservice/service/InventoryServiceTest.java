package com.orderflow.inventoryservice.service;

import com.orderflow.inventoryservice.event.OrderCreatedEvent;
import com.orderflow.inventoryservice.model.ProcessedEvent;
import com.orderflow.inventoryservice.model.Stock;
import com.orderflow.inventoryservice.repository.ProcessedEventRepository;
import com.orderflow.inventoryservice.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka
@Transactional
public class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @BeforeEach
    void setUp() {
        stockRepository.save(new Stock("KB-001", 100));
        processedEventRepository.save(new ProcessedEvent("evt-1"));
    }

    @Test
    void handleOrderCreated_ignoresAlreadyProcessedEvent() {

        //arrange
        OrderCreatedEvent duplicate = new OrderCreatedEvent(
                "evt-1", 1L, "CUST-A",
                List.of(new OrderCreatedEvent.ItemEvent("KB-001", 5)),
                Instant.now());

        //act
        inventoryService.handleOrderCreated(duplicate);

        //assert
        Stock stock = stockRepository.findById("KB-001").orElseThrow(() -> new RuntimeException("Stock not found"));

        assertThat(stock.getAvailableQuantity()).isEqualTo(100);
        assertThat(processedEventRepository.count()).isEqualTo(1);
    }

    @Disabled("Query nativa UPSERT (ON CONFLICT): H2 non la supporta. Verificata manualmente su Postgres in T5.")
    @Test
    void handleOrderCreated_decreasesStockForNewEvent() {

        //arrange
        OrderCreatedEvent event = new OrderCreatedEvent(
                "evt-2", 2L, "CUST-A",
                List.of(new OrderCreatedEvent.ItemEvent("KB-001", 5)),
                Instant.now());

        //act
        inventoryService.handleOrderCreated(event);

        //assert
        Stock stock = stockRepository.findById("KB-001").orElseThrow(() -> new RuntimeException("Stock not found"));

        assertThat(stock.getAvailableQuantity()).isEqualTo(95);
        assertThat(processedEventRepository.count()).isEqualTo(2);
    }
}
