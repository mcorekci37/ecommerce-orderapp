package com.emce.ecommerce.integration;

import com.emce.ecommerce.common.domain.config.MessageConfig;
import com.emce.ecommerce.common.domain.config.MessageConstants;
import com.emce.ecommerce.common.domain.valueobjects.Money;
import com.emce.ecommerce.order.domain.config.OrderRestrictions;
import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.repository.OrderRepository;
import com.emce.ecommerce.order.domain.valueobjects.OrderId;
import com.emce.ecommerce.order.domain.valueobjects.OrderStatus;
import com.emce.ecommerce.product.domain.entity.Category;
import com.emce.ecommerce.product.domain.entity.Product;
import com.emce.ecommerce.product.domain.entity.Seller;
import com.emce.ecommerce.product.domain.repository.ProductRepository;
import com.emce.ecommerce.product.domain.valueobjects.ProductId;
import com.emce.ecommerce.product.domain.valueobjects.SellerId;
import com.emce.ecommerce.security.customer.application.service.CustomerService;
import com.emce.ecommerce.security.customer.domain.entity.Customer;
import com.emce.ecommerce.security.customer.domain.repository.CustomerRepository;
import com.emce.ecommerce.security.customer.web.dto.AuthRequest;
import com.emce.ecommerce.security.customer.web.dto.AuthResponse;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import static com.emce.ecommerce.common.domain.config.MessageConstants.MSG_CANNOT_CANCEL_OTHER_USERS_ORDER;
import static com.emce.ecommerce.order.TestUtil.TEST_USER;
import static com.emce.ecommerce.order.web.controller.OrderController.API_BASE_PATH;
import static com.emce.ecommerce.order.web.controller.OrderController.CANCEL_PATH;
import static com.emce.ecommerce.order.web.controller.OrderController.CREATE_ORDER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    public static final String CONTENT_TYPE = "application/json";
    public static final int PRODUCT_ID = 9999;
    public static final long STOCK = 100L;
    public static final String EMAIL = TEST_USER + "@ecommerce.com";
    public static final String PASSWORD = "password";
    public static final String ORDER_CREATED = "order-created";
    public String TOKEN;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessageConfig messageConfig;
    @Autowired
    private OrderRestrictions restrictions;
    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;


    @BeforeEach
    public void init() {
        initDb();
    }

    private void initDb() {
        TOKEN = createCustomerAndLogin(TEST_USER, EMAIL, PASSWORD);
        Category testCategory = new Category(1, "test category");
        Product product = new Product(PRODUCT_ID, "test product", testCategory, new Seller(new SellerId(1)), Money.of(15000.0), 100L);
        productRepository.save(product);
    }

    private String createCustomerAndLogin(String name, String email, String password) {
        Customer customer = new Customer(name, email, passwordEncoder.encode(password));
        customerRepository.save(customer);
        AuthResponse login = customerService.login(new AuthRequest(email, password));
        return login.token();
    }

    @Test
    public void createOrder_success() throws Exception {
        int quantity = 2;
        int price = 1000;

        String orderId = createOrder(quantity, price);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertEquals(topicCaptor.getValue(), ORDER_CREATED);
        assertThat(messageCaptor.getValue().contains(EMAIL));
        assertThat(messageCaptor.getValue().contains(String.valueOf(PRODUCT_ID)));

        Optional<Order> byOrderId = orderRepository.findByOrderId(new OrderId(orderId));
        assertTrue(byOrderId.isPresent());

        Optional<Product> product = productRepository.findByProductId(new ProductId(PRODUCT_ID));
        product.ifPresent(value -> assertEquals(STOCK - quantity, value.getStock()));
    }

    @Test
    public void createOrder_shouldThrowProductNotFound() throws Exception {
        int quantity = 2;
        int price = 1000;

        int productId = 999;
        String createOrderCommandJson = getCreateOrderJsonStr(quantity, price, productId);

        MvcResult mvcResult = performPost(createOrderCommandJson)
                .andExpect(status().is4xxClientError())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(messageConfig.getMessage(MessageConstants.MSG_PRODUCT_NOT_FOUND, productId), response);

        Optional<Product> product = productRepository.findByProductId(new ProductId(PRODUCT_ID));
        product.ifPresent(value -> assertEquals(STOCK, value.getStock()));
    }

    @Test
    public void createOrder_shouldThrowQuantityExceeded() throws Exception {
        int quantity = 20;
        int price = 1000;

        String createOrderCommandJson = getCreateOrderJsonStr(quantity, price, PRODUCT_ID);

        MvcResult mvcResult = performPost(createOrderCommandJson)
                .andExpect(status().is4xxClientError())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(messageConfig.getMessage(MessageConstants.MSG_ORDER_QUANTITY_EXCEEDED, quantity, restrictions.getMaximumAllowedQuantity()), response);

        Optional<Product> product = productRepository.findByProductId(new ProductId(PRODUCT_ID));
        product.ifPresent(value -> assertEquals(STOCK, value.getStock()));
    }

    @Test
    public void createOrder_shouldThrowPriceExceeded() throws Exception {
        int quantity = 2;
        int price = 100000000;

        String createOrderCommandJson = getCreateOrderJsonStr(quantity, price, PRODUCT_ID);

        MvcResult mvcResult = performPost(createOrderCommandJson)
                .andExpect(status().is4xxClientError())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(messageConfig.getMessage(MessageConstants.MSG_ORDER_PRICE_EXCEEDED, price, restrictions.getMaximumAllowedPrice()), response);

        Optional<Product> product = productRepository.findByProductId(new ProductId(PRODUCT_ID));
        product.ifPresent(value -> assertEquals(STOCK, value.getStock()));
    }


    @Test
    public void cancelOrder_success() throws Exception {
        String orderId = createOrder(2, 100000);

        performCancel(orderId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(EMAIL))
                .andExpect(jsonPath("$.orderId").isString())
                .andExpect(jsonPath("$.orderId").value(Matchers.hasLength(36)))
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.CANCELED.toString())).andReturn();

        // Verify send was called twice
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(2)).send(topicCaptor.capture(), messageCaptor.capture());

        // Get all captured arguments
        List<String> capturedTopics = topicCaptor.getAllValues();
        List<String> capturedMessages = messageCaptor.getAllValues();

        assertEquals(2, capturedTopics.size());
        assertEquals("order-created", capturedTopics.get(0));
        assertEquals("order-cancelled", capturedTopics.get(1));
        assertThat(capturedMessages.get(0).contains(EMAIL));
        assertThat(capturedMessages.get(0).contains(String.valueOf(PRODUCT_ID)));
        assertThat(capturedMessages.get(1).contains(EMAIL));
        assertThat(capturedMessages.get(1).contains(String.valueOf(PRODUCT_ID)));
        assertThat(capturedMessages.get(1).contains(String.valueOf(OrderStatus.CREATED.toString())));
    }

    @Test
    public void cancelOrder_shouldThrowOtherUsersOrder() throws Exception {
        Product product = new Product(10,"product", new Category("test"), new Seller(new SellerId(1)), Money.of(100),100L);
        Order order = new Order("another user", product, 2, Money.of(200));
        Order otherUsersOrder = orderRepository.save(order);
        String orderId = otherUsersOrder.getId().getValue();

        MvcResult mvcResult = performCancel(orderId)
                .andExpect(status().is4xxClientError())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals(messageConfig.getMessage(MSG_CANNOT_CANCEL_OTHER_USERS_ORDER), contentAsString);

    }

    private String createOrder(int quantity, int price) throws Exception {
        String createOrderCommandJson = getCreateOrderJsonStr(quantity, price, PRODUCT_ID);

        MvcResult mvcResult = performPost(createOrderCommandJson)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(EMAIL))
                .andExpect(jsonPath("$.orderId").isString())
                .andExpect(jsonPath("$.orderId").value(Matchers.hasLength(36)))
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.quantity").value(quantity))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.CREATED.toString())).andReturn();

        return JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.orderId");
    }

    private ResultActions performPost(String createOrderCommandJson) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(TOKEN);
        return mockMvc
                .perform(
                        post(API_BASE_PATH + CREATE_ORDER_PATH)
                                .headers(httpHeaders)
                                .contentType(CONTENT_TYPE)
                                .content(createOrderCommandJson));
    }


    private ResultActions performCancel(String orderId) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(TOKEN);
        return mockMvc
                .perform(
                        delete(API_BASE_PATH + CANCEL_PATH + "/" + orderId)
                                .headers(httpHeaders)
                                .contentType(CONTENT_TYPE));
    }

    private static String getCreateOrderJsonStr(int quantity, int price, int productId) {
        return String.format(Locale.US, "{\n" +
                "    \"productId\": %d,\n" +
                "    \"quantity\": %d,\n" +
                "    \"totalAmount\": %.2f\n" +
                "}", productId, quantity, BigDecimal.valueOf(price));
    }
}
