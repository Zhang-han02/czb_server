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
package me.zhengjie.modules.czb.billManage.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.billManage.domain.Bill;
import me.zhengjie.modules.czb.billManage.service.BillService;
import me.zhengjie.modules.czb.billManage.service.dto.BillQueryCriteria;
import me.zhengjie.utils.StringUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @date 2021-10-29
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：单据管理")
@RequestMapping("/api/bill")
public class BillController {

    private final BillService billService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('bill:list')")
    public void download(HttpServletResponse response, BillQueryCriteria criteria) throws IOException {
        billService.download(billService.queryAll(criteria), response);
    }

    @ApiOperation("导入单据数据")
    @PostMapping(value = "/importExcel")
//    @PreAuthorize("@el.check('bill:add')")
    public void importExcel(@ApiParam(value = "excel文件", required = true) MultipartFile excel,
                            @ApiParam(value = "导入模式 1：覆盖导入 2：非覆盖导入", defaultValue = "2") @RequestParam(defaultValue = "2") int importModel,
                            @ApiParam(value = "单据类型 1：采购入库 2：销售出库", required = true) @RequestParam() Integer classType) throws IOException {
        billService.importExcel(excel.getInputStream(), classType);
    }

    @GetMapping
    @Log("查询单据")
    @ApiOperation("查询单据")
//    @PreAuthorize("@el.check('bill:list')")
    public ResponseEntity<Object> query(BillQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(billService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/serialNumber")
    @ApiOperation("获取流水号")
    public ResponseEntity<Object> serialNumber(@ApiParam(value = "单据类型 1：采购入库 2：销售出库", required = true)
                                               @RequestParam int billType) {
        return new ResponseEntity<>(billService.getSerialNumber(billType), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增单据")
    @ApiOperation("新增单据")
//    @PreAuthorize("@el.check('bill:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Bill resources) {
        return new ResponseEntity<>(billService.create(resources), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改单据")
    @ApiOperation("修改单据")
//    @PreAuthorize("@el.check('bill:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Bill resources) {
        billService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除单据")
    @ApiOperation("删除单据")
//    @PreAuthorize("@el.check('bill:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        billService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/collect")
    @Log("查询出入库汇总")
    @ApiOperation("查询出入库汇总")
    @PreAuthorize("@el.check('warehousing:collect','stockOut:collect')")
    public ResponseEntity<Object> queryCollect(
            @ApiParam(required = true, value = "单据类型 1：采购入库 2：销售出库") @RequestParam Integer billType,
            @ApiParam(required = true, value = "汇总类型 1：按产品 2：按往来单位 3：按仓库 4：按日期") @RequestParam Integer collectType,
            Pageable pageable) {
        return new ResponseEntity<>(billService.collect(billType, collectType, pageable), HttpStatus.OK);
    }

    @GetMapping("/print")
    @Log("出入库单据打印")
    @ApiOperation("出入库单据打印")
//    @PreAuthorize("@el.check('warehousing:collect','stockOut:collect')")
    public ResponseEntity<Object> print(
            @ApiParam(required = true, value = "主键集合，用逗号拼接成字符串") @RequestParam String ids) {
        if (StringUtils.isEmpty(ids)) {
            throw new BadRequestException("ids不能为空");
        }
        long[] idArray = Arrays.stream(ids.split(",")).mapToLong(Long::parseLong).toArray();
        return new ResponseEntity<>(billService.print(idArray), HttpStatus.OK);
    }

}