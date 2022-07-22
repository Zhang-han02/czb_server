package me.zhengjie.modules.czb.weighSummary.model;

import lombok.Data;

/**
 * 单据明细
 */
@Data
public class PurchaseOrderDetail {
    private String pusercode;//存货编号
    private String pfullname; // 存货全名
    private String kusercode;//仓库编号
    private String kfullname;// 仓库全名
    private String billunit;//单位全名
    private Integer nunit;// 单位编号 1为基本单位 2为辅助单位1 3为辅助单位2
    private String qty;//基本单位数量 4位小数
    private String price;// 基本单位折前单价 6位小数
    private String total;//基本单位折前金额 2位小数
    private String discount;//基本单位折扣 4位小数
    private String discountprice;//基本单位折后单价 6位小数
    private String discounttotal;//基本单位折后金额 2位小数
    private String tax;//基本单位税率 2位小数
    private String taxprice;//基本单位含税单价 6位小数
    private String taxtotal;//基本单位税额 2位小数
    private String tax_total;//基本单位价税合计 2位小数
    private String price_unit;//实际单位折前单价 6位小数
    private String discountprice_unit;//实际单位折后单价 6位小数
    private String taxprice_unit;//实际单位含税单价 6位小数
    private String blockno;//批号
    private String prodate;//到期日期
    private String c1usercode;//存货自定义1编号
    private String c1fullname; // 存货自定义1全名
    private String c2usercode;//存货自定义2编号
    private String c2fullname;//存货自定义2全名
    private String custom3;//存货自定义3

    private String custom4;// 存货自定义4
    private Integer sourcevchcode;// 订单vchcode
    private String sourcevchtype;//订单vchtype
    private String sourcedlyorder;//订单dlyorder

    private Double qtyother;//副单位数量 4位小数
    private String comment;// 行摘要
    private String freedomdatedif;// 日期间隔
    private String btypeothercode;//存货多编码
    private Integer usedtype;//表格类型 本单据为1
    private Integer pdetail;// 库存类型 本单据为0
    private Integer detailsign;// 赠品标志 1为赠品
    private Serialno serialno;// 序列号数据
}
