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
package me.zhengjie.modules.system.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.weigh.domain.BarcodeConfig;
import me.zhengjie.modules.system.domain.PrintTemplate;
import me.zhengjie.modules.system.service.PrintTemplateService;
import me.zhengjie.modules.system.service.dto.PrintTemplateQueryCriteria;
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
 * @date 2022-01-28
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：打印模板配置管理")
@RequestMapping("/api/printTemplate")
public class PrintTemplateController {

    private final PrintTemplateService printTemplateService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('printTemplate:list')")
    public void download(HttpServletResponse response, PrintTemplateQueryCriteria criteria) throws IOException {
        printTemplateService.download(printTemplateService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询打印模板配置")
    @ApiOperation("查询打印模板配置")
    @PreAuthorize("@el.check('printTemplate:list')")
    public ResponseEntity<Object> query(PrintTemplateQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(printTemplateService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/detail")
//    @Log("查询打印模板配置最新一条记录")
    @ApiOperation("查询打印模板配置")
    @PreAuthorize("@el.check('printTemplate:list')")
    public ResponseEntity<Object> detail(PrintTemplateQueryCriteria criteria) {
        return new ResponseEntity<>(printTemplateService.queryDetail(criteria), HttpStatus.OK);
    }

    @PostMapping("/save")
    @Log("保存打印模板配置")
    @ApiOperation("保存打印模板配置")
    @PreAuthorize("@el.check('printTemplate:add')")
    public ResponseEntity<Object> save(@Validated @RequestBody PrintTemplate resources) {
        printTemplateService.save(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @Log("新增打印模板配置")
    @ApiOperation("新增打印模板配置")
    @PreAuthorize("@el.check('printTemplate:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PrintTemplate resources) {
        return new ResponseEntity<>(printTemplateService.create(resources), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改打印模板配置")
    @ApiOperation("修改打印模板配置")
    @PreAuthorize("@el.check('printTemplate:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PrintTemplate resources) {
        printTemplateService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除打印模板配置")
    @ApiOperation("删除打印模板配置")
    @PreAuthorize("@el.check('printTemplate:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        printTemplateService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}