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
package me.zhengjie.modules.czb.billManage.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import me.zhengjie.constants.SysParamKey;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.archive.domain.*;
import me.zhengjie.modules.czb.archive.service.CustService;
import me.zhengjie.modules.czb.archive.service.ProdService;
import me.zhengjie.modules.czb.archive.service.SupplierService;
import me.zhengjie.modules.czb.archive.service.WarehouseService;
import me.zhengjie.modules.czb.archive.service.dto.CustMiniDto;
import me.zhengjie.modules.czb.archive.service.dto.ProdMiniDto;
import me.zhengjie.modules.czb.archive.service.dto.SupplierMiniDto;
import me.zhengjie.modules.czb.archive.service.dto.WarehouseMiniDto;
import me.zhengjie.modules.czb.billManage.domain.Bill;
import me.zhengjie.modules.czb.billManage.domain.BillDetail;
import me.zhengjie.modules.czb.billManage.repository.BillDetailRepository;
import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.ParamsService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserMiniDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.billManage.repository.BillRepository;
import me.zhengjie.modules.czb.billManage.service.BillService;
import me.zhengjie.modules.czb.billManage.service.dto.BillDto;
import me.zhengjie.modules.czb.billManage.service.dto.BillQueryCriteria;
import me.zhengjie.modules.czb.billManage.service.mapstruct.BillMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-10-29
 **/
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillMapper billMapper;
    private final BillDetailRepository billDetailRepository;
    private final ParamsService paramsService;
    private final CustService custService;
    private final SupplierService supplierService;
    private final WarehouseService warehouseService;
    private final UserService userService;
    private final ProdService prodService;

    @Override
    public Map<String, Object> queryAll(BillQueryCriteria criteria, Pageable pageable) {
        Page<Bill> page = billRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(billMapper::toDto));
    }

    @Override
    public List<BillDto> queryAll(BillQueryCriteria criteria) {
        return billMapper.toDto(billRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public BillDto findById(Long id) {
        Bill bill = billRepository.findById(id).orElseGet(Bill::new);
        ValidationUtil.isNull(bill.getId(), "Bill", "id", id);
        return billMapper.toDto(bill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillDto create(Bill resources) {
        if (billRepository.existsByBillNumAndBillTypeAndIdNot(resources.getBillNum(), resources.getBillType(), -1L)) {
            throw new BadRequestException(String.format("%s 单据编号已存在", resources.getBillNum()));
        }

        //保存单据详情
        List<BillDetail> tmp = resources.getDetail();
        resources.setDetail(null);
        resources.setCust(null);
        resources.setSupplier(null);
        resources.setWarehouse(null);
        billRepository.save(resources);
        if (tmp.size() != 0) {
            for (BillDetail item : tmp) {
                item.setBillId(resources.getId());
                item.setProd(null);
            }
            billDetailRepository.saveAll(tmp);
        }

        return billMapper.toDto(billRepository.findById(resources.getId()).orElseGet(Bill::new));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Bill resources) {
        if (billRepository.existsByBillNumAndBillTypeAndIdNot(resources.getBillNum(), resources.getBillType(), resources.getId())) {
            throw new BadRequestException(String.format("%s 单据编号已存在", resources.getBillNum()));
        }
        Bill bill = billRepository.findById(resources.getId()).orElseGet(Bill::new);
        ValidationUtil.isNull(bill.getId(), "Bill", "id", resources.getId());
        bill.copy(resources);

        List<BillDetail> tmp = bill.getDetail();
        bill.setDetail(null);
        bill.setCust(null);
        bill.setSupplier(null);
        bill.setWarehouse(null);
        billRepository.save(bill);
        billDetailRepository.deleteByBillId(bill.getId());
        if (tmp.size() != 0) {
            for (BillDetail item : tmp) {
                item.setBillId(bill.getId());
                item.setProd(null);
            }
            billDetailRepository.saveAll(tmp);
        }

    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            billRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BillDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BillDto bill : all) {
            bill.getDetail().forEach(billDetailDto -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("单据日期", bill.getBillDate());
                map.put("单据编号", bill.getBillNum());
                if (bill.getWarehouse() == null) {
                    bill.setWarehouse(new WarehouseMiniDto());
                }
                map.put("仓库", bill.getWarehouse().getName());
                map.put("仓库编号", bill.getWarehouse().getWarehouseNum());
                if (bill.getBillType() == 1) {
                    //采购入库
                    if (bill.getSupplier() == null) {
                        bill.setSupplier(new SupplierMiniDto());
                    }
                    map.put("供应商", bill.getSupplier().getName());
                    map.put("供应商编号", bill.getSupplier().getSupplierNum());
                } else {
                    //销售出库
                    if (bill.getCust() == null) {
                        bill.setCust(new CustMiniDto());
                    }
                    map.put("客户", bill.getCust().getName());
                    map.put("客户编号", bill.getCust().getCustNum());
                }
                if (bill.getOperator() == null) {
                    bill.setOperator(new UserMiniDto());
                }
                map.put("操作员", bill.getOperator().getNickName());
                map.put("操作员编号", bill.getOperator().getUserNum());
                if (billDetailDto.getProd() == null) {
                    billDetailDto.setProd(new ProdMiniDto());
                }
                map.put("产品", billDetailDto.getProd().getName());
                map.put("产品编号", billDetailDto.getProd().getProdNum());
                map.put("磅台名称", billDetailDto.getPoundName());
                map.put("磅台编号", billDetailDto.getPoundBillNum());
                map.put("重量", billDetailDto.getWeight());
                map.put("单位", billDetailDto.getUnit());
                map.put("创建日期", bill.getCreateTime());
                list.add(map);
            });
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> collect(Integer billType, Integer collectType, Pageable pageable) {
        //汇总类型 1：按产品 2：按往来单位 3：按仓库 4：按日期
        switch (collectType) {
            case 1:
                //按产品
                return PageUtil.toPage(billRepository.findCollectByProd(billType, pageable));
            case 2:
                //按往来单位
                if (billType == 1) {
                    //入库
                    return PageUtil.toPage(billRepository.findCollectBySupplier(billType, pageable));
                } else {
                    //出库
                    return PageUtil.toPage(billRepository.findCollectByCust(billType, pageable));
                }
            case 3:
                //按仓库
                return PageUtil.toPage(billRepository.findCollectByWarehouse(billType, pageable));
            case 4:
                //按日期
                return PageUtil.toPage(billRepository.findCollectByBillDate(billType, pageable));
            default:
                throw new BadRequestException("不支持该类型汇总");
        }

    }

    @Override
    public String getSerialNumber(int billType) {
        StringBuilder prefix = new StringBuilder();
        String time = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SysParamKey key;
        if (billType == 1) {
            //入库
            prefix.append("RK-").append(time);
            key = SysParamKey.RK_Index;
        } else {
            //出库
            prefix.append("CK-").append(time);
            key = SysParamKey.CK_Index;
        }
        String indexStr = paramsService.findByKey(key).getValue();
        if (StringUtils.isBlank(indexStr)) {
            indexStr = "0";
        }
        int index = Integer.parseInt(indexStr);
        return autoIncrement(billType, key, prefix.toString(), index);
    }

    private String autoIncrement(int billType, SysParamKey key, String prefix, int start) {
        start++;
        String billNum = prefix + String.format("-%05d", start);
        if (billRepository.existsByBillNumAndBillTypeAndIdNot(billNum, billType, -1L)) {
            //编号已存在
            return autoIncrement(billType, key, prefix, start);
        } else {
            //编号不存在，缓存计数，返回编号
            paramsService.save(key, start + "");
            return billNum;
        }
    }

    @Override
    public void importExcel(InputStream excelStream, Integer classType) {
        ExcelReader reader = ExcelUtil.getReader(excelStream, 0);
        List<Map<String, Object>> readAll = reader.readAll();
        Long billId = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> item : readAll) {
            String billDate = CzbHelper.defaultString(item.get("单据日期"));
            if (billId == null && StringUtils.isBlank(billDate)) {
                continue;
            }
            if (StringUtils.isNotBlank(billDate)) {
                //单据日期不为空，则新建单据
                Bill bill = new Bill();
                try {
                    bill.setBillDate(new Date(sdf.parse(billDate).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
                bill.setBillType(classType);
                if (classType == 1) {
                    //采购入库
                    Supplier supplier = supplierService.findBySupplierNum(CzbHelper.defaultString(item.get("供应商编号")));
                    if (supplier != null) {
                        bill.setSupplierId(supplier.getId());
                    }
                } else {
                    //销售出库
                    Cust cust = custService.findByCustNum(CzbHelper.defaultString(item.get("客户编号")));
                    if (cust != null) {
                        bill.setCustId(cust.getId());
                    }
                }
                Warehouse warehouse = warehouseService.findByWarehouseNum(CzbHelper.defaultString(item.get("仓库编号")));
                if (warehouse != null) {
                    bill.setWarehouseId(warehouse.getId());
                }

                String billNum = getSerialNumber(classType);
                while (billRepository.existsByBillNumAndBillTypeAndIdNot(billNum, classType, -1L)) {
                    billNum = getSerialNumber(classType);
                }
                bill.setBillNum(billNum);
                bill = billRepository.save(bill);
                billId = bill.getId();
            }
            //保存单据详情
            BillDetail billDetail = new BillDetail();
            billDetail.setBillId(billId);
            Prod prod = prodService.findByProdNum(CzbHelper.defaultString(item.get("产品编号")));
            if (prod != null) {
                billDetail.setProdId(prod.getId());
            }
            billDetail.setPoundName(CzbHelper.defaultString(item.get("磅台名称")));
            billDetail.setPoundBillNum(CzbHelper.defaultString(item.get("磅单编号")));
            billDetail.setWeight(Float.parseFloat(CzbHelper.defaultString(item.get("重量"))));
            billDetail.setUnit(CzbHelper.defaultString(item.get("单位")));
            billDetailRepository.save(billDetail);
        }
    }

    @Override
    public List<Map<String, Object>> print(long[] idArray) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (long id : idArray) {
            Bill billDto = billRepository.findById(id).orElse(new Bill());
            Map<String, Object> item = new HashMap<>();
            List<Map<String, Object>> detailList = new ArrayList<>();
            Float totalWeight = 0F;
            String header, transactionCompany;
            //单据类型 1：采购入库 2：销售出库
            if (billDto.getBillType() == 1) {
                //采购入库
                header = "收货通知单";
                transactionCompany = billDto.getSupplier().getName();
            } else {
                //销售出库
                header = "发货通知单";
                transactionCompany = billDto.getCust().getName();
            }
            item.put("header", header);
            item.put("billDate", billDto.getBillDate());
            item.put("billNum", billDto.getBillNum());
            item.put("transactionCompany", transactionCompany);
            item.put("warehouseName", billDto.getWarehouse().getName());
            //处理单据详情
            for (int i = 0; i < billDto.getDetail().size(); i++) {
                final int serialNumber = i + 1;
                BillDetail billDetail = billDto.getDetail().get(i);
                detailList.add(new HashMap<String, Object>() {{
                    put("serialNumber", serialNumber);
                    put("prodNum", billDetail.getProd().getProdNum());
                    put("prodName", billDetail.getProd().getName());
                    put("poundName", billDetail.getPoundName());
                    put("poundBillNum", billDetail.getPoundBillNum());
                    put("unit", billDetail.getUnit());
                    put("weight", billDetail.getWeight());
                }});
                totalWeight += billDetail.getWeight();
            }
            item.put("detail", detailList);
            item.put("totalWeight", totalWeight);
            resultList.add(item);
        }
        return resultList;
    }

    @Override
    public void weigh2bill(Weigh origin) {
        //单据类型 1：采购入库 2：销售出库
        int billType = Integer.parseInt(origin.getBillType());
        Bill bill = new Bill();
        bill.setBillType(billType);
        bill.setBillDate(new Date(origin.getWeighDate().getTime()));
        bill.setBillNum(getSerialNumber(billType));
        bill.setWarehouseId(origin.getWarehouseId());
        if (billType == 1) {
            //采购入库
            bill.setSupplierId(origin.getSupplierId());
        } else {
            //销售出库
            bill.setCustId(origin.getCustId());
        }
        BillDetail detail = new BillDetail();
        detail.setWeighId(origin.getId());
        detail.setProdId(origin.getProdId());
        detail.setPoundName(origin.getPoundName());
        detail.setPoundBillNum(origin.getPoundBillNum());
        detail.setUnit(origin.getUnit());
        detail.setWeight(origin.getWeight().floatValue());
        detail.setPrice(origin.getPrice());
        detail.setAmount(origin.getAmount());
        bill.setDetail(new ArrayList<BillDetail>() {{
            add(detail);
        }});
        create(bill);

    }
}