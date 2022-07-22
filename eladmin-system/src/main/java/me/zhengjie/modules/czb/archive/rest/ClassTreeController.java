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

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.archive.domain.ClassTree;
import me.zhengjie.modules.czb.archive.service.ClassTreeService;
import me.zhengjie.modules.czb.archive.service.dto.ClassTreeDto;
import me.zhengjie.modules.czb.archive.service.dto.ClassTreeQueryCriteria;
import me.zhengjie.utils.PageUtil;
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
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-09-17
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：树形分类管理管理")
@RequestMapping("/api/classTree")
public class ClassTreeController {

    private final ClassTreeService classTreeService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('classTree:list')")
    public void download(HttpServletResponse response, ClassTreeQueryCriteria criteria) throws IOException {
        classTreeService.download(classTreeService.queryAll(criteria), response);
    }

    @ApiOperation("导入分类数据")
    @PostMapping(value = "/importExcel")
    public void importExcel(@ApiParam(value = "excel文件", required = true) MultipartFile excel,
                            @ApiParam(value = "导入模式 1：覆盖导入 2：非覆盖导入",defaultValue = "2")@RequestParam(defaultValue = "2") int importModel,
                            @ApiParam(value = "类型 1：产品分类 2：仓库分类 3：客户分类 4：供货商分类",required = true)@RequestParam() Integer classType) throws IOException {
        classTreeService.importExcel(excel.getInputStream(),importModel,classType);
    }

    @GetMapping
    @ApiOperation("查询树形分类管理")
    public ResponseEntity<Object> query(ClassTreeQueryCriteria criteria){
        List<ClassTreeDto> list = classTreeService.queryAll(criteria);
        return new ResponseEntity<>(PageUtil.toPage(list, list.size()),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增树形分类管理")
    @ApiOperation("新增树形分类管理")
//    @PreAuthorize("@el.check('classTree:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClassTree resources){
        return new ResponseEntity<>(classTreeService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改树形分类管理")
    @ApiOperation("修改树形分类管理")
//    @PreAuthorize("@el.check('classTree:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ClassTree resources){
        classTreeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除树形分类管理")
    @ApiOperation("删除树形分类管理")
//    @PreAuthorize("@el.check('classTree:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        classTreeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}