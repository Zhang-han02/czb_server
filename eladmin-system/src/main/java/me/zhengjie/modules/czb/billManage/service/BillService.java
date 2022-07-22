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
package me.zhengjie.modules.czb.billManage.service;

import me.zhengjie.modules.czb.billManage.domain.Bill;
import me.zhengjie.modules.czb.billManage.service.dto.BillDto;
import me.zhengjie.modules.czb.billManage.service.dto.BillQueryCriteria;
import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author unknown
* @date 2021-10-29
**/
public interface BillService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(BillQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<BillDto>
    */
    List<BillDto> queryAll(BillQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return BillDto
     */
    BillDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return BillDto
    */
    BillDto create(Bill resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(Bill resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Long[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<BillDto> all, HttpServletResponse response) throws IOException;

    /**
     * 汇总
     * @param billType 单据类型 1：采购入库 2：销售出库
     * @param collectType 汇总类型 1：按产品 2：按往来单位 3：按仓库 4：按日期
     * @param pageable
     * @return
     */
    Map<String, Object> collect(Integer billType,Integer collectType, Pageable pageable);

    /**
     * 获取流水号
     * @param billType 单据类型 1：采购入库 2：销售出库
     * @return
     */
    String getSerialNumber(int billType);

    /**
     * 导入
     * @param excelStream 文件输入流
     * @param classType 单据类型 1：采购入库 2：销售出库
     */
    void importExcel(InputStream excelStream, Integer classType);

    /**
     * 模板打印
     * @param idArray id集合
     * @return
     */
    List<Map<String,Object>> print(long[] idArray);

    /**
     * 称重记录生单
     * @param origin 称重记录
     */
    void weigh2bill(Weigh origin);
}