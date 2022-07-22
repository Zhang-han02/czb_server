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
import me.zhengjie.base.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-10-28
**/
@Entity
@Data
@Table(name="czb_supplier")
public class Supplier extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "供应商id")
    private Long id;

    @Column(name = "class_id")
    @ApiModelProperty(value = "分类id")
    private Long classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "分类信息")
    private ClassTree classInfo;

    @Column(name = "supplier_num",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商编号")
    private String supplierNum;

    @Column(name = "name",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "供应商名称")
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

    @Column(name = "check_out_unit")
    @ApiModelProperty(value = "结算单位")
    private Long checkOutUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_out_unit",insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ApiModelProperty(value = "结算单位信息")
    private Supplier checkOutUnitInfo;

    @Column(name = "enabled",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态：1启用、0禁用")
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

    public void copy(Supplier source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}