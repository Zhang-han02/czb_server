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
package me.zhengjie.modules.czb.archive.service.dto;

import lombok.Data;
import me.zhengjie.base.BaseDTO;
import me.zhengjie.modules.czb.archive.domain.ClassTree;

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-09-23
**/
@Data
public class ProdDto extends BaseDTO implements Serializable {

    /** 产品id */
    private Long id;

    /** 分类信息 */
    private ClassTreeDto classInfo;

    /** 产品编号 */
    private String prodNum;

    /** 产品名称 */
    private String name;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 基本单位 */
    private String baseUnit;

    /** 辅助单位1 */
    private String unit1;

    /** 辅助单位2 */
    private String unit2;

    /** 状态：1启用、0禁用 */
    private Boolean enabled;

    /** 备注1 */
    private String remark1;

    /** 备注2 */
    private String remark2;

    /** 备注3 */
    private String remark3;

    /** 备注4 */
    private String remark4;

    /** 备注5 */
    private String remark5;

    /** 备注6 */
    private String remark6;

}