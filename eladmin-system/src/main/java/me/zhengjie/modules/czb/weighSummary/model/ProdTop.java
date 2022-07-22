package me.zhengjie.modules.czb.weighSummary.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdTop {
    private String prod_name;
    private Integer total;
    private BigDecimal weightTotal;
    private BigDecimal average;
}
