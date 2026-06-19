package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.OrderCreateRequest;
import com.shop.entity.OrderItem;
import com.shop.entity.OrderMain;
import com.shop.security.SecurityUtils;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderMain> create(@Valid @RequestBody OrderCreateRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderMain order = orderService.create(userId, req);
        return Result.ok(order);
    }

    @GetMapping
    public Result<List<OrderMain>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(orderService.listByUserId(userId));
    }

    @GetMapping("/{id}")
    public Result<OrderMain> getById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderMain order = orderService.getById(id, userId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        return Result.ok(order);
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> listItems(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderMain order = orderService.getById(id, userId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        return Result.ok(orderService.listItems(id));
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.pay(id, userId);
        return Result.ok();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancel(id, userId);
        return Result.ok();
    }
}
