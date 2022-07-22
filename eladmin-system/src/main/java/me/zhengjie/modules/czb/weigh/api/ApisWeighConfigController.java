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
package me.zhengjie.modules.czb.weigh.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.weigh.domain.ServerConfig;
import me.zhengjie.modules.czb.weigh.service.BarcodeConfigService;
import me.zhengjie.modules.czb.weigh.service.ServerConfigService;
import me.zhengjie.modules.czb.weigh.service.dto.BarcodeConfigDto;
import me.zhengjie.modules.czb.weigh.service.dto.ServerConfigDto;
import me.zhengjie.modules.czb.weigh.service.dto.ServerConfigQueryCriteria;
import me.zhengjie.utils.JdbcUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @date 2021-10-21
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "APIs-称重宝：称重配置管理")
@RequestMapping("/apis/weighConfig")
public class ApisWeighConfigController {

    private final ServerConfigService serverConfigService;
    private final BarcodeConfigService barcodeConfigService;

    @GetMapping("/server")
    @ApiOperation("查询服务器配置信息")
    @AnonymousAccess
    public ResponseEntity<ServerConfigDto> queryServer() {
        return new ResponseEntity<>(serverConfigService.queryLatest(), HttpStatus.OK);
    }

    @GetMapping("/barcode")
    @ApiOperation("查询条码设置信息")
    @AnonymousAccess
    public ResponseEntity<BarcodeConfigDto> queryBarcode() {
        return new ResponseEntity<>(barcodeConfigService.queryLatest(), HttpStatus.OK);
    }

}