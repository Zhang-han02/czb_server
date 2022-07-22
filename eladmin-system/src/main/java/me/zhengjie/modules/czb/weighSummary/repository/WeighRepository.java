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
package me.zhengjie.modules.czb.weighSummary.repository;

import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @date 2022-01-07
 **/
public interface WeighRepository extends JpaRepository<Weigh, Long>, JpaSpecificationExecutor<Weigh> {

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_status = ?1 group by a.prod_id", nativeQuery = true)
    Page<Map<String, Object>> findWeightByProdId(String billStatus, Pageable pageable);

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight ,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_type=1 and a.bill_status = ?1 group by a.supplier_id", nativeQuery = true)
    Page<Map<String, Object>> findWeightBySupplier(String billStatus, Pageable pageable);

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight ,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_type=2 and a.bill_status = ?1 group by a.cust_id", nativeQuery = true)
    Page<Map<String, Object>> findWeightByCust(String billStatus, Pageable pageable);

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight ,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_status = ?1 group by a.operator_id", nativeQuery = true)
    Page<Map<String, Object>> findWeightByUser(String billStatus, Pageable pageable);

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight ,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_status = ?1 group by a.weigh_date", nativeQuery = true)
    Page<Map<String, Object>> findWeightByBillDate(String billStatus, Pageable pageable);

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight ,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_status = ?1 group by a.warehouse_id", nativeQuery = true)
    Page<Map<String, Object>> findWeightByWarehouse(String billStatus, Pageable pageable);

    @Query(value = "select a.id,a.weigh_date as weighDate,a.bill_type as billType," + "a.pound_name as poundName,sum(a.weight) as weight ,a.unit," + "a.prod_id as prodId,prod.name as prodName ,a.supplier_id as supplierId,supplier.name as supplierName," + "a.warehouse_id as warehouseId,warehouse.name as warehouseName,a.operator_id as operatorId,userinfo.nick_name as operatorName," + "a.cust_id as custId,cust.name as custName " + "from czb_weigh a " + "left join czb_prod prod on prod.id = a.prod_id " + "left join czb_supplier supplier on supplier.id = a.supplier_id " + "left join czb_warehouse warehouse on warehouse.id = a.warehouse_id " + "left join czb_cust cust on cust.id = a.cust_id " + "left join sys_user userinfo on userinfo.user_id = a.operator_id " + "where a.bill_status = ?1  group by a.pound_name", nativeQuery = true)
    Page<Map<String, Object>> findWeightByPoundName(String billStatus, Pageable pageable);

    @Query(value = "SELECT * from czb_weigh where id in (?1)", nativeQuery = true)
    List<Map<String, Object>> findListByIds(String ids);

    @Query(value = "SELECT pound_name,weigh_date,sum(weight) as weight,COUNT(*) as number,SUM(CASE WHEN bill_status= '1' THEN 1 ELSE 0 END) nosync ,SUM(CASE WHEN bill_status= '2' THEN 1 ELSE 0 END) sync  FROM `czb_weigh` WHERE weigh_date = DATE(NOW()) GROUP BY pound_name", nativeQuery = true)
    Page<Map<String, Object>> findPound(Pageable pageable);

    @Query(value = "select b.name as prod_name,count(1) as total,convert(SUM(a.weight),decimal(10,2)) as weightTotal,convert(SUM(a.weight)/count(1),decimal(10,2))as average from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where bill_type=?1 and \n" +
            "DATE_FORMAT(a.weigh_date,'%Y%m') =DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY b.name ORDER BY weightTotal desc limit 10", nativeQuery = true)
    List<Map<String, Object>> findProd(Integer billType);
    @Query(value = "SELECT count(*) as number,IFNULL(SUM(weight),0) as weight from czb_weigh where bill_type = ?1 and weigh_date = DATE(NOW())",nativeQuery = true)
    Page<Map<String, Object>> findDAY(Integer billType, Pageable pageable);
    @Query(value = "SELECT count(*) as number,IFNULL(SUM(weight),0) as weight from czb_weigh where bill_type = ?1 and DATE_FORMAT(weigh_date,'%Y%m') =DATE_FORMAT(CURDATE(),'%Y%m')",nativeQuery = true)
    Page<Map<String, Object>> findMoth(Integer billType, Pageable pageable);
    @Query(value = "SELECT count(*) as number,IFNULL(SUM(weight),0) as weight from czb_weigh where bill_type = ?1 and YEAR(weigh_date) = YEAR(NOW())",nativeQuery = true)
    Page<Map<String, Object>> findYear(Integer billType, Pageable pageable);
    @Query(value = "SELECT DATE_FORMAT((CURDATE() - INTERVAL 11 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0) as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 11 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            " UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 10 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0) as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 10 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 9 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 9 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 8 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 8 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 7 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 7 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 6 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 6 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 5 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 5 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 4 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 4 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 3 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 3 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 2 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 2 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 1 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 1 MONTH), '%Y-%m') and a.prod_id = ?1\n" +
            "UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 0 MONTH), '%Y-%m') AS `month`,IFNULL(SUM(a.weight),0)as weigh,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id = b.id where DATE_FORMAT(a.weigh_date,'%Y-%m')=DATE_FORMAT((CURDATE() - INTERVAL 0 MONTH), '%Y-%m') and a.prod_id = ?1\n",nativeQuery = true)
    List<Map<String,Object>> prodTopThree(String prod_id);

    @Query(value = "SELECT b.id,SUM(a.weight)as weight,b.name from czb_weigh a LEFT JOIN czb_prod b on a.prod_id =b.id\n" +
            "\twhere YEAR(weigh_date) = YEAR(NOW()) GROUP BY b.name ORDER BY weight desc\n" +
            "limit 3",nativeQuery = true)
    List<Map<String,Object>> prodTop();



}