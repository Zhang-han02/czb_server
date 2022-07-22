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
import me.zhengjie.base.BaseDTO;
import me.zhengjie.modules.czb.archive.domain.ClassTree;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-09-17
**/
@Data
public class ClassTreeDto extends BaseDTO implements Serializable {

    /** ID */
    private Long id;

    /** 上级id */
    private Long pid;

    /** 分类名称 */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private Boolean enabled;

    /** 类型 1：产品分类 2：仓库分类 3：客户分类 4：供货商分类 */
    private Integer type;

    @ApiModelProperty(value = "分类编号")
    private String classNum;

    private Integer subCount;

    private List<ClassTreeDto> children = new ArrayList<>();

    public Boolean getLeaf() {
        return subCount <= 0;
    }

    public Boolean getHasChildren() {
        return subCount > 0;
    }

    public String getLabel() {
        return name;
    }


}