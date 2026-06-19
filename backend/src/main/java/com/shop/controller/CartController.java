package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.CartAddRequest;
import com.shop.dto.CartItemVO;
import com.shop.dto.CartSetCheckedRequest;
import com.shop.dto.CartUpdateQuantityRequest;
import com.shop.security.SecurityUtils;
import com.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartItemVO>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(cartService.list(userId));
    }

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CartAddRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.add(userId, req.getProductId(), req.getQuantity());
        return Result.ok();
    }

    @PutMapping("/quantity")
    public Result<Void> updateQuantity(@Valid @RequestBody CartUpdateQuantityRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.updateQuantity(userId, req.getProductId(), req.getQuantity());
        return Result.ok();
    }

    @PutMapping("/checked")
    public Result<Void> setChecked(@Valid @RequestBody CartSetCheckedRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.setChecked(userId, req.getProductId(), req.getChecked());
        return Result.ok();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> remove(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.remove(userId, productId);
        return Result.ok();
    }
}
