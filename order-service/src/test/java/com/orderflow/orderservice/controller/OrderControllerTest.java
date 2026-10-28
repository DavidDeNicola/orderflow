package com.orderflow.orderservice.controller;

import com.orderflow.orderservice.model.Order;
import com.orderflow.orderservice.model.OrderStatus;
import com.orderflow.orderservice.model.details.OrderDetails;
import com.orderflow.orderservice.model.details.OrderItem;
import com.orderflow.orderservice.model.details.ShippingAddress;
import com.orderflow.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        OrderDetails details = new OrderDetails(
                new ShippingAddress("Via Roma 1", "Brindisi", "72100"),
                List.of(new OrderItem("KB-001", 1, new BigDecimal("49.90"))),
                false,
                null);

        orderRepository.save(new Order("CUST-A", OrderStatus.CREATED, new BigDecimal("49.90"), details));
        orderRepository.save(new Order("CUST-A", OrderStatus.CREATED, new BigDecimal("49.90"), details));
        orderRepository.save(new Order("CUST-B", OrderStatus.CREATED, new BigDecimal("49.90"), details));
    }

    @Test
    void createOrder_returns201AndCalculatesTotal() throws Exception {
        String body = """
                    {
                        "customerId" : "CUST-TEST",
                        "shipping" : { "street": "Via Roma 1", "city": "Brindisi", "zip": "72100" },
                        "items": [
                            { "productCode": "KB-001", "quantity": 2, "unitPrice": 49.90 },
                            { "productCode": "MS-002", "quantity": 1, "unitPrice": 25.50 }
                        ]
                    }
                    """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value("CUST-TEST"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(125.30));
    }

    @Test
    void createOrder_returns400WhenItemsAreEmpty() throws Exception {
        String body = """
                    {
                        "customerId" : "CUST-TEST",
                        "shipping" : { "street": "Via Roma 1", "city": "Brindisi", "zip": "72100" },
                        "items": []
                    }
                    """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersByCustomer_returnsOnlyThatCustomerOrders() throws Exception {
        mockMvc.perform(get("/orders")
                .param("customerId", "CUST-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value("CUST-A"))
                .andExpect(jsonPath("$[1].customerId").value("CUST-A"));
    }

    @Test
    void getOrdersByDetails_returns400WhenBothParamsArePresent() throws  Exception {
        mockMvc.perform(get("/orders/search")
                .param("city", "Brindisi")
                .param("productCode", "KB-001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersByDetails_returns400WhenNoParamsArePresent() throws Exception {
        mockMvc.perform(get("/orders/search"))
                .andExpect(status().isBadRequest());
    }

    @Disabled("Query nativa JSONB (->>): H2 non supporta gli operatori JSON di PostgreSQL. Verificata su Postgres reale.")
    @Test
    void getOrdersByDetails_returnsOrdersByCity() throws Exception {
        mockMvc.perform(get("/orders/search")
                .param("city", "Brindisi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
