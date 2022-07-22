package me.zhengjie.modules.czb.weighSummary.model;

import lombok.Data;

/**
 * 付款数据
 */
@Data
public class PayData {
    private String ausercode;//会计科目编号
    private String afullname;//会计科目全名
    private Double total;//金额
}
