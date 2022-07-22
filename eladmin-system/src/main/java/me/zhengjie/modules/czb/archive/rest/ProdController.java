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

import cn.hutool.core.util.ObjectUtil;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.archive.domain.ClassTree;
import me.zhengjie.modules.czb.archive.domain.Prod;
import me.zhengjie.modules.czb.archive.service.ProdService;
import me.zhengjie.modules.czb.archive.service.dto.ProdQueryCriteria;
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
* @date 2021-09-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：产品档案管理")
@RequestMapping("/api/prod")
public class ProdController {

    private final ProdService prodService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('prod:list')")
    public void download(HttpServletResponse response, ProdQueryCriteria criteria) throws IOException {
        prodService.download(prodService.queryAll(criteria), response);
    }

    @ApiOperation("导入产品数据")
    @PostMapping(value = "/importExcel")
    @PreAuthorize("@el.check('prod:add')")
    public void importExcel(@ApiParam(value = "excel文件", required = true) MultipartFile excel,
                            @ApiParam(value = "导入模式 1：覆盖导入 2：非覆盖导入",defaultValue = "2")@RequestParam(defaultValue = "2") int importModel) throws IOException {
        prodService.importExcel(excel.getInputStream(),importModel);
    }

    @GetMapping
    @Log("查询产品档案")
    @ApiOperation("查询产品档案")
    @PreAuthorize("@el.check('prod:list')")
    public ResponseEntity<Object> query(ProdQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(prodService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增产品档案")
    @ApiOperation("新增产品档案")
    @PreAuthorize("@el.check('prod:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Prod resources){
        return new ResponseEntity<>(prodService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改产品档案")
    @ApiOperation("修改产品档案")
    @PreAuthorize("@el.check('prod:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Prod resources){
        prodService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除产品档案")
    @ApiOperation("删除产品档案")
    @PreAuthorize("@el.check('prod:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        prodService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("ERP接口数据导入")
    @ApiOperation("ERP接口数据导入")
    @GetMapping(value = "/importData/{flag}")
    @AnonymousAccess
    public  ResponseEntity<Object> importData(@PathVariable("flag") Boolean flag) {
        prodService.importData(flag);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}