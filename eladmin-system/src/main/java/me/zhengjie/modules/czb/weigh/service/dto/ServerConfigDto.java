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
* @date 2021-10-21
**/
@Data
@ApiModel(value = "服务器配置")
public class ServerConfigDto extends BaseDTO implements Serializable {

    /** 主键 */
    private Long id;

    /** 数据库类型 */
    @ApiModelProperty(value = "数据库类型: 1:SQL Server 2:MySQL")
    private String dbType;

    /** 数据库ip地址 */
    @ApiModelProperty(value = "数据库地址")
    private String dbIp;

    /** 数据库端口 */
    @ApiModelProperty(value = "数据库端口")
    private String dbPort;

    /** 数据库名称 */
    @ApiModelProperty(value = "数据库名称")
    private String dbName;

    /** 数据库用户名 */
    @ApiModelProperty(value = "数据库用户名")
    private String dbUsername;

    /** 数据库密码 */
    @ApiModelProperty(value = "数据库密码")
    private String dbPassword;

    /** 称重模式 */
    @ApiModelProperty(value = "称重模式: 1:地磅模式 2:产品模式 3:订单模式 4:产品订单模式")
    private Integer weighModel;

    /** 称重频次 */
    @ApiModelProperty(value = "称重频次: 1:直接称重 2:连续称重")
    private Integer weighFrequency;

    /** 是否保存后自动打印 0:否 1:是 */
    @ApiModelProperty(value = "是否保存后自动打印 0:否 1:是")
    private Boolean isAutoPrint;

    /** 是否稳定后自动保存并打印 0:否 1:是 */
    @ApiModelProperty(value = "是否稳定后自动保存并打印 0:否 1:是")
    private Boolean isAutoSavePrint;

    /** 是否必选订单 0:否 1:是 */
    @ApiModelProperty(value = "是否必选订单 0:否 1:是")
    private Boolean isMustOrder;

    @ApiModelProperty(value = "账套名称")
    private String account;

}