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

import me.zhengjie.modules.czb.billManage.domain.BillDetail;
import me.zhengjie.modules.czb.billManage.service.dto.BillCollectDetailQueryCriteria;
import me.zhengjie.modules.czb.billManage.service.dto.BillDetailDto;
import me.zhengjie.modules.czb.billManage.service.dto.BillDetailQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务接口
 * @date 2021-10-29
 **/
public interface BillDetailService {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String, Object>
     */
    Map<String, Object> queryAll(BillDetailQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<BillDetailDto>
     */
    List<BillDetailDto> queryAll(BillDetailQueryCriteria criteria);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return BillDetailDto
     */
    BillDetailDto findById(Long id);

    /**
     * 创建
     *
     * @param resources /
     * @return BillDetailDto
     */
    BillDetailDto create(BillDetail resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(BillDetail resources);

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
    void download(List<BillDetailDto> all, HttpServletResponse response) throws IOException;

    Map<String, Object> collectDetail(BillCollectDetailQueryCriteria queryCriteria, Pageable pageable);
}