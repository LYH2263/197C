package com.shop.controller;

import com.shop.common.Result;
import com.shop.dto.ReviewRequest;
import com.shop.dto.ReviewVO;
import com.shop.entity.Review;
import com.shop.security.SecurityUtils;
import com.shop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Result<Review> create(@Valid @RequestBody ReviewRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = reviewService.create(userId, req);
        return Result.ok(review);
    }

    @GetMapping("/product/{productId}")
    public Result<List<Review>> listByProduct(@PathVariable Long productId) {
        return Result.ok(reviewService.listByProductId(productId));
    }

    @GetMapping("/me")
    public Result<List<ReviewVO>> myReviews() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(reviewService.listByUserId(userId));
    }
}
