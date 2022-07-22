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
package me.zhengjie.modules.czb.archive.service;

import me.zhengjie.modules.czb.archive.domain.Cust;
import me.zhengjie.modules.czb.archive.service.dto.CustDto;
import me.zhengjie.modules.czb.archive.service.dto.CustQueryCriteria;
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
* @date 2021-10-15
**/
public interface CustService {
    /**
     * ERP数据导入
     */
    void importData(Boolean flag);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(CustQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<CustDto>
    */
    List<CustDto> queryAll(CustQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return CustDto
     */
    CustDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return CustDto
    */
    CustDto create(Cust resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(Cust resources);

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
    void download(List<CustDto> all, HttpServletResponse response) throws IOException;

    /**
     * 导入
     * @param excelStream 文件输入流
     * @param importModel 导入模式 1：覆盖导入 2：非覆盖导入
     */
    void importExcel(InputStream excelStream, int importModel);

    /**
     * 根据客户编号查询
     * @param custNum 客户编号
     * @return
     */
    Cust findByCustNum(String custNum);
}