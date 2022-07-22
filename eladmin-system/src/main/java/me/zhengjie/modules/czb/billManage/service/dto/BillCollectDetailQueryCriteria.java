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
package me.zhengjie.modules.czb.billManage.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.annotation.Query;

import java.sql.Date;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-10-29
**/
@Data
public class BillCollectDetailQueryCriteria {

    @ApiModelProperty(value = "单据类型 1：采购入库 2：销售出库")
    @Query(joinName = "bill",propName = "billType")
    private Integer billType;

    @ApiModelProperty(value = "单据日期")
    @Query(joinName = "bill",propName = "billDate")
    private Date billDate;

    @ApiModelProperty(value = "产品id")
    @Query
    private Long prodId;

    @ApiModelProperty(value = "仓库id")
    @Query(joinName = "bill",propName = "warehouseId")
    private Long warehouseId;

    @ApiModelProperty(value = "供应商id（入库）")
    @Query(joinName = "bill",propName = "supplierId")
    private Long supplierId;

    @ApiModelProperty(value = "客户id（出库）")
    @Query(joinName = "bill",propName = "custId")
    private Long custId;

    @ApiModelProperty(value = "操作员")
    @Query(joinName = "bill",propName = "updateBy")
    private String operator;
}