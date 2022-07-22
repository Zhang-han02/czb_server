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

import com.google.common.reflect.TypeToken;
import me.zhengjie.modules.czb.billManage.domain.BillDetail;
import me.zhengjie.modules.czb.billManage.service.dto.BillCollectDetailQueryCriteria;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.billManage.repository.BillDetailRepository;
import me.zhengjie.modules.czb.billManage.service.BillDetailService;
import me.zhengjie.modules.czb.billManage.service.dto.BillDetailDto;
import me.zhengjie.modules.czb.billManage.service.dto.BillDetailQueryCriteria;
import me.zhengjie.modules.czb.billManage.service.mapstruct.BillDetailMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.util.*;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-10-29
 **/
@Service
@RequiredArgsConstructor
public class BillDetailServiceImpl implements BillDetailService {

    private final BillDetailRepository billDetailRepository;
    private final BillDetailMapper billDetailMapper;

    @Override
    public Map<String, Object> queryAll(BillDetailQueryCriteria criteria, Pageable pageable) {
        Page<BillDetail> page = billDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(billDetailMapper::toDto));
    }

    @Override
    public List<BillDetailDto> queryAll(BillDetailQueryCriteria criteria) {
        return billDetailMapper.toDto(billDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public BillDetailDto findById(Long id) {
        BillDetail billDetail = billDetailRepository.findById(id).orElseGet(BillDetail::new);
        ValidationUtil.isNull(billDetail.getId(), "BillDetail", "id", id);
        return billDetailMapper.toDto(billDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillDetailDto create(BillDetail resources) {
        return billDetailMapper.toDto(billDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BillDetail resources) {
        BillDetail billDetail = billDetailRepository.findById(resources.getId()).orElseGet(BillDetail::new);
        ValidationUtil.isNull(billDetail.getId(), "BillDetail", "id", resources.getId());
        billDetail.copy(resources);
        billDetailRepository.save(billDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            billDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BillDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BillDetailDto billDetail : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("单据id", billDetail.getBillNum());
            map.put("产品id", billDetail.getProd().getId());
            map.put("磅台名称", billDetail.getPoundName());
            map.put("磅单编号", billDetail.getPoundBillNum());
            map.put("单位", billDetail.getUnit());
            map.put("辅助单位1", billDetail.getUnit1());
            map.put("辅助单位2", billDetail.getUnit2());
            map.put("重量", billDetail.getWeight());
            map.put("辅助数量1", billDetail.getAmount1());
            map.put("辅助数量2", billDetail.getAmount2());
            map.put("创建者", billDetail.getCreateBy());
            map.put("更新者", billDetail.getUpdateBy());
            map.put("创建日期", billDetail.getCreateTime());
            map.put("更新时间", billDetail.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> collectDetail(BillCollectDetailQueryCriteria queryCriteria, Pageable pageable) {
        Page<BillDetail> page = billDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            return QueryHelp.getPredicate(root, queryCriteria, criteriaBuilder);
        }, pageable);

        return PageUtil.toPage(page.map(billDetail -> billDetailMapper.toCollectDto(billDetail,billDetail.getBill())));
    }
}