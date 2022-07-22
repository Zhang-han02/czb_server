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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;
import me.zhengjie.annotation.Query;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.Column;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-09-17
**/
@Data
public class ClassTreeQueryCriteria{

    @ApiModelProperty(value = "状态 true：启用 false：禁用")
    @Query
    private Boolean enabled;

    @ApiModelProperty("类型 1：产品分类 2：仓库分类 3：客户分类 4：供货商分类")
    @Query
    private Integer type;

    @ApiModelProperty(value = "上级id")
    @Query
    private Long pid;

    /**
     * pid为空的
     */
    @ApiModelProperty(hidden = true)
    @Query(type = Query.Type.IS_NULL, propName = "pid")
    private Boolean pidIsNull;
}