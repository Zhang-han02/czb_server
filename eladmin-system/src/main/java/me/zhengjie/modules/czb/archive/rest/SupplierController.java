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
package me.zhengjie.modules.czb.archive.rest;

import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import me.zhengjie.modules.czb.archive.service.SupplierService;
import me.zhengjie.modules.czb.archive.service.dto.SupplierQueryCriteria;
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
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-10-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：供应商档案管理")
@RequestMapping("/api/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('supplier:list')")
    public void download(HttpServletResponse response, SupplierQueryCriteria criteria) throws IOException {
        supplierService.download(supplierService.queryAll(criteria), response);
    }

    @ApiOperation("导入供应商数据")
    @PostMapping(value = "/importExcel")
    @PreAuthorize("@el.check('supplier:add')")
    public void importExcel(@ApiParam(value = "excel文件", required = true) MultipartFile excel,
                            @ApiParam(value = "导入模式 1：覆盖导入 2：非覆盖导入",defaultValue = "2")@RequestParam(defaultValue = "2") int importModel) throws IOException {
        supplierService.importExcel(excel.getInputStream(),importModel);
    }

    @GetMapping
    @Log("查询供应商档案")
    @ApiOperation("查询供应商档案")
    @PreAuthorize("@el.check('supplier:list')")
    public ResponseEntity<Object> query(SupplierQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(supplierService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增供应商档案")
    @ApiOperation("新增供应商档案")
    @PreAuthorize("@el.check('supplier:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Supplier resources){
        return new ResponseEntity<>(supplierService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改供应商档案")
    @ApiOperation("修改供应商档案")
    @PreAuthorize("@el.check('supplier:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Supplier resources){
        supplierService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除供应商档案")
    @ApiOperation("删除供应商档案")
    @PreAuthorize("@el.check('supplier:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        supplierService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("ERP接口数据导入")
    @ApiOperation("ERP接口数据导入")
    @GetMapping(value = "/importData/{flag}")
    @AnonymousAccess
    public  ResponseEntity<Object> importData(@PathVariable("flag") Boolean flag) {
        supplierService.importData(flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}