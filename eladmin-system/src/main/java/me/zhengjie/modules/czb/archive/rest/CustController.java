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
import me.zhengjie.modules.czb.archive.domain.Cust;
import me.zhengjie.modules.czb.archive.service.CustService;
import me.zhengjie.modules.czb.archive.service.dto.CustQueryCriteria;
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
* @date 2021-10-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：客户档案管理")
@RequestMapping("/api/cust")
public class CustController {

    private final CustService custService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('cust:list')")
    public void download(HttpServletResponse response, CustQueryCriteria criteria) throws IOException {
        custService.download(custService.queryAll(criteria), response);
    }

    @ApiOperation("导入客户数据")
    @PostMapping(value = "/importExcel")
    @PreAuthorize("@el.check('cust:add')")
    public void importExcel(@ApiParam(value = "excel文件", required = true) MultipartFile excel,
                            @ApiParam(value = "导入模式 1：覆盖导入 2：非覆盖导入",defaultValue = "2")@RequestParam(defaultValue = "2") int importModel) throws IOException {
        custService.importExcel(excel.getInputStream(),importModel);
    }

    @GetMapping
    @Log("查询客户档案")
    @ApiOperation("查询客户档案")
    @PreAuthorize("@el.check('cust:list')")
    public ResponseEntity<Object> query(CustQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(custService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增客户档案")
    @ApiOperation("新增客户档案")
    @PreAuthorize("@el.check('cust:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Cust resources){
        return new ResponseEntity<>(custService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改客户档案")
    @ApiOperation("修改客户档案")
    @PreAuthorize("@el.check('cust:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Cust resources){
        custService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除客户档案")
    @ApiOperation("删除客户档案")
    @PreAuthorize("@el.check('cust:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        custService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("ERP接口数据导入")
    @ApiOperation("ERP接口数据导入")
    @GetMapping(value = "/importData/{flag}")
    @AnonymousAccess
    public  ResponseEntity<Object> importData(@PathVariable("flag") Boolean flag) {
        custService.importData(flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}