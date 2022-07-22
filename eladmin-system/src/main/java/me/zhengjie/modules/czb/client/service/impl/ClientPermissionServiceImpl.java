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
package me.zhengjie.modules.czb.client.service.impl;

import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ArrayUtil;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.client.domain.ClientPermission;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.client.repository.ClientPermissionRepository;
import me.zhengjie.modules.czb.client.service.ClientPermissionService;
import me.zhengjie.modules.czb.client.service.dto.ClientPermissionDto;
import me.zhengjie.modules.czb.client.service.dto.ClientPermissionQueryCriteria;
import me.zhengjie.modules.czb.client.service.mapstruct.ClientPermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author unknown
* @date 2022-02-18
**/
@Service
@RequiredArgsConstructor
public class ClientPermissionServiceImpl implements ClientPermissionService {

    private final ClientPermissionRepository clientPermissionRepository;
    private final ClientPermissionMapper clientPermissionMapper;

    @Override
    public Map<String,Object> queryAll(ClientPermissionQueryCriteria criteria, Pageable pageable){
        Page<ClientPermission> page = clientPermissionRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(clientPermissionMapper::toDto));
    }

    @Override
    public List<ClientPermissionDto> queryAll(ClientPermissionQueryCriteria criteria){
        return clientPermissionMapper.toDto(clientPermissionRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ClientPermissionDto findById(Long id) {
        ClientPermission clientPermission = clientPermissionRepository.findById(id).orElseGet(ClientPermission::new);
        ValidationUtil.isNull(clientPermission.getId(),"ClientPermission","id",id);
        return clientPermissionMapper.toDto(clientPermission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClientPermissionDto create(ClientPermission resources) {
        if (clientPermissionRepository.existsByTagAndIdNot(resources.getTag(), -1L)) {
            throw new BadRequestException(String.format("%s 权限标识已存在", resources.getTag()));
        }
        return clientPermissionMapper.toDto(clientPermissionRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClientPermission resources) {
        if (clientPermissionRepository.existsByTagAndIdNot(resources.getTag(), resources.getId())) {
            throw new BadRequestException(String.format("%s 权限标识已存在", resources.getTag()));
        }
        ClientPermission clientPermission = clientPermissionRepository.findById(resources.getId()).orElseGet(ClientPermission::new);
        ValidationUtil.isNull( clientPermission.getId(),"ClientPermission","id",resources.getId());
        clientPermission.copy(resources);
        clientPermissionRepository.save(clientPermission);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            ClientPermission clientPermission = clientPermissionRepository.findById(id).orElse(null);
            if (clientPermission == null){
                continue;
            }
            if (clientPermission.getRoles().size() != 0){
                List<String> roleNames = new ArrayList<>();
                clientPermission.getRoles().forEach(role -> roleNames.add(role.getName()));
                throw new RuntimeException(String.format("%s还有%s权限，请取消配置后再删除", StringUtils.join(roleNames,"、"),clientPermission.getName()));
            }
            clientPermissionRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ClientPermissionDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ClientPermissionDto clientPermission : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("权限名称", clientPermission.getName());
            map.put("权限标识(唯一)", clientPermission.getTag());
            map.put("创建者", clientPermission.getCreateBy());
            map.put("更新者", clientPermission.getUpdateBy());
            map.put("创建日期", clientPermission.getCreateTime());
            map.put("更新时间", clientPermission.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}