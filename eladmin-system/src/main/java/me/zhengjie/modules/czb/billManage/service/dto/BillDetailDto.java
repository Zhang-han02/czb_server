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
package me.zhengjie.modules.czb.billManage.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.base.BaseDTO;
import me.zhengjie.modules.czb.archive.service.dto.ProdMiniDto;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description /
 * @date 2021-10-29
 **/
@Data
public class BillDetailDto extends BaseDTO implements Serializable {

    /**
     * 单据详情id
     */
    private Long id;

    /**
     * 单据id
     */
    private Long billId;

    private String billNum;

    /**
     * 产品id
     */
    private Long prodId;
    private ProdMiniDto prod;

    /**
     * 磅台名称
     */
    private String poundName;

    /**
     * 磅单编号
     */
    private String poundBillNum;

    /**
     * 单位
     */
    private String unit;

    /**
     * 辅助单位1
     */
    private String unit1;

    /**
     * 辅助单位2
     */
    private String unit2;

    /**
     * 重量
     */
    private Float weight;

    /**
     * 辅助数量1
     */
    private String amount1;

    /**
     * 辅助数量2
     */
    private String amount2;

    @ApiModelProperty(value = "单价(元)")
    private Float price;

    @ApiModelProperty(value = "总金额(元)")
    private Float amount;

    @ApiModelProperty(value = "称重记录id")
    private Long weigh_id;

}