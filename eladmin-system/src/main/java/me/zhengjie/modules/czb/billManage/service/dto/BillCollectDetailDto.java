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

import lombok.Data;
import me.zhengjie.base.BaseDTO;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-10-29
**/
@Data
public class BillCollectDetailDto implements Serializable {


    /** 单据类型 1：采购入库 2：销售出库 */
    private Integer billType;

    /** 单据日期 */
    private Date billDate;

    /** 单据编号 */
    private String billNum;

    /** 仓库 */
    private Long warehouseId;

    private String warehouseName;

    /** 供应商id */
    private Long supplierId;

    private String supplierName;

    private Long custId;

    private String custName;

    private String operatorUserName;

    private String operatorName;



    /** 单据详情id */
    private Long id;

    /** 单据id */
    private Long billId;


    /** 产品id */
    private Long prodId;

    private String prodName;

    /** 磅台名称 */
    private String poundName;

    /** 磅单编号 */
    private String poundBillNum;

    /** 单位 */
    private String unit;

    /** 辅助单位1 */
    private String unit1;

    /** 辅助单位2 */
    private String unit2;

    /** 重量 */
    private Float weight;

    /** 辅助数量1 */
    private String amount1;

    /** 辅助数量2 */
    private String amount2;

}