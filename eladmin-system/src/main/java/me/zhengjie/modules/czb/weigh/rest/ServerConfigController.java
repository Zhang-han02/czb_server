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
import me.zhengjie.modules.czb.weigh.domain.ServerConfig;
import me.zhengjie.modules.czb.weigh.service.ServerConfigService;
import me.zhengjie.modules.czb.weigh.service.dto.ServerConfigQueryCriteria;
import me.zhengjie.utils.JdbcUtil;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @date 2021-10-21
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：服务器配置管理")
@RequestMapping("/api/serverConfig")
public class ServerConfigController {

    private final ServerConfigService serverConfigService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('serverConfig:list')")
    public void download(HttpServletResponse response, ServerConfigQueryCriteria criteria) throws IOException {
        serverConfigService.download(serverConfigService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询服务器配置")
    @ApiOperation("查询服务器配置")
    @PreAuthorize("@el.check('serverConfig:list')")
    public ResponseEntity<Object> query(ServerConfigQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(serverConfigService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping("/latest")
    @Log("查询服务器配置最新一条记录")
    @ApiOperation("查询服务器配置最新一条记录")
    @PreAuthorize("@el.check('serverConfig:list')")
    public ResponseEntity<Object> queryLatest() {
        return new ResponseEntity<>(serverConfigService.queryLatest(), HttpStatus.OK);
    }

    @PostMapping("/save")
    @Log("保存服务器配置")
    @ApiOperation("保存服务器配置")
    @PreAuthorize("@el.check('serverConfig:add')")
    public ResponseEntity<Object> save(@Validated @RequestBody ServerConfig resources) {
        serverConfigService.save(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/accountSet")
    @Log("获取账套列表")
    @ApiOperation("获取账套列表")
    @PreAuthorize("@el.check('serverConfig:add')")
    public ResponseEntity<Object> accountSet(@Validated @RequestBody ServerConfig serverConfig) {
        serverConfigService.save(serverConfig);
        Connection connection = serverConfigService.getConnection(serverConfig);
        List<String> data = serverConfigService.getAccountSet(connection);
        JdbcUtil.closeConnection(connection);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping
    @Log("新增服务器配置")
    @ApiOperation("新增服务器配置")
    @PreAuthorize("@el.check('serverConfig:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ServerConfig resources) {
        return new ResponseEntity<>(serverConfigService.create(resources), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改服务器配置")
    @ApiOperation("修改服务器配置")
    @PreAuthorize("@el.check('serverConfig:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ServerConfig resources) {
        serverConfigService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除服务器配置")
    @ApiOperation("删除服务器配置")
    @PreAuthorize("@el.check('serverConfig:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        serverConfigService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}