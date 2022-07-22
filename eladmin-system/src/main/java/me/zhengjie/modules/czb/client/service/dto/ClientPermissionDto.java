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
package me.zhengjie.modules.czb.client.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zhengjie.base.BaseDTO;

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2022-02-18
**/
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientPermissionDto extends BaseDTO implements Serializable {

    /** id */
    private Long id;

    /** 权限名称 */
    private String name;

    /** 权限标识(唯一) */
    private String tag;

}