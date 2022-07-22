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
package me.zhengjie.modules.czb.archive.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-09-17
**/
@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name="czb_class_tree")
public class ClassTree extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "pid")
    @ApiModelProperty(value = "上级id")
    private Long pid;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "分类名称")
    private String name;

    @Column(name = "sort")
    @ApiModelProperty(value = "排序")
    private Integer sort;

    @Column(name = "enabled",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Boolean enabled;

    @Column(name = "type",nullable = false)
    @NotNull
    @ApiModelProperty(value = "类型 1：产品分类 2：仓库分类 3：客户分类 4：供货商分类")
    private Integer type;

    @Column(name = "class_num")
    @ApiModelProperty(value = "分类编号")
    private String classNum;

    @ApiModelProperty(value = "子节点数目,暂时没用")
    private Integer subCount = 0;

//    @OneToMany(cascade={CascadeType.REMOVE})
//    @JoinColumn(name = "pid")
//    private List<ClassTree> children = new ArrayList<>();


    public void copy(ClassTree source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}