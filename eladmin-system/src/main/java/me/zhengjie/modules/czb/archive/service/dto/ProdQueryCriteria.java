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
import java.util.Set;

import me.zhengjie.annotation.Query;
import me.zhengjie.modules.czb.archive.domain.ClassTree;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-09-23
**/
@Data
public class ProdQueryCriteria{

    /** 精确 */
    @ApiModelProperty(value = "分类id")
    @Query(propName = "id",joinName = "classInfo",type = Query.Type.IN)
    private Set<Long> classIds;

    /** 模糊 */
    @ApiModelProperty(value = "产品编号")
    @Query(type = Query.Type.INNER_LIKE)
    private String prodNum;

    /** 模糊 */
    @ApiModelProperty(value = "产品名称")
    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    /** 精确 */
    @ApiModelProperty(value = "状态 true：启用 false：禁用")
    @Query
    private Boolean enabled;
}