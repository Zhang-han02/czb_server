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
package me.zhengjie.modules.czb.billManage.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import me.zhengjie.base.BaseEntity;
import me.zhengjie.modules.czb.archive.domain.ClassTree;
import me.zhengjie.modules.czb.archive.domain.Cust;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import me.zhengjie.modules.czb.archive.domain.Warehouse;
import me.zhengjie.modules.system.domain.User;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-10-29
**/
@Entity
@Data
@Table(name="czb_bill")
public class Bill extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "单据id")
    private Long id;

    @Column(name = "bill_type",nullable = false)
    @NotNull
    @ApiModelProperty(value = "单据类型 1：采购入库 2：销售出库")
    private Integer billType;

    @Column(name = "bill_date",nullable = false)
    @NotNull
    @ApiModelProperty(value = "单据日期")
    private Date billDate;

    @Column(name = "bill_num",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "单据编号")
    private String billNum;

    @Column(name = "warehouse_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "仓库")
    private Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "仓库信息")
    private Warehouse warehouse;

    @Column(name = "supplier_id")
    @ApiModelProperty(value = "供应商id（入库）")
    private Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "供应商信息")
    private Supplier supplier;

    @Column(name = "cust_id")
    @ApiModelProperty(value = "客户id（出库）")
    private Long custId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "客户信息")
    private Cust cust;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by",referencedColumnName = "username",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "操作员")
    private User operator;

    /**
     * 单据详情
     */
    @OneToMany(mappedBy = "bill",cascade = {CascadeType.REMOVE})
    private List<BillDetail> detail;

    public void copy(Bill source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}