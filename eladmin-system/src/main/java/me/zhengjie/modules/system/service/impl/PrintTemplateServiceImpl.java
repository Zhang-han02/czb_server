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
package me.zhengjie.modules.system.service.impl;

import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.weigh.domain.BarcodeConfig;
import me.zhengjie.modules.czb.weigh.service.dto.BarcodeConfigDto;
import me.zhengjie.modules.system.domain.PrintTemplate;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.system.repository.PrintTemplateRepository;
import me.zhengjie.modules.system.service.PrintTemplateService;
import me.zhengjie.modules.system.service.dto.PrintTemplateDto;
import me.zhengjie.modules.system.service.dto.PrintTemplateQueryCriteria;
import me.zhengjie.modules.system.service.mapstruct.PrintTemplateMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author unknown
* @date 2022-01-28
**/
@Service
@RequiredArgsConstructor
public class PrintTemplateServiceImpl implements PrintTemplateService {

    private final PrintTemplateRepository printTemplateRepository;
    private final PrintTemplateMapper printTemplateMapper;

    @Override
    public Map<String,Object> queryAll(PrintTemplateQueryCriteria criteria, Pageable pageable){
        Page<PrintTemplate> page = printTemplateRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(printTemplateMapper::toDto));
    }

    @Override
    public List<PrintTemplateDto> queryAll(PrintTemplateQueryCriteria criteria){
        return printTemplateMapper.toDto(printTemplateRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PrintTemplateDto findById(Long id) {
        PrintTemplate printTemplate = printTemplateRepository.findById(id).orElseGet(PrintTemplate::new);
        ValidationUtil.isNull(printTemplate.getId(),"PrintTemplate","id",id);
        return printTemplateMapper.toDto(printTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrintTemplateDto create(PrintTemplate resources) {
        return printTemplateMapper.toDto(printTemplateRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PrintTemplate resources) {
        PrintTemplate printTemplate = printTemplateRepository.findById(resources.getId()).orElseGet(PrintTemplate::new);
        ValidationUtil.isNull( printTemplate.getId(),"PrintTemplate","id",resources.getId());
        printTemplate.copy(resources);
        printTemplateRepository.save(printTemplate);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            printTemplateRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PrintTemplateDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PrintTemplateDto printTemplate : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("纸张类型", printTemplate.getPaperType());
            map.put("纸张宽度", printTemplate.getPaperWidth());
            map.put("纸张高度", printTemplate.getPaperHeight());
            map.put("模板内容", printTemplate.getContent());
            map.put("创建者", printTemplate.getCreateBy());
            map.put("更新者", printTemplate.getUpdateBy());
            map.put("创建日期", printTemplate.getCreateTime());
            map.put("更新时间", printTemplate.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public PrintTemplateDto queryDetail(PrintTemplateQueryCriteria criteria) {
        List<PrintTemplate> list = printTemplateRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        if (list.size() == 0) {
            return new PrintTemplateDto().setKey(criteria.getKey());
        } else {
            return printTemplateMapper.toDto(list.get(0));
        }
    }

    @Override
    public void save(PrintTemplate resources) {
        if (StringUtils.isBlank(resources.getKey())){
            throw new BadRequestException("缺少模板key");
        }
        if (resources.getId() != null) {
            update(resources);
        } else {
            create(resources);
        }
    }
}