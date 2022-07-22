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
package me.zhengjie.modules.czb.weigh.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.base.BaseDTO;

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2022-01-07
**/
@Data
@ApiModel(value = "条码设置")
public class BarcodeConfigDto extends BaseDTO implements Serializable {

    /** 记录id */
    private Long id;

    /** 前缀类型 */
    @ApiModelProperty(value = "前缀类型 1:产品编号 2:日期 3:固定字符")
    private String prefixType;

    /** 前缀固定字符 */
    @ApiModelProperty(value = "前缀固定字符")
    private String prefixValue;

    /** 后缀类型 */
    @ApiModelProperty(value = "后缀类型 1:产品编号 2:日期 3:固定字符")
    private String suffixType;

    /** 后缀固定字符 */
    @ApiModelProperty(value = "后缀固定字符")
    private String suffixValue;

    /** 流水号类型 */
    @ApiModelProperty(value = "流水号类型 1:每日自增长 2:每月自增长 3:全年自增长")
    private String serialNumberType;

}