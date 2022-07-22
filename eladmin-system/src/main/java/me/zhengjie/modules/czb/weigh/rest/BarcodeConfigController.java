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
package me.zhengjie.modules.czb.weigh.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.weigh.domain.BarcodeConfig;
import me.zhengjie.modules.czb.weigh.domain.ServerConfig;
import me.zhengjie.modules.czb.weigh.service.BarcodeConfigService;
import me.zhengjie.modules.czb.weigh.service.dto.BarcodeConfigQueryCriteria;
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
* @website https://el-admin.vip
* @author unknown
* @date 2022-01-07
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：条码设置管理")
@RequestMapping("/api/barcodeConfig")
public class BarcodeConfigController {

    private final BarcodeConfigService barcodeConfigService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('barcodeConfig:list')")
    public void download(HttpServletResponse response, BarcodeConfigQueryCriteria criteria) throws IOException {
        barcodeConfigService.download(barcodeConfigService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询条码设置")
    @ApiOperation("查询条码设置")
    @PreAuthorize("@el.check('barcodeConfig:list')")
    public ResponseEntity<Object> query(BarcodeConfigQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(barcodeConfigService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/latest")
    @Log("查询条码设置最新一条记录")
    @ApiOperation("查询条码设置最新一条记录")
    @PreAuthorize("@el.check('barcodeConfig:list')")
    public ResponseEntity<Object> queryLatest() {
        return new ResponseEntity<>(barcodeConfigService.queryLatest(), HttpStatus.OK);
    }

    @PostMapping("/save")
    @Log("保存条码设置")
    @ApiOperation("保存条码设置")
    @PreAuthorize("@el.check('barcodeConfig:add')")
    public ResponseEntity<Object> save(@Validated @RequestBody BarcodeConfig resources) {
        barcodeConfigService.save(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @Log("新增条码设置")
    @ApiOperation("新增条码设置")
    @PreAuthorize("@el.check('barcodeConfig:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BarcodeConfig resources){
        return new ResponseEntity<>(barcodeConfigService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改条码设置")
    @ApiOperation("修改条码设置")
    @PreAuthorize("@el.check('barcodeConfig:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BarcodeConfig resources){
        barcodeConfigService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除条码设置")
    @ApiOperation("删除条码设置")
    @PreAuthorize("@el.check('barcodeConfig:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        barcodeConfigService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}