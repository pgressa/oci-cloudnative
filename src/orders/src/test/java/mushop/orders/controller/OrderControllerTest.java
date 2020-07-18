package mushop.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mushop.orders.controllers.OrdersController;
import mushop.orders.entities.Address;
import mushop.orders.entities.Card;
import mushop.orders.entities.Customer;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.resources.NewOrderResource;
import mushop.orders.services.OrdersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersService ordersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void orderPayload_returns_201() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(URI.create("http://user/customers/1"),
                URI.create("http://user/customers/1/addresses/1"),
                URI.create("http://user/customers/1/cards/1"),
                URI.create("http://carts/carts/1/items"));
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,customer,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenReturn(order);

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void missingCustomer_returns_406() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(null,
                URI.create("http://user/customers/1/addresses/1"),
                URI.create("http://user/customers/1/cards/1"),
                URI.create("http://carts/carts/1/items"));
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,null,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenReturn(order);

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void missingAddress_returns_406() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(URI.create("http://user/customers/1"),
                null,
                URI.create("http://user/customers/1/cards/1"),
                URI.create("http://carts/carts/1/items"));
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,null,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenReturn(order);

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void missingCard_returns_406() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(URI.create("http://user/customers/1"),
                URI.create("http://user/customers/1/addresses/1"),
                null,
                URI.create("http://carts/carts/1/items"));
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,null,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenReturn(order);

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void missingCartItems_returns_406() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(URI.create("http://user/customers/1"),
                URI.create("http://user/customers/1/addresses/1"),
                URI.create("http://user/customers/1/cards/1"),
                null);
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,null,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenReturn(order);

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void paymentDeclined_returns_406() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(URI.create("http://user/customers/1"),
                URI.create("http://user/customers/1/addresses/1"),
                URI.create("http://user/customers/1/cards/1"),
                null);
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,null,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenThrow(new OrdersController.PaymentDeclinedException("test"));

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void illegalState_returns_406() throws Exception {
        NewOrderResource orderPayload = new NewOrderResource(URI.create("http://user/customers/1"),
                URI.create("http://user/customers/1/addresses/1"),
                URI.create("http://user/customers/1/cards/1"),
                null);
        Address address = new Address("001", "000", "street", "city", "postcode", "country");
        Card card = new Card("001", "0000000000000000", "00/00", "000");
        Customer customer = new Customer("001", "firstname", "lastName", "username", Arrays.asList(address), Arrays.asList(card));

        CustomerOrder order = new CustomerOrder(001l,null,address,card, null,null,null,00f);
        when(ordersService.createNewOrder(orderPayload)).thenThrow(new IllegalStateException("test"));

        this.mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderPayload))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable());
    }


}
