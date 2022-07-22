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
package me.zhengjie.modules.czb.weighSummary.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.archive.domain.Cust;
import me.zhengjie.modules.czb.archive.domain.Prod;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import me.zhengjie.modules.czb.archive.domain.Warehouse;
import me.zhengjie.modules.czb.archive.service.CustService;
import me.zhengjie.modules.czb.archive.service.ProdService;
import me.zhengjie.modules.czb.archive.service.SupplierService;
import me.zhengjie.modules.czb.archive.service.WarehouseService;
import me.zhengjie.modules.czb.archive.service.dto.CustDto;
import me.zhengjie.modules.czb.archive.service.dto.ProdDto;
import me.zhengjie.modules.czb.archive.service.dto.SupplierDto;
import me.zhengjie.modules.czb.archive.service.dto.WarehouseDto;
import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import me.zhengjie.modules.czb.weighSummary.model.PayData;
import me.zhengjie.modules.czb.weighSummary.model.PurchaseOrder;
import me.zhengjie.modules.czb.weighSummary.model.PurchaseOrderDetail;
import me.zhengjie.modules.czb.weighSummary.service.WeighService;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighDto;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighQueryCriteria;
import me.zhengjie.utils.DateUtil;
import me.zhengjie.utils.HttpUtil;
import me.zhengjie.utils.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @date 2022-01-07
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：称重汇总及明细管理")
@RequestMapping("/api/weighSummary")
public class WeighController {

    private final WeighService weighService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('weighSummary:list')")
    public void download(HttpServletResponse response, WeighQueryCriteria criteria) throws IOException {
        weighService.download(weighService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询称重汇总及明细")
    @ApiOperation("查询称重汇总及明细")
    @PreAuthorize("@el.check('weighSummary:list')")
    public ResponseEntity<Object> query(@ApiParam(required = true, value = "汇总类型 1：按产品汇总,2：按往来单位汇总,3：按司磅员汇总,4：按日期汇总,5：按仓库汇总,6：按磅台汇总") @RequestParam Integer collectType,
                                        WeighQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(weighService.queryAll(collectType, criteria, pageable), HttpStatus.OK);
    }


    @Log("查询称重汇总及明细")
    @ApiOperation("查询称重汇总及明细")
    @GetMapping(value = "/detail")
    @PreAuthorize("@el.check('weighSummary:list')")
    public ResponseEntity<Object> queryDetail(WeighQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(weighService.queryAlls(criteria, pageable), HttpStatus.OK);
    }
    @Log("查询称重所有明细")
    @ApiOperation("查询称重所有明细")
    @AnonymousAccess
    @GetMapping(value = "/detailAll")
    //@PreAuthorize("@el.check('weighSummary:list')")
    public ResponseEntity<Object> queryDetailAll(WeighQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(weighService.queryDetailAll(criteria, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增称重汇总及明细")
    @ApiOperation("新增称重汇总及明细")
    @PreAuthorize("@el.check('weighSummary:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Weigh resources) {
        return new ResponseEntity<>(weighService.create(resources), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改称重汇总及明细")
    @ApiOperation("修改称重汇总及明细")
    @PreAuthorize("@el.check('weighSummary:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Weigh resources) {
        weighService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除称重汇总及明细")
    @ApiOperation("删除称重汇总及明细")
    @PreAuthorize("@el.check('weighSummary:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        weighService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/print")
    @Log("称重汇总明细打印")
    @ApiOperation("称重汇总明细打印")
//    @PreAuthorize("@el.check('warehousing:collect','stockOut:collect')")
    public ResponseEntity<Object> print(@ApiParam(required = true, value = "汇总类型 1：按产品汇总,2：按往来单位汇总,3：按司磅员汇总,4：按日期汇总,5：按仓库汇总,6：按磅台汇总") @RequestParam Integer collectType, WeighQueryCriteria criteria) {
        return new ResponseEntity<>(weighService.generatePrintData(collectType, criteria), HttpStatus.OK);
    }

    @PostMapping("/createUpOrder")
    @Log("称重汇总明细生单")
    @ApiOperation("称重汇总明细生单")
    public ResponseEntity<Object> createUpOrder(@RequestBody String ids)  {
        weighService.createBill(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("首页地磅信息")
    @ApiOperation("首页地磅信息")
    @GetMapping(value = "/findPound")
    @AnonymousAccess
    public ResponseEntity<Object> findPound(Pageable pageable) {
        return new ResponseEntity<>(weighService.findPound(pageable), HttpStatus.OK);
    }
    @Log("首页产品前十")
    @ApiOperation("首页产品前十")
    @GetMapping(value = "/findProd")
    @AnonymousAccess
    public ResponseEntity<Object> findProd(WeighQueryCriteria criteria) {
        return new ResponseEntity<>(weighService.findProd(criteria.getBillType()), HttpStatus.OK);
    }


    @Log("称重出入库信息")
    @ApiOperation("称重出入库信息")
    @GetMapping(value = "/outPut")
    @AnonymousAccess
    public ResponseEntity<Object> outPut(@RequestParam Integer collectType,WeighQueryCriteria criteria,Pageable pageable) {
        return new ResponseEntity<>(weighService.outPut(collectType,criteria,pageable), HttpStatus.OK);
    }

    @Log("产品年度top3")
    @ApiOperation("产品年度top3")
    @GetMapping(value = "/prodTopThree")
    @AnonymousAccess
    public ResponseEntity<Object> prodTopThree(Pageable pageable) {
        return new ResponseEntity<>(weighService.prodTopThree(pageable), HttpStatus.OK);
    }
}