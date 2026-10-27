package com.orderflow.inventoryservice.service;

import com.orderflow.inventoryservice.event.OrderCreatedEvent;
import com.orderflow.inventoryservice.model.ProcessedEvent;
import com.orderflow.inventoryservice.repository.ProcessedEventRepository;
import com.orderflow.inventoryservice.repository.StockRepository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final StockRepository stockRepository;
    private final ProcessedEventRepository processedEventRepository;
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(StockRepository stockRepository, ProcessedEventRepository processedEventRepository) {
        this.stockRepository = stockRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event){
        if(processedEventRepository.existsById(event.eventId())){
            log.info("Evento {} gia processato", event.eventId());
            return;
        }

        event.items().forEach(item -> {
            stockRepository.upsertQuantity(item.productCode(), -item.quantity());
        });

        processedEventRepository.save(new ProcessedEvent(event.eventId()));
        log.info("Evento {} elaborato: ordine {}, {} articoli", event.eventId(), event.orderId(), event.items().size());

    }
}
