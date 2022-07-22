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
package me.zhengjie.modules.czb.weighSummary.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.base.BaseDTO;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import me.zhengjie.modules.czb.archive.domain.Warehouse;
import me.zhengjie.modules.czb.archive.service.dto.*;
import me.zhengjie.modules.system.service.dto.UserMiniDto;

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
@Data
public class WeighDto implements Serializable {

    /**
     * 称重记录id
     */
    private Long id;

    /**
     * 单据类型 1：采购入库 2：销售出库
     */
    private String billType;

    /**
     * 称重日期
     */
    private Date weighDate;

    /**
     * 单据状态 1：未生单 2：已生单
     */
    private String billStatus;

    /**
     * 磅台名称
     */
    private String poundName;

    /**
     * 磅单编号
     */
    private String poundBillNum;

    /**
     * 产品id
     */
    private Long prodId;

    private ProdMiniDto prodInfo;
    /**
     * 仓库id
     */
    private Long warehouseId;

    private WarehouseMiniDto warehouseInfo;

    /**
     * 供应商id（入库）
     */
    private Long supplierId;

    private SupplierMiniDto supplierInfo;

    /**
     * 客户id（出库）
     */
    private Long custId;

    private CustMiniDto custInfo;

    /**
     * 司磅员id
     */
    private Long operatorId;
    private UserMiniDto operatorInfo;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 单位
     */
    private String unit;

    @ApiModelProperty(value = "单价(元)")
    private Float price;

    @ApiModelProperty(value = "总金额(元)")
    private Float amount;

    private Timestamp createTime;
}