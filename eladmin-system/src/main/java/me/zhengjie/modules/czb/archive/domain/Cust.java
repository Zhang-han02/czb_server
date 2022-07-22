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
import reactor.util.annotation.Nullable;

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-10-15
**/
@Entity
@Data
@Table(name="czb_cust")
public class Cust extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "客户id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "分类信息")
    private ClassTree classInfo;

    @Column(name = "cust_num",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户编号")
    private String custNum;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "客户名称")
    private String name;

    @Column(name = "contact",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "联系人")
    private String contact;

    @Column(name = "contact_address")
    @ApiModelProperty(value = "联系地址")
    private String contactAddress;

    @Column(name = "contact_mobile",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "联系电话")
    private String contactMobile;

    @ManyToOne
    @JoinColumn(name = "check_out_unit")
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "结算单位")
    private Cust checkOutUnit;

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

    @Column(name = "remark3")
    @ApiModelProperty(value = "备注3")
    private String remark3;

    @Column(name = "remark4")
    @ApiModelProperty(value = "备注4")
    private String remark4;

    @Column(name = "remark5")
    @ApiModelProperty(value = "备注5")
    private String remark5;

    @Column(name = "remark6")
    @ApiModelProperty(value = "备注6")
    private String remark6;

    public void copy(Cust source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}