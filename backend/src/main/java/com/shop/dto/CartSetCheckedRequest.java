package com.shop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartSetCheckedRequest {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "选中状态不能为空")
    @Min(value = 0, message = "选中状态只能为0或1")
    @Max(value = 1, message = "选中状态只能为0或1")
    private Integer checked;
}
