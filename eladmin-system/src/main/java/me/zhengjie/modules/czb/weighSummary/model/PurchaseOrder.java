package me.zhengjie.modules.czb.weighSummary.model;

import lombok.Data;

import java.util.List;

/**
 * 单据表头
 */
@Data
public class PurchaseOrder {
    private String date; // 单据日期
    private String summary; // 摘要
    private String busercode; // 供应商编号
    private String bfullname; // 供应商全名
    private String settlebusercode; //结算单位编号
    private String settlebfullname;//结算单位全名
    private String eusercode;// 否-经办人编号
    private String efullname;// 否-经办人全名
    private String kusercode;// 仓库编号
    private String kfullname;// 仓库全名
    private String dusercode; // 否-部门编号
    private String dfullname; // 否-部门全名
    private String gatheringdate; // 否-付款日期
    private String inputno; // 制单人全名
    private Integer billtype;//单据处理类型 本单据为0
    private float billtotal;// 单据含税金额合计 - 抹零金额
    private String difatypename;//抹零类型

    private String musercode;// 否-项目编号
    private String mfullname;// 否-项目全名
    private String LogOnDate;//登陆日期
    private List<PurchaseOrderDetail> detail;//单据明细
    private List<PayData> settle;//付款明细

}
