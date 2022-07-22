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
package me.zhengjie.modules.czb.archive.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.archive.domain.ClassTree;
import me.zhengjie.modules.czb.archive.service.ClassTreeService;
import me.zhengjie.modules.czb.archive.service.dto.ClassTreeDto;
import me.zhengjie.modules.czb.archive.service.dto.ClassTreeQueryCriteria;
import me.zhengjie.utils.PageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-09-17
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "APIs-称重宝：树形分类管理管理")
@RequestMapping("/apis/classTree")
public class ApisClassTreeController {

    private final ClassTreeService classTreeService;

    @GetMapping
    @ApiOperation("查询树形分类管理")
    @AnonymousAccess
    public ResponseEntity<Object> query(ClassTreeQueryCriteria criteria){
        List<ClassTreeDto> list = classTreeService.queryAll(criteria);
        return new ResponseEntity<>(PageUtil.toPage(list, list.size()),HttpStatus.OK);
    }
}