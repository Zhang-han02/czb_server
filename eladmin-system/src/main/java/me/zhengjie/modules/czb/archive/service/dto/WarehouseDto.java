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
package me.zhengjie.modules.czb.archive.service.dto;

import lombok.Data;
import me.zhengjie.base.BaseDTO;

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-09-17
**/
@Data
public class WarehouseDto extends BaseDTO implements Serializable {

    /** 仓库id */
    private Long id;

    /** 分类id */
    private Long classId;

    private ClassTreeDto classInfo;

    /** 仓库编号 */
    private String warehouseNum;

    /** 仓库名称 */
    private String name;

    /** 状态 */
    private Boolean enabled;

    /** 备注1 */
    private String remark1;

    /** 备注2 */
    private String remark2;


}