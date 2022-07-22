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
package me.zhengjie.modules.czb.weigh.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import me.zhengjie.base.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2022-01-07
**/
@Entity
@Data
@Table(name="czb_barcode_config")
public class BarcodeConfig extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "记录id")
    private Long id;

    @Column(name = "prefix_type")
    @ApiModelProperty(value = "前缀类型")
    private String prefixType;

    @Column(name = "prefix_value")
    @ApiModelProperty(value = "前缀固定字符")
    private String prefixValue;

    @Column(name = "suffix_type")
    @ApiModelProperty(value = "后缀类型")
    private String suffixType;

    @Column(name = "suffix_value")
    @ApiModelProperty(value = "后缀固定字符")
    private String suffixValue;

    @Column(name = "serial_number_type",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "流水号类型")
    private String serialNumberType;

    public void copy(BarcodeConfig source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}