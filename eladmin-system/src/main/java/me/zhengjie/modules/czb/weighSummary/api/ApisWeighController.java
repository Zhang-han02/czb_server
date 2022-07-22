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
package me.zhengjie.modules.czb.weighSummary.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.archive.service.dto.SupplierQueryCriteria;
import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import me.zhengjie.modules.czb.weighSummary.service.WeighService;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @date 2022-02-07
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "APIs-称重宝：称重汇总")
@RequestMapping("/apis/weighSummary")
public class ApisWeighController {

    private final WeighService weighService;

    @PostMapping
    @ApiOperation("新增称重汇总")
    @AnonymousAccess
    public ResponseEntity<Object> create(@Validated @RequestBody Weigh resources) {
        return new ResponseEntity<>(weighService.create(resources), HttpStatus.CREATED);
    }

    @GetMapping
    @Log("查询称重汇总")
    @ApiOperation("查询称重汇总")
    @AnonymousAccess
    public ResponseEntity<Object> query(WeighQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(weighService.queryAll(criteria), HttpStatus.OK);
    }

}