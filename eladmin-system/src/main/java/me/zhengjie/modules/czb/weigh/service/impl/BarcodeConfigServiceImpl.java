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
package me.zhengjie.modules.czb.weigh.service.impl;

import me.zhengjie.modules.czb.weigh.domain.BarcodeConfig;
import me.zhengjie.modules.czb.weigh.domain.ServerConfig;
import me.zhengjie.modules.czb.weigh.service.dto.ServerConfigDto;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.weigh.repository.BarcodeConfigRepository;
import me.zhengjie.modules.czb.weigh.service.BarcodeConfigService;
import me.zhengjie.modules.czb.weigh.service.dto.BarcodeConfigDto;
import me.zhengjie.modules.czb.weigh.service.dto.BarcodeConfigQueryCriteria;
import me.zhengjie.modules.czb.weigh.service.mapstruct.BarcodeConfigMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
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
* @date 2022-01-07
**/
@Service
@RequiredArgsConstructor
public class BarcodeConfigServiceImpl implements BarcodeConfigService {

    private final BarcodeConfigRepository barcodeConfigRepository;
    private final BarcodeConfigMapper barcodeConfigMapper;

    @Override
    public Map<String,Object> queryAll(BarcodeConfigQueryCriteria criteria, Pageable pageable){
        Page<BarcodeConfig> page = barcodeConfigRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(barcodeConfigMapper::toDto));
    }

    @Override
    public List<BarcodeConfigDto> queryAll(BarcodeConfigQueryCriteria criteria){
        return barcodeConfigMapper.toDto(barcodeConfigRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public BarcodeConfigDto findById(Long id) {
        BarcodeConfig barcodeConfig = barcodeConfigRepository.findById(id).orElseGet(BarcodeConfig::new);
        ValidationUtil.isNull(barcodeConfig.getId(),"BarcodeConfig","id",id);
        return barcodeConfigMapper.toDto(barcodeConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BarcodeConfigDto create(BarcodeConfig resources) {
        return barcodeConfigMapper.toDto(barcodeConfigRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BarcodeConfig resources) {
        BarcodeConfig barcodeConfig = barcodeConfigRepository.findById(resources.getId()).orElseGet(BarcodeConfig::new);
        ValidationUtil.isNull( barcodeConfig.getId(),"BarcodeConfig","id",resources.getId());
        barcodeConfig.copy(resources);
        barcodeConfigRepository.save(barcodeConfig);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            barcodeConfigRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BarcodeConfigDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BarcodeConfigDto barcodeConfig : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("前缀类型", barcodeConfig.getPrefixType());
            map.put("前缀固定字符", barcodeConfig.getPrefixValue());
            map.put("后缀类型", barcodeConfig.getSuffixType());
            map.put("后缀固定字符", barcodeConfig.getSuffixValue());
            map.put("流水号类型", barcodeConfig.getSerialNumberType());
            map.put("创建者", barcodeConfig.getCreateBy());
            map.put("更新者", barcodeConfig.getUpdateBy());
            map.put("创建日期", barcodeConfig.getCreateTime());
            map.put("更新时间", barcodeConfig.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public BarcodeConfigDto queryLatest() {
        List<BarcodeConfig> list = barcodeConfigRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        if (list.size() == 0) {
            return new BarcodeConfigDto();
        } else {
            return barcodeConfigMapper.toDto(list.get(0));
        }
    }

    @Override
    public void save(BarcodeConfig resources) {
        if (resources.getId() != null) {
            update(resources);
        } else {
            create(resources);
        }
    }
}