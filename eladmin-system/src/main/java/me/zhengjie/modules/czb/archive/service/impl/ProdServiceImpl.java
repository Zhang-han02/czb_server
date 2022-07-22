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
import me.zhengjie.modules.czb.archive.domain.Prod;
import me.zhengjie.modules.czb.archive.repository.ClassTreeRepository;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.archive.repository.ProdRepository;
import me.zhengjie.modules.czb.archive.service.ProdService;
import me.zhengjie.modules.czb.archive.service.dto.ProdDto;
import me.zhengjie.modules.czb.archive.service.dto.ProdQueryCriteria;
import me.zhengjie.modules.czb.archive.service.mapstruct.ProdMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-09-23
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ProdServiceImpl implements ProdService {

    private final ProdRepository prodRepository;
    private final ProdMapper prodMapper;
    private final ClassTreeRepository classTreeRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(Boolean flag){
        ErpRequest erpRequest = new ErpRequest();
        JSONObject obj = erpRequest.ErpRequestApi("Ptype");
                //获取erp数据进行插入
        createProd(obj,flag);
    }

    /**
     * 插入产品
     * @param object3
     */
    public List<ErpEntity> createProd(JSONObject object3,Boolean flag){
        if (object3.getInteger("code") == 0) {
            JSONArray array = object3.getJSONArray("response");
            //将JSONArray转为List对象以便使用stream组装成父子的树形结构
            List<ErpEntity> ErpEntitys = array.toJavaList(ErpEntity.class);
            // 组装成父子的树形结构
            ErpEntitys.stream().filter(x->x.getParId().equals("0")).
                    peek(menu -> {
                        menu.setErpEntityList(getChildren(menu, ErpEntitys));
                    }).collect(Collectors.toList());

            List<ErpEntity> ErpEntityFor =  ErpEntitys.get(0).getErpEntityList();
            // 保存树结构到表
            circulation(ErpEntityFor,null,flag);

             return ErpEntitys;

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
    public  void circulation(List<ErpEntity> prodList,Long id,Boolean flag){
        List<Prod> selectProd = prodRepository.findAll();
        List<ClassTree> selectClassTree = classTreeRepository.findByType(1);
        try {
            for (ErpEntity prods : prodList) {
                Long classTreeId = null;
                ClassTree classTree = new ClassTree();
                classTree.setType(1);
                classTree.setName(prods.getFullName());
                classTree.setClassNum(prods.getUserCode());
                classTree.setEnabled(true);
                classTree.setSort(prodList.indexOf(prods));
                if (prods.getSonnum() > 0) {
                    classTree.setPid(id);
                    //存在则返回
                    ClassTree classTreeOld = selectClassTree.stream().filter(x -> x.getClassNum().equals(classTree.getClassNum()) && x.getType().equals(classTree.getType())).findFirst().orElse(null);
                    if (classTreeOld != null) {
                        //如果存在判断此编码下的产品是否修改过其他内容
                        if(flag){
                            ClassCompareUtil.getNewVo(classTreeOld, classTree, true);
                            classTreeRepository.save(classTreeOld);
                        }

                        classTreeId = classTreeOld.getId();
                    } else {
                        ClassTree classTreeNew = classTreeRepository.save(classTree);
                        classTreeId = classTreeNew.getId();
                    }
                    for (ErpEntity erpPord : prods.getErpEntityList()) {
                        Prod prod = new Prod();
                        ClassTree classTree2 = new ClassTree();
                        if (erpPord.getSonnum() == 0) {
                            classTree2.setId(classTreeId);
                            prod.setClassInfo(classTree2);
                            prod.setName("".equals(erpPord.getFullName()) ? "-" : erpPord.getFullName());
                            prod.setProdNum("".equals(erpPord.getUserCode()) ? "-" : erpPord.getUserCode());
                            prod.setSpecification("".equals(erpPord.getStandard()) ? "-" : erpPord.getStandard());
                            prod.setModel("".equals(erpPord.getType()) ? "-" : erpPord.getType());
                            prod.setBaseUnit("".equals(erpPord.getUnit1()) ? "-" : erpPord.getUnit1());
                            prod.setUnit1(erpPord.getUnit1());
                            prod.setUnit2(erpPord.getUnit1());
                            prod.setEnabled(true);
                            prod.setRemark1(erpPord.getComment1());
                            prod.setRemark2(erpPord.getComment2());
                            prod.setRemark3(erpPord.getComment3());
                            prod.setRemark4(erpPord.getComment4());
                            prod.setRemark5(erpPord.getComment5());
                            prod.setRemark6(erpPord.getComment6());
                            //判断表中是否已经存在相同编码的产品  如果存在则判断内容是否改变 改变则更新
                            Prod prodOld = selectProd.stream().filter(x -> x.getProdNum().equals(prod.getProdNum())).findFirst().orElse(null);
                            if (prodOld != null) {
                                if (flag) {
                                    ClassCompareUtil.getNewVo(prodOld, prod, true);
                                    prodRepository.save(prodOld);
                                }
                            } else {
                                prodRepository.save(prod);
                            }
                        }
                    }
                    circulation(prods.getErpEntityList(), classTreeId,flag);
                } else {
                    // 下面没有子节点且为第一级的直接插入分类和产品
                    if (prods.getLeveal() == 1) {
                        classTree.setPid(null);
                        //存在则返回
                        ClassTree classTreeOld = selectClassTree.stream().filter(x -> x.getClassNum().equals(classTree.getClassNum()) && x.getType().equals(classTree.getType())).findFirst().orElse(null);
                        if (classTreeOld != null) {
                            //如果存在判断此编码下的产品是否修改过其他内容
                            if(flag){
                                ClassCompareUtil.getNewVo(classTreeOld, classTree, true);
                                classTreeRepository.save(classTreeOld);
                            }
                            classTreeId = classTreeOld.getId();
                        } else {
                            ClassTree classTreeNew = classTreeRepository.save(classTree);
                            classTreeId = classTreeNew.getId();
                        }
                        ClassTree classTree2 = new ClassTree();
                        Prod prod = new Prod();
                        classTree2.setId(classTreeId);
                        prod.setClassInfo(classTree2);
                        prod.setName("".equals(prods.getFullName()) ? "-" : prods.getFullName());
                        prod.setProdNum("".equals(prods.getUserCode()) ? "-" : prods.getUserCode());
                        prod.setSpecification("".equals(prods.getStandard()) ? "-" : prods.getStandard());
                        prod.setModel("".equals(prods.getType()) ? "-" : prods.getType());
                        prod.setBaseUnit("".equals(prods.getUnit1()) ? "-" : prods.getUnit1());
                        prod.setUnit1(prods.getUnit1());
                        prod.setUnit2(prods.getUnit1());
                        prod.setEnabled(true);
                        prod.setRemark1(prods.getComment1());
                        prod.setRemark2(prods.getComment2());
                        prod.setRemark3(prods.getComment3());
                        prod.setRemark4(prods.getComment4());
                        prod.setRemark5(prods.getComment5());
                        prod.setRemark6(prods.getComment6());
                        //保存时判断保存是否存在相同的产品编码
                        //判断是否已经存在
                        Prod prodOld = selectProd.stream().filter(x -> x.getProdNum().equals(prod.getProdNum())).findFirst().orElse(null);
                        if (prodOld != null) {
                            if (flag) {
                                ClassCompareUtil.getNewVo(prodOld, prod, true);
                                prodRepository.save(prodOld);
                            }
                        } else {
                            prodRepository.save(prod);
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
    public Map<String, Object> queryAll(ProdQueryCriteria criteria, Pageable pageable) {
        Page<Prod> page = prodRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(prodMapper::toDto));
    }

    @Override
    public List<ProdDto> queryAll(ProdQueryCriteria criteria) {
        return prodMapper.toDto(prodRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public ProdDto findById(Long id) {
        Prod prod = prodRepository.findById(id).orElseGet(Prod::new);
        ValidationUtil.isNull(prod.getId(), "Prod", "id", id);
        return prodMapper.toDto(prod);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProdDto create(Prod resources) {
        if (resources.getClassInfo().getId() == null) {
            resources.setClassInfo(null);
        }
        if (prodRepository.existsByProdNumAndIdNot(resources.getProdNum(), -1L)) {
            throw new BadRequestException(String.format("%s 产品编号已存在", resources.getProdNum()));
        }
        return prodMapper.toDto(prodRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Prod resources) {
        if (prodRepository.existsByProdNumAndIdNot(resources.getProdNum(), resources.getId())) {
            throw new BadRequestException(String.format("%s 产品编号已存在", resources.getProdNum()));
        }
        Prod prod = prodRepository.findById(resources.getId()).orElseGet(Prod::new);
        ValidationUtil.isNull(prod.getId(), "Prod", "id", resources.getId());
        prod.copy(resources);
        if (prod.getClassInfo().getId() == null) {
            prod.setClassInfo(null);
        }
        prodRepository.save(prod);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            prodRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ProdDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ProdDto prod : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("产品编号", prod.getProdNum());
            map.put("产品名称", prod.getName());
            map.put("分类", prod.getClassInfo().getName());
            map.put("分类编号", prod.getClassInfo().getClassNum());
            map.put("规格", prod.getSpecification());
            map.put("型号", prod.getModel());
            map.put("基本单位", prod.getBaseUnit());
            map.put("辅助单位1", prod.getUnit1());
            map.put("辅助单位2", prod.getUnit2());
            map.put("状态", prod.getEnabled() ? "启用" : "禁用");
            map.put("备注1", prod.getRemark1());
            map.put("备注2", prod.getRemark2());
            map.put("备注3", prod.getRemark3());
            map.put("备注4", prod.getRemark4());
            map.put("备注5", prod.getRemark5());
            map.put("备注6", prod.getRemark6());
            map.put("创建日期", prod.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void importExcel(InputStream excelStream, int importModel) {
        ExcelReader reader = ExcelUtil.getReader(excelStream, 0);
        List<Map<String, Object>> readAll = reader.readAll();
        readAll.forEach(item -> {
            String prodNum = CzbHelper.defaultString(item.get("产品编号"));
            Prod old = prodRepository.findByProdNum(prodNum);
            if (old != null && importModel == 2) {
                //分类编号存在并且是非覆盖导入，则下一条
                return;
            }

            Prod prod = new Prod();
            prod.setProdNum(prodNum);
            prod.setName(CzbHelper.defaultString(item.get("产品名称")));
            String classNum = CzbHelper.defaultString(item.get("分类编号"));
            if (StringUtils.isNotBlank(classNum)) {
                ClassTree classTree = classTreeRepository.findByClassNumAndType(classNum, ClassTreeType.TYPE_PROD);
                if (classTree != null) {
                    prod.setClassInfo(classTree);
                }
            }
            prod.setSpecification(CzbHelper.defaultString(item.get("规格")));
            prod.setModel(CzbHelper.defaultString(item.get("型号")));
            prod.setBaseUnit(CzbHelper.defaultString(item.get("基本单位")));
            prod.setUnit1(CzbHelper.defaultString(item.get("辅助单位1")));
            prod.setUnit2(CzbHelper.defaultString(item.get("辅助单位2")));
            prod.setRemark1(CzbHelper.defaultString(item.get("备注1")));
            prod.setRemark2(CzbHelper.defaultString(item.get("备注2")));
            prod.setRemark3(CzbHelper.defaultString(item.get("备注3")));
            prod.setRemark4(CzbHelper.defaultString(item.get("备注4")));
            prod.setRemark5(CzbHelper.defaultString(item.get("备注5")));
            prod.setRemark6(CzbHelper.defaultString(item.get("备注6")));

            try {
                if (old == null) {
                    //新增
                    prod.setEnabled(true);
                    create(prod);
                } else {
                    //编辑
                    prod.setId(old.getId());
                    update(prod);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }

        });
    }

    @Override
    public Prod findByProdNum(String prodNum) {
        if (StringUtils.isBlank(prodNum)) {
            return null;
        }
        return prodRepository.findByProdNum(prodNum);
    }

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5=new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:"+e.getMessage(),e);
        }
    }

    /**
     * 生成32位 MD5
     * @param md5
     * @return
     */
    public static String fillMD5(String md5){
        return md5.length()==32?md5:fillMD5("0"+md5);
    }

}