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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constants.ClassTreeType;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.archive.domain.*;
import me.zhengjie.modules.czb.archive.repository.ClassTreeRepository;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.archive.repository.WarehouseRepository;
import me.zhengjie.modules.czb.archive.service.WarehouseService;
import me.zhengjie.modules.czb.archive.service.dto.WarehouseDto;
import me.zhengjie.modules.czb.archive.service.dto.WarehouseQueryCriteria;
import me.zhengjie.modules.czb.archive.service.mapstruct.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-09-17
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final ClassTreeRepository classTreeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(Boolean flag) {
        ErpRequest erpRequest = new ErpRequest();
        JSONObject obj = erpRequest.ErpRequestApi("Stock");
        createWarehouse(obj,flag);
    }

    public List<ErpEntity> createWarehouse(JSONObject object3,Boolean flag){
        if (object3.getInteger("code") == 0) {
            JSONArray array = object3.getJSONArray("response");
            //将JSONArray转为List对象以便使用stream组装成父子的树形结构
            List<ErpEntity> ErpWarehouses = array.toJavaList(ErpEntity.class);
            // 组装成父子的树形结构
            ErpWarehouses.stream().filter(x->x.getParId().equals("0")).
                    peek(menu -> {
                        menu.setErpEntityList(getChildren(menu, ErpWarehouses));
                    }).collect(Collectors.toList());

            List<ErpEntity> ErpWarehouseFor =  ErpWarehouses.get(0).getErpEntityList();
            // 保存树结构到表
            circulation(ErpWarehouseFor,null,flag);

            return ErpWarehouses;

        } else {
            StringBuilder msg = new StringBuilder(object3.getString("message"));
            JSONArray array = object3.getJSONArray("response");
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = array.getJSONObject(i);
                String errormessage = item.getString("errormessage");
                if (i == 0) {
                    msg.append("：");
                } else {
                    msg.append("、");
                }
                msg.append(errormessage);
            }
            throw new RuntimeException(msg.toString());
        }
    }
    // 组装成父子的树形结构 保存到产品表
    public  void circulation(List<ErpEntity> warehouseList,Long id,Boolean flag){
        List<Warehouse> selectWarehouse = warehouseRepository.findAll();
        List<ClassTree> selectClassTree = classTreeRepository.findByType(2);
        try {
            for (ErpEntity warehouses : warehouseList) {
                Long classTreeId = null;
                ClassTree classTree = new ClassTree();
                classTree.setType(2);
                classTree.setName(warehouses.getFullName());
                classTree.setClassNum(warehouses.getUserCode());
                classTree.setEnabled(true);
                classTree.setSort(warehouseList.indexOf(warehouses));
                if (warehouses.getSonnum() > 0) {
                    classTree.setPid(id);
                    //存在则返回
                    ClassTree classTreeOld = selectClassTree.stream().filter(x -> x.getClassNum().equals(classTree.getClassNum()) && x.getType().equals(classTree.getType())).findFirst().orElse(null);
                    if (classTreeOld != null) {
                        if (flag){
                            //如果存在判断此编码下的产品是否修改过其他内容
                            ClassCompareUtil.getNewVo(classTreeOld,classTree,true);
                            classTreeRepository.save(classTreeOld);
                        }
                        classTreeId = classTreeOld.getId();
                    } else {
                        ClassTree classTreeNew = classTreeRepository.save(classTree);
                        classTreeId = classTreeNew.getId();
                    }
                    for (ErpEntity erpWarehouse : warehouses.getErpEntityList()) {
                        Warehouse warehouse = new Warehouse();
                        ClassTree classTree2 = new ClassTree();
                        if (erpWarehouse.getSonnum() == 0) {
                            classTree2.setId(classTreeId);
                            warehouse.setClassInfo(classTree2);
                            warehouse.setName(erpWarehouse.getFullName());
                            warehouse.setWarehouseNum(erpWarehouse.getUserCode());
                            warehouse.setEnabled(true);
                            warehouse.setRemark1(erpWarehouse.getComment1());
                            warehouse.setRemark2(erpWarehouse.getComment2());
                            //保存时判断保存是否存在相同的产品编码
                            Warehouse warehouseOld = selectWarehouse.stream().filter(x -> x.getWarehouseNum().equals(warehouse.getWarehouseNum())).findFirst().orElse(null);
                            if (warehouseOld != null) {
                                if (flag){
                                    ClassCompareUtil.getNewVo(warehouseOld,warehouse,true);
                                    warehouseRepository.save(warehouseOld);
                                }
                            } else {
                                warehouseRepository.save(warehouse);
                            }
                        }
                    }
                    circulation(warehouses.getErpEntityList(),classTreeId,flag);
                } else {
                    // 下面没有子节点且为第一级的直接插入分类和产品
                    if (warehouses.getLeveal() == 1) {
                        classTree.setPid(null);
                        //存在则返回
                        ClassTree classTreeOld = selectClassTree.stream().filter(x -> x.getClassNum().equals(classTree.getClassNum()) && x.getType().equals(classTree.getType())).findFirst().orElse(null);
                        if (classTreeOld != null) {
                            if (flag) {
                                //如果存在判断此编码下的产品是否修改过其他内容
                                ClassCompareUtil.getNewVo(classTreeOld, classTree, true);
                                classTreeRepository.save(classTreeOld);
                            }
                            classTreeId = classTreeOld.getId();
                        } else {
                            ClassTree classTreeNew = classTreeRepository.save(classTree);
                            classTreeId = classTreeNew.getId();
                        }
                        Warehouse warehouse = new Warehouse();
                        warehouse.setClassId(classTreeId);
                        warehouse.setName(warehouses.getFullName());
                        warehouse.setWarehouseNum(warehouses.getUserCode());
                        warehouse.setEnabled(true);
                        warehouse.setRemark1(warehouses.getComment1());
                        warehouse.setRemark2(warehouses.getComment2());
                        //判断是否已经存在
                        Warehouse warehouseOld = selectWarehouse.stream().filter(x -> x.getWarehouseNum().equals(warehouse.getWarehouseNum())).findFirst().orElse(null);
                        if (warehouseOld != null) {
                            if (flag) {
                                ClassCompareUtil.getNewVo(warehouseOld, warehouse, true);
                                warehouseRepository.save(warehouseOld);
                            }
                        } else {
                            warehouseRepository.save(warehouse);
                        }

                    }

                }
            }
        } catch (Exception e){
            throw new BadRequestException("导入客户失败"+e.getMessage());
        }
    }

    // 组装成父子的树形结构
    private List<ErpEntity> getChildren(ErpEntity root, List<ErpEntity> all) {
        return all.stream().filter(m1 -> {
            return m1.getParId().equals(root.getTypeID());
        }).peek(m1 -> {
            m1.setErpEntityList(getChildren(m1, all));
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> queryAll(WarehouseQueryCriteria criteria, Pageable pageable) {
        Page<Warehouse> page = warehouseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(warehouseMapper::toDto));
    }

    @Override
    public List<WarehouseDto> queryAll(WarehouseQueryCriteria criteria) {
        return warehouseMapper.toDto(warehouseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public WarehouseDto findById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseGet(Warehouse::new);
        ValidationUtil.isNull(warehouse.getId(), "Warehouse", "id", id);
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WarehouseDto create(Warehouse resources) {
        if (warehouseRepository.existsByWarehouseNumAndIdNot(resources.getWarehouseNum(), -1L)) {
            throw new BadRequestException(String.format("%s 仓库编号已存在", resources.getWarehouseNum()));
        }
        return warehouseMapper.toDto(warehouseRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Warehouse resources) {
        if (warehouseRepository.existsByWarehouseNumAndIdNot(resources.getWarehouseNum(), resources.getId())) {
            throw new BadRequestException(String.format("%s 仓库编号已存在", resources.getWarehouseNum()));
        }
        Warehouse warehouse = warehouseRepository.findById(resources.getId()).orElseGet(Warehouse::new);
        ValidationUtil.isNull(warehouse.getId(), "Warehouse", "id", resources.getId());
        warehouse.copy(resources);

        //分类可以设置空
        warehouse.setClassId(resources.getClassId());

        warehouseRepository.save(warehouse);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            warehouseRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WarehouseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WarehouseDto warehouse : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("仓库编号", warehouse.getWarehouseNum());
            map.put("仓库名称", warehouse.getName());
            map.put("分类", warehouse.getClassInfo().getName());
            map.put("分类编号", warehouse.getClassInfo().getClassNum());
            map.put("状态", warehouse.getEnabled() ? "启用" : "禁用");
            map.put("备注1", warehouse.getRemark1());
            map.put("备注2", warehouse.getRemark2());
            map.put("创建日期", warehouse.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void importExcel(InputStream excelStream, int importModel) {
        ExcelReader reader = ExcelUtil.getReader(excelStream, 0);
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.forEach(item -> {
            String warehouseNum = CzbHelper.defaultString(item.get("仓库编号"));
            Warehouse old = warehouseRepository.findByWarehouseNum(warehouseNum);
            if (old != null && importModel == 2) {
                //分类编号存在并且是非覆盖导入，则下一条
                return;
            }

            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseNum(warehouseNum);
            warehouse.setName(CzbHelper.defaultString(item.get("仓库名称")));
            //分类
            String classNum = CzbHelper.defaultString(item.get("分类编号"));
            if (StringUtils.isNotBlank(classNum)) {
                ClassTree classTree = classTreeRepository.findByClassNumAndType(classNum, ClassTreeType.TYPE_WAREHOUSE);
                if (classTree != null) {
                    warehouse.setClassId(classTree.getId());
                }
            }

            warehouse.setRemark1(CzbHelper.defaultString(item.get("备注1")));
            warehouse.setRemark2(CzbHelper.defaultString(item.get("备注2")));

            try {
                if (old == null) {
                    //新增
                    warehouse.setEnabled(true);
                    create(warehouse);
                } else {
                    //编辑
                    warehouse.setId(old.getId());
                    update(warehouse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        });
    }

    @Override
    public Warehouse findByWarehouseNum(String warehouseNum) {
        if (StringUtils.isBlank(warehouseNum)) {
            return null;
        }
        return warehouseRepository.findByWarehouseNum(warehouseNum);
    }
}