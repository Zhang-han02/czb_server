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
import me.zhengjie.modules.czb.billManage.domain.BillDetail;
import me.zhengjie.modules.czb.billManage.service.BillDetailService;
import me.zhengjie.modules.czb.billManage.service.dto.BillCollectDetailQueryCriteria;
import me.zhengjie.modules.czb.billManage.service.dto.BillDetailQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @date 2021-10-29
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：单据详情管理")
@RequestMapping("/api/billDetail")
public class BillDetailController {

    private final BillDetailService billDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('billDetail:list')")
    public void download(HttpServletResponse response, BillDetailQueryCriteria criteria) throws IOException {
        billDetailService.download(billDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询单据详情")
    @ApiOperation("查询单据详情")
    @PreAuthorize("@el.check('billDetail:list')")
    public ResponseEntity<Object> query(BillDetailQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(billDetailService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增单据详情")
    @ApiOperation("新增单据详情")
    @PreAuthorize("@el.check('billDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BillDetail resources) {
        return new ResponseEntity<>(billDetailService.create(resources), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改单据详情")
    @ApiOperation("修改单据详情")
    @PreAuthorize("@el.check('billDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BillDetail resources) {
        billDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除单据详情")
    @ApiOperation("删除单据详情")
    @PreAuthorize("@el.check('billDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        billDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/collectDetail")
    @Log("查询单据汇总详情")
    @ApiOperation("查询单据汇总详情")
    @PreAuthorize("@el.check('warehousing:collect','stockOut:collect')")
    public ResponseEntity<Object> queryCollectDetail(BillCollectDetailQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(billDetailService.collectDetail(criteria, pageable), HttpStatus.OK);
    }
}