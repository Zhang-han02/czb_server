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

import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author unknown
* @date 2021-10-15
**/
@Data
public class CustDto extends BaseDTO implements Serializable {

    /** 客户id */
    private Long id;

    /** 分类信息 */
    private ClassTreeDto classInfo;

    /** 客户编号 */
    private String custNum;

    /** 客户名称 */
    private String name;

    /** 联系人 */
    private String contact;

    /** 联系地址 */
    private String contactAddress;

    /** 联系电话 */
    private String contactMobile;

    /** 结算单位 */
    private CustMiniDto checkOutUnit;

    /** 状态 */
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