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
import me.zhengjie.modules.czb.archive.domain.Prod;
import me.zhengjie.modules.czb.archive.service.ProdService;
import me.zhengjie.modules.czb.archive.service.dto.ProdQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Api(tags = "APIs-称重宝：产品档案管理")
@RequestMapping("/apis/prod")
public class ApisProdController {

    private final ProdService prodService;

    @GetMapping
    @Log("查询产品档案")
    @ApiOperation("查询产品档案")
    @AnonymousAccess
    public ResponseEntity<Object> query(ProdQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(prodService.queryAll(criteria, pageable), HttpStatus.OK);
    }
}