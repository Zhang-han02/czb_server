/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.czb.weighSummary.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import me.zhengjie.base.BaseEntity;
import me.zhengjie.modules.czb.archive.domain.*;
import me.zhengjie.modules.system.domain.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @description /
 * @date 2022-01-07
 **/
@Entity
@Data
@Table(name = "czb_weigh")
public class Weigh implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "称重记录id")
    private Long id;

    @Column(name = "bill_type")
    @NotNull(message = "请上传单据类型")
    @ApiModelProperty(value = "单据类型 1：采购入库 2：销售出库")
    private String billType;

    @Column(name = "weigh_date")
    @ApiModelProperty(value = "称重日期")
    private Date weighDate;

    @Column(name = "bill_status")
    @ApiModelProperty(value = "单据状态 1：未生单 2：已生单")
    private String billStatus;

    @Column(name = "pound_name")
    @NotBlank(message = "请上传磅台名称")
    @ApiModelProperty(value = "磅台名称")
    private String poundName;

    @Column(name = "pound_bill_num")
    @NotBlank(message = "请上传磅单编号")
    @ApiModelProperty(value = "磅单编号")
    private String poundBillNum;

    @Column(name = "prod_id")
    @NotNull(message = "请上传产品id")
    @ApiModelProperty(value = "产品id")
    private Long prodId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "产品信息")
    private Prod prodInfo;

    @Column(name = "warehouse_id", nullable = false)
    @NotNull(message = "请上传仓库id")
    @ApiModelProperty(value = "仓库id")
    private Long warehouseId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "仓库信息")
    private Warehouse warehouseInfo;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "供应商id（入库）")
    private Long supplierId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "供应商信息")
    private Supplier supplierInfo;

    @Column(name = "cust_id")
    @ApiModelProperty(value = "客户id（出库）")
    private Long custId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "客户信息")
    private Cust custInfo;

    @Column(name = "operator_id")
    @NotNull(message = "请上传司磅员id")
    @ApiModelProperty(value = "司磅员id")
    private Long operatorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "司磅员信息")
    private User operatorInfo;

    @Column(name = "weight")
    @NotNull(message = "请上传重量")
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @Column(name = "unit")
    @NotBlank(message = "请上传单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    @Column(name = "price")
    @NotNull(message = "请上传单价")
    @ApiModelProperty(value = "单价(元)")
    private Float price;

    @Column(name = "amount")
    @NotNull(message = "请上传总金额")
    @ApiModelProperty(value = "总金额(元)")
    private Float amount;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;

    public void copy(Weigh source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}