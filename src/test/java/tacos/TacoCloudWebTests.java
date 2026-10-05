package tacos;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class TacoCloudWebTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("Welcome to Taco Cloud")));
    }

    @Test
    void designPageRedirectsAnonymousUsersToLogin() throws Exception {
        mockMvc.perform(get("/design"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void designPageAllowsAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/design").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("design"))
                .andExpect(content().string(containsString("Design your taco!")));
    }

    @Test
    void ingredientsApiIsReadableWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void ingredientsApiRejectsWritesWithoutJwt() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"TEST","name":"Test Ingredient","type":"SAUCE"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ingredientsApiAllowsWritesWithScope() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_writeIngredients")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"TEST","name":"Test Ingredient","type":"SAUCE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("TEST"));

        mockMvc.perform(delete("/api/ingredients/TEST")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_deleteIngredients"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void ordersApiCreatesOrdersForAuthenticatedApiClients() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "deliveryName": "API Customer",
                                  "deliveryStreet": "Main Street",
                                  "deliveryCity": "Kyiv",
                                  "deliveryState": "UA",
                                  "deliveryZip": "01001",
                                  "ccNumber": "4111111111111111",
                                  "ccExpiration": "12/30",
                                  "ccCVV": "123",
                                  "tacos": [{
                                    "name": "API Taco",
                                    "ingredients": [{"id": "FLTO"}]
                                  }]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.deliveryName").value("API Customer"));
    }

    @Test
    void orderFormRejectsMissingDeliveryAndPaymentFields() throws Exception {
        Taco taco = new Taco();
        taco.setName("Valid Taco");
        taco.addIngredient(ingredientRepository.findById("FLTO").orElseThrow());

        TacoOrder sessionOrder = new TacoOrder();
        sessionOrder.addTaco(taco);

        mockMvc.perform(post("/orders")
                        .with(user("customer").roles("USER"))
                        .with(csrf())
                        .sessionAttr("tacoOrder", sessionOrder))
                .andExpect(status().isOk())
                .andExpect(view().name("orderForm"))
                .andExpect(content().string(containsString("Delivery name is required")))
                .andExpect(content().string(containsString("Credit card number is required")));
    }

    @Test
    void orderHistoryRendersSavedOrdersWithTacosAndIngredients() throws Exception {
        User user = userRepository.save(new User(
                "history-user",
                passwordEncoder.encode("password"),
                "History User",
                "Main Street",
                "Kyiv",
                "UA",
                "01001",
                "1234567890",
                "ROLE_USER"
        ));

        Taco taco = new Taco();
        taco.setName("History Taco");
        taco.addIngredient(ingredientRepository.findById("FLTO").orElseThrow());
        taco.addIngredient(ingredientRepository.findById("SLSA").orElseThrow());

        TacoOrder order = new TacoOrder();
        order.setUser(user);
        order.setDeliveryName("History User");
        order.setDeliveryStreet("Main Street");
        order.setDeliveryCity("Kyiv");
        order.setDeliveryState("UA");
        order.setDeliveryZip("01001");
        order.setCcNumber("4111111111111111");
        order.setCcExpiration("12/30");
        order.setCcCVV("123");
        order.addTaco(taco);
        orderRepository.save(order);

        mockMvc.perform(get("/orders").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(view().name("orderList"))
                .andExpect(content().string(containsString("History Taco")))
                .andExpect(content().string(containsString("Flour Tortilla")))
                .andExpect(content().string(containsString("Salsa")));
    }

    @Test
    void usersCanCancelTheirNewOrders() throws Exception {
        User user = userRepository.save(new User(
                "cancel-user",
                passwordEncoder.encode("password"),
                "Cancel User",
                "Main Street",
                "Kyiv",
                "UA",
                "01001",
                "1234567890",
                "ROLE_USER"
        ));

        TacoOrder order = new TacoOrder();
        order.setUser(user);
        order.setDeliveryName("Cancel User");
        order.setDeliveryStreet("Main Street");
        order.setDeliveryCity("Kyiv");
        order.setDeliveryState("UA");
        order.setDeliveryZip("01001");
        order.setCcNumber("4111111111111111");
        order.setCcExpiration("12/30");
        order.setCcCVV("123");
        Taco taco = new Taco();
        taco.setName("Cancel Taco");
        taco.addIngredient(ingredientRepository.findById("FLTO").orElseThrow());
        order.addTaco(taco);
        TacoOrder saved = orderRepository.save(order);

        mockMvc.perform(post("/orders/{id}/cancel", saved.getId())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        mockMvc.perform(get("/orders").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CANCELLED")));
    }

    @Test
    void adminCanUpdateOrderStatusFromApiWithScope() throws Exception {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Admin API");
        order.setDeliveryStreet("Main Street");
        order.setDeliveryCity("Kyiv");
        order.setDeliveryState("UA");
        order.setDeliveryZip("01001");
        order.setCcNumber("4111111111111111");
        order.setCcExpiration("12/30");
        order.setCcCVV("123");
        Taco taco = new Taco();
        taco.setName("Admin Taco");
        taco.addIngredient(ingredientRepository.findById("FLTO").orElseThrow());
        order.addTaco(taco);
        TacoOrder saved = orderRepository.save(order);

        mockMvc.perform(patch("/api/orders/{id}/status", saved.getId())
                        .param("status", "READY")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_admin"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }
}
