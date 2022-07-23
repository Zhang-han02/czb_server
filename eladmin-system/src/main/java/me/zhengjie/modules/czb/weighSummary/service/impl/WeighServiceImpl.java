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
package me.zhengjie.modules.czb.weighSummary.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.archive.domain.Cust;
import me.zhengjie.modules.czb.archive.domain.Prod;
import me.zhengjie.modules.czb.archive.domain.Supplier;
import me.zhengjie.modules.czb.archive.domain.Warehouse;
import me.zhengjie.modules.czb.archive.repository.CustRepository;
import me.zhengjie.modules.czb.archive.repository.ProdRepository;
import me.zhengjie.modules.czb.archive.repository.SupplierRepository;
import me.zhengjie.modules.czb.archive.repository.WarehouseRepository;
import me.zhengjie.modules.czb.billManage.service.BillService;
import me.zhengjie.modules.czb.weighSummary.domain.Weigh;
import me.zhengjie.modules.czb.weighSummary.model.ProdTop;
import me.zhengjie.modules.czb.weighSummary.model.PurchaseOrder;
import me.zhengjie.modules.czb.weighSummary.model.PurchaseOrderDetail;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.repository.UserRepository;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.weighSummary.repository.WeighRepository;
import me.zhengjie.modules.czb.weighSummary.service.WeighService;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighDto;
import me.zhengjie.modules.czb.weighSummary.service.dto.WeighQueryCriteria;
import me.zhengjie.modules.czb.weighSummary.service.mapstruct.WeighMapper;
import org.apache.http.client.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zrs
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2022-01-07
 **/
@Service
@RequiredArgsConstructor
public class WeighServiceImpl implements WeighService {

    private final WeighRepository weighRepository;
    private final WeighMapper weighMapper;
    private final ProdRepository prodRepository;
    private final SupplierRepository supplierRepository;
    private final CustRepository custRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final BillService billService;

    @Override
    public Map<String, Object> queryAll(Integer collectType, WeighQueryCriteria criteria, Pageable pageable) {

        // 汇总类型 1：按产品汇总,2：按往来单位汇总,3：按司磅员汇总,4：按日期汇总,5：按仓库汇总,6：按磅台汇总
        switch (collectType) {
            case 1:
                //按产品
                return PageUtil.toPage(weighRepository.findWeightByProdId(criteria.getBillStatus(), pageable));
            case 2:
                //按往来单位
                if (criteria.getBillType() == 1) {
                    //采购入库-供应商
                    return PageUtil.toPage(weighRepository.findWeightBySupplier(criteria.getBillStatus(), pageable));
                } else {
                    //销售出库-客户
                    return PageUtil.toPage(weighRepository.findWeightByCust(criteria.getBillStatus(), pageable));
                }
            case 3:
                //按司磅员汇总
                return PageUtil.toPage(weighRepository.findWeightByUser(criteria.getBillStatus(), pageable));
            case 4:
                //按日期
                return PageUtil.toPage(weighRepository.findWeightByBillDate(criteria.getBillStatus(), pageable));
            case 5:
                //按仓库
                return PageUtil.toPage(weighRepository.findWeightByWarehouse(criteria.getBillStatus(), pageable));
            case 6:
                //按磅台汇总
                return PageUtil.toPage(weighRepository.findWeightByPoundName(criteria.getBillStatus(), pageable));
            default:
                throw new BadRequestException("不支持该类型汇总");
        }
//        Page<Weigh> page = weighRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
//        return PageUtil.toPage(page.map(weighMapper::toDto));
    }
    @Override
    public Map<String, Object> findPound(Pageable pageable){
        return PageUtil.toPage(weighRepository.findPound(pageable));
    }

    @Override
    public Map<String, Object> findProd(Integer billType) {
        Map<String, Object> content = new LinkedHashMap<>(3);
        List<Map<String,Object>> prodList = weighRepository.findProd(billType);
        String prodJson = JSON.toJSONString(prodList);
        List<ProdTop> resultList = JSON.parseArray(prodJson, ProdTop.class);
        content.put("content",resultList);
        return content;
    }

    @Override
    public Map<String, Object> outPut(Integer collectType, WeighQueryCriteria criteria, Pageable pageable) {

        switch (collectType){
            case 1:
                return PageUtil.toPage(weighRepository.findDAY(criteria.getBillType(),pageable));
            case 2:
                return PageUtil.toPage(weighRepository.findMoth(criteria.getBillType(),pageable));
            case 3:
                return PageUtil.toPage(weighRepository.findYear(criteria.getBillType(),pageable));
            default:
                String msg = criteria.getBillType()==1 ?"称重入库" :"称重出库";
                throw new BadRequestException("查询"+msg+"失败");
        }
    }

    @Override
    public Map<String, Object> prodTopThree(Pageable pageable) {
        Map<String, Object> content = new LinkedHashMap<>(3);
        List nameList = new ArrayList<>();
        List monthList = new ArrayList<>();
        List weighList = new ArrayList<>();
        List<Map<String,Object>> prodTop = weighRepository.prodTop();
        //根据年度最高的产品循环查询每个产品近12月的数量
        try {
            for (Map prod : prodTop) {
                List months = new ArrayList<>();
                List weighs = new ArrayList<>();
                nameList.add(prod.get("name"));
                List<Map<String,Object>> prodList = weighRepository.prodTopThree( prod.get("id").toString());
                //将每个产品每月的数量放在list
                for (Map mapCon : prodList) {
                    months.add(mapCon.get("month"));
                    weighs.add(mapCon.get("weigh"));
                }
                monthList.add(months);
                weighList.add(weighs);
                content.put("name",nameList);
                content.put("month",monthList);
                content.put("weigh",weighList);
            }
            return content;
        } catch (Exception e){
            throw new RuntimeException("查询年度走势图TOP3失败");
        }
    }

    @Override
    public List<WeighDto> queryAll(WeighQueryCriteria criteria) {
        return weighMapper.toDto(weighRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public WeighDto findById(Long id) {
        Weigh weigh = weighRepository.findById(id).orElseGet(Weigh::new);
        ValidationUtil.isNull(weigh.getId(), "Weigh", "id", id);
        return weighMapper.toDto(weigh);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeighDto create(Weigh resources) {
        if (resources.getBillType().equals("1")) {
            //采购入库
            if (resources.getSupplierId() == null) {
                throw new BadRequestException("请上传供应商id");
            }
        } else {
            //销售出库
            if (resources.getCustId() == null) {
                throw new BadRequestException("请上传客户id");
            }
        }
        User user = userRepository.findById(resources.getOperatorId()).orElseThrow(() -> new BadRequestException("司磅员不存在"));
        resources.setBillStatus("1");//未生单
        resources.setWeighDate(new java.sql.Date(new Date().getTime()));
        return weighMapper.toDto(weighRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Weigh resources) {
        Weigh weigh = weighRepository.findById(resources.getId()).orElseGet(Weigh::new);
        ValidationUtil.isNull(weigh.getId(), "Weigh", "id", resources.getId());
        weigh.copy(resources);
        weighRepository.save(weigh);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            weighRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WeighDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WeighDto weigh : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("单据类型 1：采购入库 2：销售出库", weigh.getBillType());
            map.put("称重日期", weigh.getWeighDate());
            map.put("单据状态 1：未生单 2：已生单", weigh.getBillStatus());
            map.put("磅台名称", weigh.getPoundName());
            map.put("磅单编号", weigh.getPoundBillNum());
            map.put("产品id", weigh.getProdId());
            map.put("仓库id", weigh.getWarehouseId());
            map.put("供应商id（入库）", weigh.getSupplierId());
            map.put("客户id（出库）", weigh.getCustId());
            map.put("司磅员id", weigh.getOperatorId());
            map.put("重量", weigh.getWeight());
            map.put("单位", weigh.getUnit());
            map.put("创建日期", weigh.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> queryAlls(WeighQueryCriteria criteria, Pageable pageable) {
        Page<Weigh> page = weighRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(weighMapper::toDto));
    }

    @Override
    public Map<String, Object> queryDetailAll(WeighQueryCriteria criteria, Pageable pageable) {
        Page<Weigh> page = weighRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(weighMapper::toDto));
    }

    @Override
    public Map<String, Object> generatePrintData(Integer collectType, WeighQueryCriteria criteria) {
        UserDto curUser = JSONUtil.toBean(SecurityUtils.getCurrentUserInfo().toString(), UserDto.class);
        List<Weigh> page = weighRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));

        Map<String, Object> result = new HashMap<>();
        result.put("createUserName", curUser.getNickName());
        result.put("printTime", DateUtil.localDateTimeFormatyMdHms(LocalDateTime.now()));
        String condition = "";
        switch (collectType) {
            case 1:
                //按产品
                condition = String.format("产品：%s", prodRepository.findById(criteria.getProdId()).orElseGet(Prod::new).getName());
                break;
            case 2:
                //按往来单位
                if (criteria.getBillType() == 1) {
                    //采购入库
                    condition = String.format("往来单位：%s", supplierRepository.findById(criteria.getSupplierId()).orElseGet(Supplier::new).getName());
                } else {
                    //销售出库
                    condition = String.format("往来单位：%s", custRepository.findById(criteria.getCustId()).orElseGet(Cust::new).getName());
                }
                break;
            case 3:
                //按司磅员汇总
                condition = String.format("司磅员：%s", userRepository.findById(criteria.getOperatorId()).orElseGet(User::new).getNickName());
                break;
            case 4:
                //按日期
                condition = String.format("日期：%s", criteria.getWeighDate());
                break;
            case 5:
                //按仓库
                condition = String.format("仓库：%s", warehouseRepository.findById(criteria.getWarehouseId()).orElseGet(Warehouse::new).getName());
                break;
            case 6:
                //按磅台汇总
                condition = String.format("磅台：%s", criteria.getPoundName());
                break;
        }
        result.put("condition", condition);
        List<Map<String, Object>> detailList = new ArrayList<>();
        //处理单据详情
        for (int i = 0; i < page.size(); i++) {
            final int serialNumber = i + 1;
            Weigh weigh = page.get(i);
            detailList.add(new HashMap<String, Object>() {{
                put("serialNumber", serialNumber);
                put("prodNum", weigh.getProdInfo().getProdNum());
                put("prodName", weigh.getProdInfo().getName());
                put("poundName", weigh.getPoundName());
                put("poundBillNum", weigh.getPoundBillNum());
                put("unit", weigh.getUnit());
                put("weight", weigh.getWeight());
            }});
        }
        result.put("detail", detailList);
        return result;
    }

    @Override
    public void createBill(String ids) {
        String userId = "10573465";
        String dbName = "测试账套";
        String AppKey = "C676DA6AEBF44FDFA5EA8E0C9A9A6B33";
        String SercretKey = "D13020F53A414FA6967BFC41C46B3FAC";
        String url = "http://api.cmgrasp.com/CMGraspApi/GateWay";

        List<Long> idList = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            for (String id : ids.split(",")) {
                idList.add(Long.valueOf(id));
            }
        } else {
            throw new BadRequestException("请选择需要生单的记录");
        }
        List<Weigh> weighs = weighRepository.findAllById(idList);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Random rand = new Random();
        Integer randNum = rand.nextInt(10);

        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("AppKey", AppKey);
        sendMap.put("UserId", userId);
        sendMap.put("InvalidTime", df2.format(new Date()));
        sendMap.put("RandamStr", randNum.toString());

        String sign = HttpUtil.arraySign(sendMap) + SercretKey;
        String SignStr = HttpUtil.stringToMD5(sign); // 签名字符串
        sendMap.put("SignStr", SignStr);

        sendMap.put("MethodName", "graspcm.cmapi.getcustomerapiurl");

        String data = JSON.toJSONString(sendMap);
        String send_res = HttpUtil.requestOnce(url, data);
        JSONObject obj = JSONObject.parseObject(send_res);
        if (obj.getInteger("RetCode") == 0) {
            String url3 = obj.getJSONObject("RetMsg").getString("ApiServerAddress");
            String ApiParam = obj.getJSONObject("RetMsg").getString("ApiParam");

            Map<String, String> sendMap2 = Maps.newHashMap();
            sendMap2.put("AppKey", AppKey);
            sendMap2.put("InvalidTime", df2.format(new Date()));
            sendMap2.put("RandamStr", obj.getJSONObject("RetMsg").getString("RandamStr"));

            //生成签名字符串
            String sign2 = HttpUtil.arraySign(sendMap2) + SercretKey;
            String sign2Md5 = HttpUtil.stringToMD5(sign2); // 签名字符串
            sendMap2.put("SignStr", sign2Md5);

            sendMap2.put("MethodName", "graspcm.cmapi.getsignstr");

            String data2 = JSON.toJSONString(sendMap2);
            String send_res2 = HttpUtil.requestOnce(url, data2);
            JSONObject obj2 = JSONObject.parseObject(send_res2);
            if (obj2.getInteger("RetCode") == 0) {
                String signKey = obj2.getJSONObject("RetMsg").getString("SignKey");
                Map<String, String> sendMap3 = Maps.newHashMap();
                sendMap3.put("managename", "GraspCMServerApi");
                sendMap3.put("dbname", dbName); // 数据库名称
                sendMap3.put("processtype", "0");  // 过账类型 目前只支持 0保存 6存为草稿
                sendMap3.put("vchcode", "0");  // 单据vchcode

                sendMap3.put("apiparam", ApiParam);  // 通过获取程序API地址接口返回
                sendMap3.put("apitype", "process"); // 接口类型 传process
                for (Weigh info : weighs) {
                    if (info.getBillStatus().equals("1")) {
                        //未生单则进行生单
                        createBillAction(url3, signKey, sendMap3, info);
                    }
                }
            }
        }
    }

    private void createBillAction(String url3, String signKey, Map<String, String> sendMap3, Weigh info) {
        // 单据类型 1：采购入库 2：销售出库
        String billType = info.getBillType();
        // 单价(元)
        BigDecimal price = new BigDecimal(info.getPrice() != null ? info.getPrice().toString() : "0");
        // 总金额(元)
        BigDecimal amount = new BigDecimal(info.getAmount() != null ? info.getAmount().toString() : "0");

        String nickName = SecurityUtils.getCurrentUserInfo().getStr("nickName");
        //单据类型 采购单:34  ，销售单:11
        String summary = "", busercode = "", bfullname = "", settlebusercode = "", settlebfullname = "", vchtype = "";
        PurchaseOrder order = new PurchaseOrder();
        if (billType.equals("1")) {
            //采购入库
            summary = "摘要-采购入库";
            busercode = info.getSupplierInfo().getSupplierNum();
            bfullname = info.getSupplierInfo().getName();

            //结算单位
            if (info.getSupplierInfo().getCheckOutUnit() == null) {
                settlebusercode = info.getSupplierInfo().getSupplierNum();
                settlebfullname = info.getSupplierInfo().getName();
            } else {
                settlebusercode = info.getSupplierInfo().getCheckOutUnitInfo().getSupplierNum();
                settlebfullname = info.getSupplierInfo().getCheckOutUnitInfo().getName();
            }

            vchtype = "34";

        } else {
            //销售出库
            summary = "摘要-销售出库";

            busercode = info.getCustInfo().getCustNum();
            bfullname = info.getCustInfo().getName();

            //结算单位
            if (info.getCustInfo().getCheckOutUnit() == null) {
                settlebusercode = info.getCustInfo().getCustNum();
                settlebfullname = info.getCustInfo().getName();
            } else {
                settlebusercode = info.getCustInfo().getCheckOutUnit().getCustNum();
                settlebfullname = info.getCustInfo().getCheckOutUnit().getName();
            }

//            order.setBilltotal(amount.floatValue());
            order.setDifatypename("销售抹零");

            vchtype = "11";
        }

        order.setDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));// 2019-09-19
        order.setSummary(summary);//摘要
        order.setBusercode(busercode);//供应商编号
        order.setBfullname(bfullname);//供应商全名
        order.setSettlebusercode(settlebusercode);//结算单位编号
        order.setSettlebfullname(settlebfullname);//结算单位全名
        order.setKusercode(info.getWarehouseInfo().getWarehouseNum());//仓库编号
        order.setKfullname(info.getWarehouseInfo().getName());//仓库全名
        order.setInputno(nickName);//制单人全名
        order.setBilltype(0);//单据处理类型 本单据为0
        order.setLogOnDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));//登陆日期

        /**
         * 单据明细 很多参数没有
         */
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPusercode(info.getProdInfo().getProdNum());//存货编号
        detail.setPfullname(info.getProdInfo().getName());//存货全名
        detail.setKusercode(info.getWarehouseInfo().getWarehouseNum());//仓库编号
        detail.setKfullname(info.getWarehouseInfo().getName());//仓库全名
        detail.setBillunit(info.getUnit());//单位全名
        detail.setNunit(1);//单位编号 1为基本单位 2为辅助单位1 3为辅助单位2
        detail.setQty(info.getWeight().setScale(4, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位数量 4位小数
        detail.setPrice(price.setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位折前单价 6位小数
        detail.setTotal(amount.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位折前金额 2位小数
        detail.setDiscount(new BigDecimal(1).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位折扣 4位小数
        detail.setDiscountprice(price.setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位折后单价 6位小数
        detail.setDiscounttotal(amount.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位折后金额 2位小数
        detail.setTax(new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位税率 2位小数
        detail.setTaxprice(price.setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位含税单价 6位小数
        detail.setTaxtotal(new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位税额 2位小数
        detail.setTax_total(amount.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());//基本单位价税合计 2位小数
        detail.setPrice_unit(price.setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());//实际单位折前单价 6位小数
        detail.setDiscountprice_unit(price.setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());//实际单位折后单价 6位小数
        detail.setTaxprice_unit(price.setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());//实际单位含税单价 6位小数
        detail.setUsedtype(1);//表格类型 本单据为1
        detail.setPdetail(0);//库存类型 本单据为0
        order.setDetail(new ArrayList<PurchaseOrderDetail>() {{
            add(detail);
        }});//单据明细

        /**
         * 付款明细
         */
//        PayData payData = new PayData();
//        payData.setAfullname(null);
//        payData.setAusercode(null);
//        payData.setTotal(null);
//        order.setSettle(new ArrayList<PayData>(){{
//            add(payData);
//        }});//付款明细

        // 填充数据
        sendMap3.put("billdata", JSONObject.toJSONString(order));  // 单据数据json 详见文档：单据Json格式
        sendMap3.put("vchtype", vchtype);  // 单据类型 采购单:34  ，销售单:11

        //生成签名字符串
        String sign3 = HttpUtil.arraySign(sendMap3) + signKey;
        String sign3Md5 = HttpUtil.stringToMD5(sign3); // 签名字符串
        sendMap3.put("sign", sign3Md5);

        String data3 = HttpUtil.asUrlParams(sendMap3);
        System.out.println("______请求参数" + JSONUtil.toJsonStr(sendMap3));
        String send_res3 = HttpUtil.requestOnce(url3, data3);
        JSONObject obj3 = JSONObject.parseObject(send_res3);
        if (obj3.getInteger("code") > 0) {
            //更新状态
            info.setBillStatus("2");
            update(info);
            //生成本系统单据
            billService.weigh2bill(info);
        } else {
            StringBuilder msg = new StringBuilder(obj3.getString("message"));
            JSONArray array = obj3.getJSONArray("response");
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


}
