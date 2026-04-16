package com.personal.ledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotBlank(message = "分类名称不能为空")
    private String name;
    @NotNull(message = "分类类型不能为空")
    private Integer type;
    @NotBlank(message = "分类图标不能为空")
    private String icon;
    private Integer sort;
}
