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
package me.zhengjie.modules.czb.billManage.repository;

import me.zhengjie.modules.czb.billManage.domain.Bill;
import me.zhengjie.modules.czb.billManage.domain.BillDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @date 2021-10-29
 **/
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {

    @Query("select bd.prod.name as prodName,bd.prodId as prodId,sum(bd.weight) as weight " +
            "from BillDetail bd left join Bill b on b.id = bd.billId where b.billType = ?1 group by bd.prodId")
    Page<Map<String, Object>> findCollectByProd(Integer billType, Pageable pageable);

    @Query("select b.supplier.name as supplierName,b.supplierId as supplierId,sum(bd.weight) as weight " +
            "from BillDetail bd left join Bill b on b.id = bd.billId where b.billType = ?1 group by b.supplierId")
    Page<Map<String, Object>> findCollectBySupplier(Integer billType, Pageable pageable);

    @Query("select b.cust.name as custName,b.custId as custId,sum(bd.weight) as weight " +
            "from BillDetail bd left join Bill b on b.id = bd.billId where b.billType = ?1 group by b.custId")
    Page<Map<String, Object>> findCollectByCust(Integer billType, Pageable pageable);

    @Query("select b.warehouse.name as warehouseName,b.warehouseId as warehouseId,sum(bd.weight) as weight " +
            "from BillDetail bd left join Bill b on b.id = bd.billId where b.billType = ?1 group by b.warehouseId")
    Page<Map<String, Object>> findCollectByWarehouse(Integer billType, Pageable pageable);

    @Query("select b.billDate as billDate,sum(bd.weight) as weight " +
            "from BillDetail bd left join Bill b on b.id = bd.billId where b.billType = ?1 group by b.billDate")
    Page<Map<String, Object>> findCollectByBillDate(Integer billType, Pageable pageable);

    boolean existsByBillNumAndBillTypeAndIdNot(String billNum,Integer billType,Long id);

}