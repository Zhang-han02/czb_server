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
import me.zhengjie.modules.czb.archive.domain.Prod;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-10-29
**/
@Entity
@Data
@Table(name="czb_bill_detail")
public class BillDetail extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "单据详情id")
    private Long id;

    @Column(name = "bill_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "单据id")
    private Long billId;

    /**
     * 单据信息
     */
    @JoinColumn(name = "bill_id",insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Bill bill;

    @Column(name = "prod_id",nullable = false)
    @NotNull
    @ApiModelProperty(value = "产品id")
    private Long prodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "产品信息")
    private Prod prod;

    @Column(name = "pound_name")
    @ApiModelProperty(value = "磅台名称")
    private String poundName;

    @Column(name = "pound_bill_num")
    @ApiModelProperty(value = "磅单编号")
    private String poundBillNum;

    @Column(name = "unit")
    @ApiModelProperty(value = "单位")
    private String unit;

    @Column(name = "unit1")
    @ApiModelProperty(value = "辅助单位1")
    private String unit1;

    @Column(name = "unit2")
    @ApiModelProperty(value = "辅助单位2")
    private String unit2;

    @Column(name = "weight")
    @ApiModelProperty(value = "重量")
    private Float weight;

    @Column(name = "amount1")
    @ApiModelProperty(value = "辅助数量1")
    private String amount1;

    @Column(name = "amount2")
    @ApiModelProperty(value = "辅助数量2")
    private String amount2;

    @Column(name = "price")
    @ApiModelProperty(value = "单价(元)")
    private Float price;

    @Column(name = "amount")
    @ApiModelProperty(value = "总金额(元)")
    private Float amount;

    @Column(name = "weigh_id")
    @ApiModelProperty(value = "称重记录id")
    private Long weighId;

    public void copy(BillDetail source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}