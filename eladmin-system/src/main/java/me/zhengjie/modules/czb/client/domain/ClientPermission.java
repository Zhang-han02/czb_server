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
package me.zhengjie.modules.czb.client.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.zhengjie.base.BaseEntity;
import me.zhengjie.modules.system.domain.Menu;
import me.zhengjie.modules.system.domain.Role;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.Set;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2022-02-18
**/
@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name="czb_client_permission")
public class ClientPermission extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Long id;

    @JSONField(serialize = false)
    @ManyToMany(mappedBy = "clientPermissions")
    @ApiModelProperty(value = "拥有该客户端权限的角色")
    private Set<Role> roles;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "权限名称")
    private String name;

    @Column(name = "tag",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "权限标识(唯一)")
    private String tag;

    public void copy(ClientPermission source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}