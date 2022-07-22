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
package me.zhengjie.modules.czb.archive.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.archive.domain.ClassTree;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.archive.repository.ClassTreeRepository;
import me.zhengjie.modules.czb.archive.service.ClassTreeService;
import me.zhengjie.modules.czb.archive.service.dto.ClassTreeDto;
import me.zhengjie.modules.czb.archive.service.dto.ClassTreeQueryCriteria;
import me.zhengjie.modules.czb.archive.service.mapstruct.ClassTreeMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-09-17
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ClassTreeServiceImpl implements ClassTreeService {

    private final ClassTreeRepository classTreeRepository;
    private final ClassTreeMapper classTreeMapper;

    @Override
    public Map<String, Object> queryAll(ClassTreeQueryCriteria criteria, Pageable pageable) {
        Page<ClassTree> page = classTreeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(classTreeMapper::toDto));
    }

    @Override
    public List<ClassTreeDto> queryAll(ClassTreeQueryCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.ASC, "sort");
        if (criteria.getPid() == null) {
            criteria.setPidIsNull(true);
        }
        List<ClassTreeDto> classes = classTreeMapper.toDto(classTreeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), sort));
        criteria.setPidIsNull(null);
        for (ClassTreeDto item : classes) {
            addChildren(item, criteria, sort);
        }
        return classes;
    }

    private void addChildren(ClassTreeDto target, ClassTreeQueryCriteria criteria, Sort sort) {
        criteria.setPid(target.getId());
        List<ClassTreeDto> children = classTreeMapper.toDto(classTreeRepository.findAll(
                (root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), sort));
        for (ClassTreeDto item : children) {
            addChildren(item, criteria, sort);
        }
        target.setChildren(children);
    }

    @Override
    @Transactional
    public ClassTreeDto findById(Long id) {
        ClassTree classTree = classTreeRepository.findById(id).orElseGet(ClassTree::new);
        ValidationUtil.isNull(classTree.getId(), "ClassTree", "id", id);
        return classTreeMapper.toDto(classTree);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassTreeDto create(ClassTree resources) {
        if (classTreeRepository.existsByClassNumAndTypeAndIdNot(resources.getClassNum(), resources.getType(), -1L)) {
            throw new BadRequestException(String.format("%s 分类编号已存在", resources.getClassNum()));
        }
        return classTreeMapper.toDto(classTreeRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClassTree resources) {
        if (classTreeRepository.existsByClassNumAndTypeAndIdNot(resources.getClassNum(), resources.getType(), resources.getId())) {
            throw new BadRequestException(String.format("%s 分类编号已存在", resources.getClassNum()));
        }
        ClassTree classTree = classTreeRepository.findById(resources.getId()).orElseGet(ClassTree::new);
        ValidationUtil.isNull(classTree.getId(), "ClassTree", "id", resources.getId());
        classTree.copy(resources);

//        classTreeRepository.save(classTree);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            delChildren(id);
            classTreeRepository.deleteById(id);
        }
    }

    private void delChildren(Long id) {
        List<ClassTree> children = classTreeRepository.findByPid(id);
        for (ClassTree item : children) {
            delChildren(item.getId());
            classTreeRepository.deleteById(item.getId());
        }
    }

    @Override
    public void download(List<ClassTreeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        transform(all, list);
        FileUtil.downloadExcel(list, response);
    }

    private void transform(List<ClassTreeDto> source, List<Map<String, Object>> target) {
        if (source == null)
            return;

        for (ClassTreeDto classTree : source) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("分类编号", classTree.getClassNum());
            map.put("分类名称", classTree.getName());
            if (classTree.getPid() != null) {
                ClassTree parent = classTreeRepository.findById(classTree.getPid()).orElse(null);
                if (parent != null) {
                    map.put("上级分类", parent.getName());
                    map.put("上级分类编号", parent.getClassNum());
                }
            }
            map.put("状态", classTree.getEnabled() ? "启用" : "停用");
            String typeName = "";
            switch (classTree.getType()) {
                case 1:
                    typeName = "产品分类";
                    break;
                case 2:
                    typeName = "仓库分类";
                    break;
                case 3:
                    typeName = "客户分类";
                    break;
                case 4:
                    typeName = "供货商分类";
                    break;
            }
            map.put("分类类型", typeName);
            map.put("创建日期", classTree.getCreateTime());
            target.add(map);

            transform(classTree.getChildren(), target);
        }
    }

    @Override
    public void importExcel(InputStream excelStream, int importModel, Integer classType) {
        ExcelReader reader = ExcelUtil.getReader(excelStream, 0);
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.forEach(item -> {
            String classNum = CzbHelper.defaultString(item.get("分类编号"));
            ClassTree old = classTreeRepository.findByClassNumAndType(classNum, classType);
            if (old != null && importModel == 2) {
                //分类编号存在并且是非覆盖导入，则下一条
                return;
            }

            ClassTree classTree = new ClassTree();
            classTree.setClassNum(classNum);
            classTree.setName(CzbHelper.defaultString(item.get("分类名称")));
            String parentNum = CzbHelper.defaultString(item.get("上级分类编号"));
            if (StringUtils.isNotBlank(parentNum)) {
                ClassTree parent = classTreeRepository.findByClassNumAndType(parentNum, classType);
                if (parent != null) {
                    classTree.setPid(parent.getId());
                }
            }
            classTree.setType(classType);

            try {
                if (old == null) {
                    //新增
                    classTree.setEnabled(true);
                    classTree.setSort(999);
                    create(classTree);
                } else {
                    //编辑
                    classTree.setId(old.getId());
                    update(classTree);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        });
    }

    @Override
    public ClassTree findByClassNumAndType(String classNum, Integer type) {
        if (StringUtils.isBlank(classNum) || type == null) {
           return null;
        }
        ClassTree parent = classTreeRepository.findByClassNumAndType(classNum, type);
        return parent;
    }
}