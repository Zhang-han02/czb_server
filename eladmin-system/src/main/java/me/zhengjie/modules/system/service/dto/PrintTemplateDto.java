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
package me.zhengjie.modules.system.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.zhengjie.base.BaseDTO;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2022-01-28
**/
@Getter
@Setter
@Accessors(chain = true)
public class PrintTemplateDto extends BaseDTO implements Serializable {

    /** 模板id */
    private Long id;

    /** 纸张类型 */
    private String paperType;

    /** 纸张宽度 */
    private BigDecimal paperWidth;

    /** 纸张高度 */
    private BigDecimal paperHeight;

    /** 模板内容 */
    private String content;

    @ApiModelProperty(value = "模板key")
    private String key;

}