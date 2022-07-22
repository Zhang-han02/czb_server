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
import java.util.Set;

import me.zhengjie.annotation.Query;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-10-15
**/
@Data
public class CustQueryCriteria{

    /** 精确 */
    @ApiModelProperty(value = "分类id")
    @Query(propName = "id",joinName = "classInfo",type = Query.Type.IN)
    private Set<Long> classIds;

    /** 模糊 */
    @ApiModelProperty(value = "客户编号")
    @Query(type = Query.Type.INNER_LIKE)
    private String custNum;

    /** 模糊 */
    @ApiModelProperty(value = "客户名称")
    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    /** 模糊 */
    @ApiModelProperty(value = "联系人")
    @Query(type = Query.Type.INNER_LIKE)
    private String contact;

    @ApiModelProperty(value = "客户编号、名称查询")
    @Query(blurry = "custNum,name")
    private String blurry;

    /** 精确 */
    @ApiModelProperty(value = "状态 true：启用 false：禁用")
    @Query
    private Boolean enabled;
}