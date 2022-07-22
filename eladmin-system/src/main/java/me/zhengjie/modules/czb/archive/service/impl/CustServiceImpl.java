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
import me.zhengjie.modules.czb.archive.repository.CustRepository;
import me.zhengjie.modules.czb.archive.service.CustService;
import me.zhengjie.modules.czb.archive.service.dto.CustDto;
import me.zhengjie.modules.czb.archive.service.dto.CustQueryCriteria;
import me.zhengjie.modules.czb.archive.service.mapstruct.CustMapper;
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
 * @date 2021-10-15
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class CustServiceImpl implements CustService {

    private final CustRepository custRepository;
    private final CustMapper custMapper;
    private final ClassTreeRepository classTreeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(Boolean flag) {
        ErpRequest erpRequest = new ErpRequest();
        JSONObject obj = erpRequest.ErpRequestApi("customer");
        createCust(obj,flag);

        System.out.println("客户信息："+obj);
    }

    public List<ErpEntity> createCust(JSONObject object3,Boolean flag){
        if (object3.getInteger("code") == 0) {
            JSONArray array = object3.getJSONArray("response");
            //将JSONArray转为List对象以便使用stream组装成父子的树形结构
            List<ErpEntity> ErpCusts = array.toJavaList(ErpEntity.class);
            // 因为客户、供应商为同一张表查询客户是没有根节点
            ErpEntity erpCust = new ErpEntity();
            erpCust.setTypeID("00000");
            erpCust.setParId("0");
            ErpCusts.add(0,erpCust);
            // 组装成父子的树形结构
            ErpCusts.stream().filter(x->x.getParId().equals("0")).
                    peek(menu -> {
                        menu.setErpEntityList(getChildren(menu, ErpCusts));
                    }).collect(Collectors.toList());

            List<ErpEntity> ErpCustFor =  ErpCusts.get(0).getErpEntityList();
            // 保存树结构到表
            circulation(ErpCustFor,null,flag);

            return ErpCusts;

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
    // 组装成父子的树形结构
    private List<ErpEntity> getChildren(ErpEntity root, List<ErpEntity> all) {
        return all.stream().filter(m1 -> {
            return m1.getParId().equals(root.getTypeID());
        }).peek(m1 -> {
            m1.setErpEntityList(getChildren(m1, all));
        }).collect(Collectors.toList());
    }
    // 组装成父子的树形结构 保存到产品表
    public  void circulation(List<ErpEntity> custList,Long id,Boolean flag){
        List<Cust> selectCust = custRepository.findAll();
        List<ClassTree> selectClassTree = classTreeRepository.findByType(3);
        try {
            for (ErpEntity custs : custList) {
                Long classTreeId = null;
                ClassTree classTree = new ClassTree();
                classTree.setType(3);
                classTree.setName(custs.getFullName());
                classTree.setClassNum(custs.getUserCode());
                classTree.setEnabled(true);
                classTree.setSort(custList.indexOf(custs));
                if (custs.getSonnum() > 0) {
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
                    for (ErpEntity erpCust : custs.getErpEntityList()) {
                        Cust cust = new Cust();
                        ClassTree classTree2 = new ClassTree();
                        if (erpCust.getSonnum() == 0) {
                            classTree2.setId(classTreeId);
                            cust.setClassInfo(classTree2);
                            cust.setName("".equals(erpCust.getFullName()) ? "-" : erpCust.getFullName());
                            cust.setCustNum("".equals(erpCust.getUserCode()) ? "-" : erpCust.getUserCode());
                            cust.setContact("".equals(erpCust.getPerson()) ? "-" : erpCust.getPerson());
                            cust.setContactAddress("".equals(erpCust.getTel()) ? "-" : erpCust.getTel());
                            cust.setContactMobile("".equals(erpCust.getTaxnumber()) ? "-" : erpCust.getTaxnumber());
                            cust.setEnabled(true);
                            cust.setRemark1(erpCust.getComment1());
                            cust.setRemark2(erpCust.getComment2());
                            cust.setRemark3(erpCust.getComment3());
                            cust.setRemark4(erpCust.getComment4());
                            cust.setRemark5(erpCust.getComment5());
                            cust.setRemark6(erpCust.getComment6());
                            //保存时判断保存是否存在相同的产品编码
                            Cust custOld = selectCust.stream().filter(x -> x.getCustNum().equals(cust.getCustNum())).findFirst().orElse(null);
                            if ( custOld != null ) {
                                if (flag){
                                    ClassCompareUtil.getNewVo(custOld,cust,true);
                                    custRepository.save(custOld);
                                }
                            } else {
                                custRepository.save(cust);
                            }
                        }
                    }
                    circulation(custs.getErpEntityList(),classTreeId,flag);
                } else {
                    // 下面没有子节点且为第一级的直接插入分类和产品
                    if (custs.getLeveal() == 1) {
                        classTree.setPid(null);
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
                        ClassTree classTree2 = new ClassTree();
                        Cust cust = new Cust();
                        classTree2.setId(classTreeId);
                        cust.setClassInfo(classTree2);
                        cust.setName("".equals(custs.getFullName()) ? "-" : custs.getFullName());
                        cust.setCustNum("".equals(custs.getUserCode()) ? "-" : custs.getUserCode());
                        cust.setContact("".equals(custs.getPerson()) ? "-" : custs.getPerson());
                        cust.setContactAddress("".equals(custs.getTel()) ? "-" : custs.getTel());
                        cust.setContactMobile("".equals(custs.getTaxnumber()) ? "-" : custs.getTaxnumber());
                        cust.setEnabled(true);
                        cust.setRemark1(custs.getComment1());
                        cust.setRemark2(custs.getComment2());
                        cust.setRemark3(custs.getComment3());
                        cust.setRemark4(custs.getComment4());
                        cust.setRemark5(custs.getComment5());
                        cust.setRemark6(custs.getComment6());
                        //判断是否已经存在
                        Cust custOld = selectCust.stream().filter(x -> x.getCustNum().equals(cust.getCustNum())).findFirst().orElse(null);
                        if ( custOld != null ) {
                            if (flag){
                                ClassCompareUtil.getNewVo(custOld,cust,true);
                                custRepository.save(custOld);
                            }
                        } else {
                            custRepository.save(cust);
                        }

                    }

                }
            }
        } catch (Exception e){
            throw new BadRequestException("导入客户失败"+e.getMessage());
        }
    }



    @Override
    public Map<String, Object> queryAll(CustQueryCriteria criteria, Pageable pageable) {
        Page<Cust> page = custRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(custMapper::toDto));
    }

    @Override
    public List<CustDto> queryAll(CustQueryCriteria criteria) {
        return custMapper.toDto(custRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustDto findById(Long id) {
        Cust cust = custRepository.findById(id).orElseGet(Cust::new);
        ValidationUtil.isNull(cust.getId(), "Cust", "id", id);
        return custMapper.toDto(cust);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustDto create(Cust resources) {
        if (resources.getClassInfo().getId() == null) {
            resources.setClassInfo(null);
        }
        if (resources.getCheckOutUnit().getId() == null) {
            resources.setCheckOutUnit(null);
        }
        if (custRepository.existsByCustNumAndIdNot(resources.getCustNum(), -1L)) {
            throw new BadRequestException(String.format("%s 客户编号已存在", resources.getCustNum()));
        }
        return custMapper.toDto(custRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Cust resources) {
        if (custRepository.existsByCustNumAndIdNot(resources.getCustNum(), resources.getId())) {
            throw new BadRequestException(String.format("%s 客户编号已存在", resources.getCustNum()));
        }
        Cust cust = custRepository.findById(resources.getId()).orElseGet(Cust::new);
        ValidationUtil.isNull(cust.getId(), "Cust", "id", resources.getId());
        cust.copy(resources);
        if (cust.getClassInfo().getId() == null) {
            cust.setClassInfo(null);
        }
        if (cust.getCheckOutUnit().getId() == null) {
            cust.setCheckOutUnit(null);
        }
        custRepository.save(cust);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            custRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustDto cust : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("客户编号", cust.getCustNum());
            map.put("客户名称", cust.getName());
            map.put("分类", cust.getClassInfo().getName());
            map.put("分类编号", cust.getClassInfo().getClassNum());
            map.put("联系人", cust.getContact());
            map.put("联系地址", cust.getContactAddress());
            map.put("联系电话", cust.getContactMobile());
            map.put("结算单位", cust.getCheckOutUnit().getName());
            map.put("结算单位编号", cust.getCheckOutUnit().getCustNum());
            map.put("状态", cust.getEnabled() ? "启用" : "禁用");
            map.put("备注1", cust.getRemark1());
            map.put("备注2", cust.getRemark2());
            map.put("备注3", cust.getRemark3());
            map.put("备注4", cust.getRemark4());
            map.put("备注5", cust.getRemark5());
            map.put("备注6", cust.getRemark6());
            map.put("创建日期", cust.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void importExcel(InputStream excelStream, int importModel) {
        ExcelReader reader = ExcelUtil.getReader(excelStream, 0);
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.forEach(item -> {
            String custNum = CzbHelper.defaultString(item.get("客户编号"));
            Cust old = custRepository.findByCustNum(custNum);
            if (old != null && importModel == 2) {
                //编号存在并且是非覆盖导入，则下一条
                return;
            }

            Cust cust = new Cust();
            cust.setCustNum(custNum);
            cust.setName(CzbHelper.defaultString(item.get("客户名称")));
            //分类
            String classNum = CzbHelper.defaultString(item.get("分类编号"));
            if (StringUtils.isNotBlank(classNum)) {
                ClassTree classTree = classTreeRepository.findByClassNumAndType(classNum, ClassTreeType.TYPE_CUST);
                if (classTree != null) {
                    cust.setClassInfo(classTree);
                }
            }
            cust.setContact(CzbHelper.defaultString(item.get("联系人")));
            cust.setContactAddress(CzbHelper.defaultString(item.get("联系地址")));
            cust.setContactMobile(CzbHelper.defaultString(item.get("联系电话")));
            //结算单位
            String checkOutUnitNum = CzbHelper.defaultString(item.get("结算单位编号"));
            if (StringUtils.isNotBlank(checkOutUnitNum)) {
                Cust checkOutUnit = custRepository.findByCustNum(checkOutUnitNum);
                if (checkOutUnit != null) {
                    cust.setCheckOutUnit(checkOutUnit);
                }
            }
            cust.setRemark1(CzbHelper.defaultString(item.get("备注1")));
            cust.setRemark2(CzbHelper.defaultString(item.get("备注2")));
            cust.setRemark3(CzbHelper.defaultString(item.get("备注3")));
            cust.setRemark4(CzbHelper.defaultString(item.get("备注4")));
            cust.setRemark5(CzbHelper.defaultString(item.get("备注5")));
            cust.setRemark6(CzbHelper.defaultString(item.get("备注6")));

            try {
                if (old == null) {
                    //新增
                    cust.setEnabled(true);
                    create(cust);
                } else {
                    //编辑
                    cust.setId(old.getId());
                    update(cust);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        });
    }

    @Override
    public Cust findByCustNum(String custNum) {
        if (StringUtils.isBlank(custNum)) {
            return null;
        }
        return custRepository.findByCustNum(custNum);
    }
}