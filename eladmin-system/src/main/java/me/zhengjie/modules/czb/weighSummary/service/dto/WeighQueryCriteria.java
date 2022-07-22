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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Date;
import java.util.List;

import me.zhengjie.annotation.Query;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @date 2022-01-07
 **/
@Data
public class WeighQueryCriteria {

    @ApiModelProperty(value = "单据类型 1：采购入库 2：销售出库")
    @Query
    private Integer billType;

    @ApiModelProperty(value = "称重日期")
    @Query
    private Date weighDate;

    @ApiModelProperty(value = "产品id")
    @Query
    private Long prodId;

    @ApiModelProperty(value = "仓库id")
    @Query
    private Long warehouseId;

    @ApiModelProperty(value = "供应商id（入库）")
    @Query
    private Long supplierId;

    @ApiModelProperty(value = "客户id（出库）")
    @Query
    private Long custId;

    @ApiModelProperty(value = "司磅员")
    @Query
    private Long operatorId;

    @ApiModelProperty(value = "磅台")
    @Query
    private String poundName;

    @ApiModelProperty(value = "操作员")
    @Query
    private String operator;

    @Query
    private String billStatus;
}