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

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import me.zhengjie.base.BaseEntity;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-09-17
**/
@Entity
@Data
@Table(name="czb_warehouse")
public class Warehouse extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "仓库id")
    private Long id;

    @Column(name = "class_id")
    @ApiModelProperty(value = "分类id")
    private Long classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "分类信息")
    private ClassTree classInfo;

    @Column(name = "warehouse_num",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库编号")
    private String warehouseNum;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "仓库名称")
    private String name;

    @Column(name = "enabled",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态")
    private Boolean enabled;

    @Column(name = "remark1")
    @ApiModelProperty(value = "备注1")
    private String remark1;

    @Column(name = "remark2")
    @ApiModelProperty(value = "备注2")
    private String remark2;

    public void copy(Warehouse source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}