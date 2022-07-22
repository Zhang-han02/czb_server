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
import me.zhengjie.modules.czb.archive.service.dto.CustMiniDto;
import me.zhengjie.modules.czb.archive.service.dto.SupplierMiniDto;
import me.zhengjie.modules.czb.archive.service.dto.WarehouseMiniDto;
import me.zhengjie.modules.system.service.dto.UserMiniDto;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description /
 * @date 2021-10-29
 **/
@Data
public class BillDto extends BaseDTO implements Serializable {

    /**
     * 单据id
     */
    private Long id;

    /**
     * 单据类型 1：采购入库 2：销售出库
     */
    private Integer billType;

    /**
     * 单据日期
     */
    private Date billDate;

    /**
     * 单据编号
     */
    private String billNum;

    /**
     * 仓库
     */
    private Long warehouseId;
    private WarehouseMiniDto warehouse;

    /**
     * 供应商
     */
    private Long supplierId;
    private SupplierMiniDto supplier;

    /*客户*/
    private Long custId;
    private CustMiniDto cust;

    /*操作员*/
    private Long operatorId;
    private UserMiniDto operator;

    /**
     * 单据详情
     */
    private List<BillDetailDto> detail;

}