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
package me.zhengjie.modules.czb.billManage.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.czb.billManage.domain.Bill;
import me.zhengjie.modules.czb.billManage.domain.BillDetail;
import me.zhengjie.modules.czb.billManage.service.dto.BillCollectDetailDto;
import me.zhengjie.modules.czb.billManage.service.dto.BillDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-10-29
**/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillDetailMapper extends BaseMapper<BillDetailDto, BillDetail> {

    @Override
    BillDetailDto toDto(BillDetail entity);

    @Mappings({
            @Mapping(source = "billDetail.id",target = "id"),
            @Mapping(source = "billDetail.prod.name",target = "prodName"),
            @Mapping(source = "bill.warehouse.name",target = "warehouseName"),
            @Mapping(source = "bill.supplier.name",target = "supplierName"),
            @Mapping(source = "bill.cust.name",target = "custName"),
            @Mapping(source = "bill.operator.nickName",target = "operatorName"),
            @Mapping(source = "bill.updateBy",target = "operatorUserName")
    })
    BillCollectDetailDto toCollectDto(BillDetail billDetail, Bill bill);
}