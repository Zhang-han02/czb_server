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
package me.zhengjie.modules.czb.client.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.czb.client.domain.ClientPermission;
import me.zhengjie.modules.czb.client.service.ClientPermissionService;
import me.zhengjie.modules.czb.client.service.dto.ClientPermissionQueryCriteria;
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
* @date 2022-02-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "称重宝：客户端权限管理")
@RequestMapping("/api/clientPermission")
public class ClientPermissionController {

    private final ClientPermissionService clientPermissionService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('clientPermission:list')")
    public void download(HttpServletResponse response, ClientPermissionQueryCriteria criteria) throws IOException {
        clientPermissionService.download(clientPermissionService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询客户端权限")
    @ApiOperation("查询客户端权限")
    @PreAuthorize("@el.check('clientPermission:list')")
    public ResponseEntity<Object> query(ClientPermissionQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(clientPermissionService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/all")
    @Log("查询客户端所有权限")
    @ApiOperation("查询客户端所有权限")
    @PreAuthorize("@el.check('clientPermission:list')")
    public ResponseEntity<Object> queryAll(ClientPermissionQueryCriteria criteria){
        return new ResponseEntity<>(clientPermissionService.queryAll(criteria),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增客户端权限")
    @ApiOperation("新增客户端权限")
    @PreAuthorize("@el.check('clientPermission:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClientPermission resources){
        return new ResponseEntity<>(clientPermissionService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改客户端权限")
    @ApiOperation("修改客户端权限")
    @PreAuthorize("@el.check('clientPermission:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ClientPermission resources){
        clientPermissionService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除客户端权限")
    @ApiOperation("删除客户端权限")
    @PreAuthorize("@el.check('clientPermission:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        clientPermissionService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}