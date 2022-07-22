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
* @date 2021-10-21
**/
@Entity
@Data
@Table(name="czb_server_config")
public class ServerConfig extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键")
    private Long id;

    @Column(name = "db_type")
    @ApiModelProperty(value = "数据库类型")
    private String dbType;

    @Column(name = "db_ip")
    @ApiModelProperty(value = "数据库地址")
    private String dbIp;

    @Column(name = "db_port")
    @ApiModelProperty(value = "数据库端口")
    private String dbPort;

    @Column(name = "db_name")
    @ApiModelProperty(value = "数据库名称")
    private String dbName;

    @Column(name = "db_username")
    @ApiModelProperty(value = "数据库用户名")
    private String dbUsername;

    @Column(name = "db_password")
    @ApiModelProperty(value = "数据库密码")
    private String dbPassword;

    @Column(name = "weigh_model")
    @ApiModelProperty(value = "称重模式")
    private Integer weighModel;

    @Column(name = "weigh_frequency")
    @ApiModelProperty(value = "称重频次")
    private Integer weighFrequency;

    @Column(name = "is_auto_print")
    @ApiModelProperty(value = "是否保存后自动打印 0:否 1:是")
    private Boolean isAutoPrint;

    @Column(name = "is_auto_save_print")
    @ApiModelProperty(value = "是否稳定后自动保存并打印 0:否 1:是")
    private Boolean isAutoSavePrint;

    @Column(name = "is_must_order")
    @ApiModelProperty(value = "是否必选订单 0:否 1:是")
    private Boolean isMustOrder;

    @Column(name = "account")
    @ApiModelProperty(value = "账套名称")
    private String account;

    public void copy(ServerConfig source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}