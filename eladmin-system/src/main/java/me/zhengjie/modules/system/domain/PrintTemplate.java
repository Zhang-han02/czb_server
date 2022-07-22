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
package me.zhengjie.modules.system.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2022-01-28
**/
@Entity
@Getter
@Setter
@Table(name="czb_print_template")
public class PrintTemplate extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "模板id")
    private Long id;

    @Column(name = "paper_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "纸张类型")
    private String paperType;

    @Column(name = "paper_width",nullable = false)
    @NotNull
    @ApiModelProperty(value = "纸张宽度")
    private BigDecimal paperWidth;

    @Column(name = "paper_height",nullable = false)
    @NotNull
    @ApiModelProperty(value = "纸张高度")
    private BigDecimal paperHeight;

    @Column(name = "content",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "模板内容")
    private String content;

    @Column(name = "`key`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "模板key")
    private String key;

    public void copy(PrintTemplate source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}