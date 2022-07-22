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
package me.zhengjie.modules.czb.weighSummary.service;

import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighDto;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @description 服务接口
 * @date 2022-01-07
 **/
public interface WeighService {

    /**
     * 查询数据分页
     *
     * @param collectType 汇总类型 1：按产品汇总,2：按往来单位汇总,3：按司磅员汇总,4：按日期汇总,5：按仓库汇总,6：按磅台汇总
     * @param criteria    条件
     * @param pageable    分页参数
     * @return Map<String, Object>
     */
    Map<String, Object> queryAll(Integer collectType, WeighQueryCriteria criteria, Pageable pageable);

    /**
     * 首页地磅称量信息
     * @param pageable
     * @return
     */
    Map<String, Object> findPound(Pageable pageable);

    /**
     * 查询本月产品称重前十
     * @param billType
     * @return
     */
    Map<String, Object> findProd(Integer billType);


    /**
     * 查询年、月、日的过磅重量
     * @param collectType
     * @param criteria
     * @param pageable
     * @return
     */
    Map<String, Object> outPut(Integer collectType, WeighQueryCriteria criteria, Pageable pageable);

    Map<String, Object> prodTopThree(Pageable pageable);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<WeighDto>
     */
    List<WeighDto> queryAll(WeighQueryCriteria criteria);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return WeighDto
     */
    WeighDto findById(Long id);

    /**
     * 创建
     *
     * @param resources /
     * @return WeighDto
     */
    WeighDto create(Weigh resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(Weigh resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void deleteAll(Long[] ids);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<WeighDto> all, HttpServletResponse response) throws IOException;

    Map<String, Object> queryAlls(WeighQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有称重明细
     * @param criteria
     * @param pageable
     * @return
     */
    Map<String, Object> queryDetailAll(WeighQueryCriteria criteria, Pageable pageable);

    /**
     * @param collectType 汇总类型 1：按产品汇总,2：按往来单位汇总,3：按司磅员汇总,4：按日期汇总,5：按仓库汇总,6：按磅台汇总
     * @param criteria
     * @return
     */
    Map<String, Object> generatePrintData(Integer collectType, WeighQueryCriteria criteria);

    void createBill(String ids);
}