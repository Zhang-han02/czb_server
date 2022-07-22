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
import me.zhengjie.modules.czb.archive.domain.ClassTree;
import me.zhengjie.modules.czb.archive.domain.ErpEntity;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import me.zhengjie.modules.czb.archive.repository.ClassTreeRepository;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.archive.repository.SupplierRepository;
import me.zhengjie.modules.czb.archive.service.SupplierService;
import me.zhengjie.modules.czb.archive.service.dto.SupplierDto;
import me.zhengjie.modules.czb.archive.service.dto.SupplierQueryCriteria;
import me.zhengjie.modules.czb.archive.service.mapstruct.SupplierMapper;
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
 * @date 2021-10-28
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final ClassTreeRepository classTreeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(Boolean flag) {
        ErpRequest erpRequest = new ErpRequest();
        JSONObject obj = erpRequest.ErpRequestApi("supplier");
        createSupplier(obj,flag);

    }

    public List<ErpEntity> createSupplier(JSONObject object3,Boolean flag){
        if (object3.getInteger("code") == 0) {
            JSONArray array = object3.getJSONArray("response");
            //将JSONArray转为List对象以便使用stream组装成父子的树形结构
            List<ErpEntity> ErpSuppliers = array.toJavaList(ErpEntity.class);
            // 组装成父子的树形结构
            ErpSuppliers.stream().filter(x->x.getParId().equals("0")).
                    peek(menu -> {
                        menu.setErpEntityList(getChildren(menu, ErpSuppliers));
                    }).collect(Collectors.toList());

            List<ErpEntity> ErpSupplierFor =  ErpSuppliers.get(0).getErpEntityList();
            // 保存树结构到表
            circulation(ErpSupplierFor,null,flag);

            return ErpSuppliers;

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
    public  void circulation(List<ErpEntity> supplierList,Long id,Boolean flag){
        List<Supplier> selectSupplier = supplierRepository.findAll();
        List<ClassTree> selectClassTree = classTreeRepository.findByType(4);
        try {
            for (ErpEntity suppliers : supplierList) {
                Long classTreeId = null;
                ClassTree classTree = new ClassTree();
                classTree.setName(suppliers.getFullName());
                classTree.setClassNum(suppliers.getUserCode());
                classTree.setType(4);classTree.setName(suppliers.getFullName());
                classTree.setClassNum(suppliers.getUserCode());
                classTree.setEnabled(true);
                classTree.setSort(supplierList.indexOf(suppliers));
                if (suppliers.getSonnum() > 0) {
                    classTree.setPid(id);
                    //存在则返回
                    ClassTree classTreeOld = selectClassTree.stream().filter(x -> x.getClassNum().equals(classTree.getClassNum()) && x.getType().equals(classTree.getType())).findFirst().orElse(null);
                    if (classTreeOld != null) {
                        // 是否更新以前的内容
                        if (flag){
                            //如果存在判断此编码下的产品是否修改过其他内容
                            ClassCompareUtil.getNewVo(classTreeOld, classTree, true);
                            classTreeRepository.save(classTreeOld);
                        }
                        classTreeId = classTreeOld.getId();
                    } else {
                        ClassTree classTreeNew = classTreeRepository.save(classTree);
                        classTreeId = classTreeNew.getId();
                    }
                    for (ErpEntity erpSupplier : suppliers.getErpEntityList()) {
                        Supplier supplier = new Supplier();
                        ClassTree classTree2 = new ClassTree();
                        if (erpSupplier.getSonnum() == 0) {
                            classTree2.setId(classTreeId);
                            supplier.setClassInfo(classTree2);
                            supplier.setName("".equals(erpSupplier.getFullName()) ? "-" : erpSupplier.getFullName());
                            supplier.setSupplierNum("".equals(erpSupplier.getUserCode()) ? "-" : erpSupplier.getUserCode());
                            supplier.setContact("".equals(erpSupplier.getPerson()) ? "-" : erpSupplier.getPerson());
                            supplier.setContactAddress("".equals(erpSupplier.getTel()) ? "-" : erpSupplier.getTel());
                            supplier.setContactMobile("".equals(erpSupplier.getTaxnumber()) ? "-" : erpSupplier.getTaxnumber());
                            supplier.setEnabled(true);
                            supplier.setRemark1(erpSupplier.getComment1());
                            supplier.setRemark2(erpSupplier.getComment2());
                            supplier.setRemark3(erpSupplier.getComment3());
                            supplier.setRemark4(erpSupplier.getComment4());
                            supplier.setRemark5(erpSupplier.getComment5());
                            supplier.setRemark6(erpSupplier.getComment6());
                            //保存时判断保存是否存在相同的产品编码
                            Supplier supplierOld = selectSupplier.stream().filter(x -> x.getSupplierNum().equals(supplier.getSupplierNum())).findFirst().orElse(null);
                            if ( supplierOld != null ) {
                                if (flag){
                                    ClassCompareUtil.getNewVo(supplierOld,supplier,true);
                                    supplierRepository.save(supplierOld);
                                }
                            } else {
                                supplierRepository.save(supplier);
                            }
                        }
                    }
                    circulation(suppliers.getErpEntityList(),classTreeId,flag);
                } else {
                    // 下面没有子节点且为第一级的直接插入分类和产品
                    if (suppliers.getLeveal() == 1) {
                        classTree.setPid(null);
                        //存在则返回
                        ClassTree classTreeOld = selectClassTree.stream().filter(x -> x.getClassNum().equals(classTree.getClassNum()) && x.getType().equals(classTree.getType())).findFirst().orElse(null);
                        if (classTreeOld != null) {
                            if (flag){
                                //如果存在判断此编码下的产品是否修改过其他内容
                                ClassCompareUtil.getNewVo(classTreeOld, classTree, true);
                                classTreeRepository.save(classTreeOld);
                            }
                            classTreeId = classTreeOld.getId();
                        } else {
                            ClassTree classTreeNew = classTreeRepository.save(classTree);
                            classTreeId = classTreeNew.getId();
                        }
                        Supplier supplier = new Supplier();
                        supplier.setClassId(classTreeId);
                        supplier.setName("".equals(suppliers.getFullName()) ? "-" : suppliers.getFullName());
                        supplier.setSupplierNum("".equals(suppliers.getUserCode()) ? "-" : suppliers.getUserCode());
                        supplier.setContact("".equals(suppliers.getPerson()) ? "-" : suppliers.getPerson());
                        supplier.setContactAddress("".equals(suppliers.getTel()) ? "-" : suppliers.getTel());
                        supplier.setContactMobile("".equals(suppliers.getTaxnumber()) ? "-" : suppliers.getTaxnumber());
                        supplier.setEnabled(true);
                        supplier.setRemark1(suppliers.getComment1());
                        supplier.setRemark2(suppliers.getComment2());
                        supplier.setRemark3(suppliers.getComment3());
                        supplier.setRemark4(suppliers.getComment4());
                        supplier.setRemark5(suppliers.getComment5());
                        supplier.setRemark6(suppliers.getComment6());
                        //判断是否已经存在
                        Supplier supplierOld = selectSupplier.stream().filter(x -> x.getSupplierNum().equals(supplier.getSupplierNum())).findFirst().orElse(null);
                        if ( supplierOld != null ) {
                            if (flag){
                                ClassCompareUtil.getNewVo(supplierOld,supplier,true);
                                supplierRepository.save(supplierOld);
                            }
                        } else {
                            supplierRepository.save(supplier);
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
    public Map<String, Object> queryAll(SupplierQueryCriteria criteria, Pageable pageable) {
        Page<Supplier> page = supplierRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(supplierMapper::toDto));
    }

    @Override
    public List<SupplierDto> queryAll(SupplierQueryCriteria criteria) {
        return supplierMapper.toDto(supplierRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public SupplierDto findById(Long id) {
        Supplier supplier = supplierRepository.findById(id).orElseGet(Supplier::new);
        ValidationUtil.isNull(supplier.getId(), "Supplier", "id", id);
        return supplierMapper.toDto(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupplierDto create(Supplier resources) {
        if (supplierRepository.existsBySupplierNumAndIdNot(resources.getSupplierNum(), -1L)) {
            throw new BadRequestException(String.format("%s 供应商编号已存在", resources.getSupplierNum()));
        }
        return supplierMapper.toDto(supplierRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Supplier resources) {
        if (supplierRepository.existsBySupplierNumAndIdNot(resources.getSupplierNum(), resources.getId())) {
            throw new BadRequestException(String.format("%s 供应商编号已存在", resources.getSupplierNum()));
        }
        Supplier supplier = supplierRepository.findById(resources.getId()).orElseGet(Supplier::new);
        ValidationUtil.isNull(supplier.getId(), "Supplier", "id", resources.getId());
        supplier.copy(resources);

        //分类、结算单位可以设置空
        supplier.setClassId(resources.getClassId());
        supplier.setCheckOutUnit(resources.getCheckOutUnit());

        supplierRepository.save(supplier);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            supplierRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SupplierDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SupplierDto supplier : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("供应商编号", supplier.getSupplierNum());
            map.put("供应商名称", supplier.getName());
            map.put("分类", supplier.getClassInfo().getName());
            map.put("分类编号", supplier.getClassInfo().getClassNum());
            map.put("联系人", supplier.getContact());
            map.put("联系地址", supplier.getContactAddress());
            map.put("联系电话", supplier.getContactMobile());
            map.put("结算单位", supplier.getCheckOutUnitInfo().getName());
            map.put("结算单位编号", supplier.getCheckOutUnitInfo().getSupplierNum());
            map.put("状态", supplier.getEnabled() ? "启用" : "禁用");
            map.put("备注1", supplier.getRemark1());
            map.put("备注2", supplier.getRemark2());
            map.put("备注3", supplier.getRemark3());
            map.put("备注4", supplier.getRemark4());
            map.put("备注5", supplier.getRemark5());
            map.put("备注6", supplier.getRemark6());
            map.put("创建日期", supplier.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void importExcel(InputStream excelStream, int importModel) {
        ExcelReader reader = ExcelUtil.getReader(excelStream, 0);
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.forEach(item -> {
            String supplierNum = CzbHelper.defaultString(item.get("供应商编号"));
            Supplier old = supplierRepository.findBySupplierNum(supplierNum);
            if (old != null && importModel == 2) {
                //编号存在并且是非覆盖导入，则下一条
                return;
            }

            Supplier supplier = new Supplier();
            supplier.setSupplierNum(supplierNum);
            supplier.setName(CzbHelper.defaultString(item.get("供应商名称")));
            //分类
            String classNum = CzbHelper.defaultString(item.get("分类编号"));
            if (StringUtils.isNotBlank(classNum)) {
                ClassTree classTree = classTreeRepository.findByClassNumAndType(classNum, ClassTreeType.TYPE_SUPPLIER);
                if (classTree != null) {
                    supplier.setClassId(classTree.getId());
                }
            }
            supplier.setContact(CzbHelper.defaultString(item.get("联系人")));
            supplier.setContactAddress(CzbHelper.defaultString(item.get("联系地址")));
            supplier.setContactMobile(CzbHelper.defaultString(item.get("联系电话")));
            //结算单位
            String checkOutUnitNum = CzbHelper.defaultString(item.get("结算单位编号"));
            if (StringUtils.isNotBlank(checkOutUnitNum)) {
                Supplier checkOutUnit = supplierRepository.findBySupplierNum(checkOutUnitNum);
                if (checkOutUnit != null) {
                    supplier.setCheckOutUnit(checkOutUnit.getId());
                }
            }

            supplier.setRemark1(CzbHelper.defaultString(item.get("备注1")));
            supplier.setRemark2(CzbHelper.defaultString(item.get("备注2")));
            supplier.setRemark3(CzbHelper.defaultString(item.get("备注3")));
            supplier.setRemark4(CzbHelper.defaultString(item.get("备注4")));
            supplier.setRemark5(CzbHelper.defaultString(item.get("备注5")));
            supplier.setRemark6(CzbHelper.defaultString(item.get("备注6")));

            try {
                if (old == null) {
                    //新增
                    supplier.setEnabled(true);
                    create(supplier);
                } else {
                    //编辑
                    supplier.setId(old.getId());
                    update(supplier);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        });
    }

    @Override
    public Supplier findBySupplierNum(String supplierNum) {
        if (StringUtils.isBlank(supplierNum)) {
            return null;
        }
        return supplierRepository.findBySupplierNum(supplierNum);
    }
}