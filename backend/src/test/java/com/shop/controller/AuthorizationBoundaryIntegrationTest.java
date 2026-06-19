package com.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.entity.OrderMain;
import com.shop.entity.User;
import com.shop.mapper.OrderMainMapper;
import com.shop.mapper.UserMapper;
import com.shop.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 权限边界集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("权限边界集成测试")
class AuthorizationBoundaryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMainMapper orderMainMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User normalUser;
    private User adminUser;
    private User orderOwner;
    private String normalUserToken;
    private String adminToken;
    private String orderOwnerToken;
    private OrderMain testOrder;

    @BeforeEach
    void setUp() {
        normalUser = createUser("normal_" + System.currentTimeMillis(), "user");
        adminUser = createUser("admin_" + System.currentTimeMillis(), "admin");
        orderOwner = createUser("owner_" + System.currentTimeMillis(), "user");

        normalUserToken = jwtService.generateToken(normalUser.getId());
        adminToken = jwtService.generateToken(adminUser.getId());
        orderOwnerToken = jwtService.generateToken(orderOwner.getId());

        testOrder = createOrder(orderOwner.getId());
    }

    private User createUser(String username, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setNickname(username + "_nick");
        user.setStatus(1);
        user.setRole(role);
        userMapper.insert(user);
        return user;
    }

    private OrderMain createOrder(Long userId) {
        OrderMain order = new OrderMain();
        order.setOrderNo("T" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                String.format("%04d", (int) (Math.random() * 10000)));
        order.setUserId(userId);
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setStatus(0);
        order.setReceiverName("测试收货人");
        order.setReceiverPhone("13800138000");
        order.setReceiverAddress("测试地址");
        orderMainMapper.insert(order);
        return orderMainMapper.selectById(order.getId());
    }

    @Test
    @DisplayName("普通用户 Token 访问 /admin/users 返回 403")
    void normalUser_accessAdminUsers_returns403() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + normalUserToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("权限不足"));
    }

    @Test
    @DisplayName("管理员 Token 可正常访问 /admin/users")
    void admin_accessAdminUsers_success() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("用户 Token 访问他人订单详情返回 404")
    void user_accessOthersOrderDetail_returns404() throws Exception {
        mockMvc.perform(get("/orders/" + testOrder.getId())
                        .header("Authorization", "Bearer " + normalUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("订单不存在"));
    }

    @Test
    @DisplayName("订单所有者可正常访问自己的订单详情")
    void orderOwner_accessOwnOrderDetail_success() throws Exception {
        mockMvc.perform(get("/orders/" + testOrder.getId())
                        .header("Authorization", "Bearer " + orderOwnerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.data.userId").value(orderOwner.getId()));
    }
}
