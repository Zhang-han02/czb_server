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

import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.czb.weigh.domain.ServerConfig;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.czb.weigh.repository.ServerConfigRepository;
import me.zhengjie.modules.czb.weigh.service.ServerConfigService;
import me.zhengjie.modules.czb.weigh.service.dto.ServerConfigDto;
import me.zhengjie.modules.czb.weigh.service.dto.ServerConfigQueryCriteria;
import me.zhengjie.modules.czb.weigh.service.mapstruct.ServerConfigMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author unknown
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-10-21
 **/
@Service
@RequiredArgsConstructor
public class ServerConfigServiceImpl implements ServerConfigService {

    private final ServerConfigRepository serverConfigRepository;
    private final ServerConfigMapper serverConfigMapper;

    @Override
    public Map<String, Object> queryAll(ServerConfigQueryCriteria criteria, Pageable pageable) {
        Page<ServerConfig> page = serverConfigRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(serverConfigMapper::toDto));
    }

    @Override
    public List<ServerConfigDto> queryAll(ServerConfigQueryCriteria criteria) {
        return serverConfigMapper.toDto(serverConfigRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }


    @Override
    @Transactional
    public ServerConfigDto findById(Long id) {
        ServerConfig serverConfig = serverConfigRepository.findById(id).orElseGet(ServerConfig::new);
        ValidationUtil.isNull(serverConfig.getId(), "ServerConfig", "id", id);
        return serverConfigMapper.toDto(serverConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerConfigDto create(ServerConfig resources) {
        return serverConfigMapper.toDto(serverConfigRepository.save(resources));
    }

    @Override
    public void save(ServerConfig resources) {
        //数据库名称固定
        resources.setDbName("IndustryTradeSystem");
        if (resources.getId() != null) {
            update(resources);
        } else {
            create(resources);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ServerConfig resources) {
        ServerConfig serverConfig = serverConfigRepository.findById(resources.getId()).orElseGet(ServerConfig::new);
        ValidationUtil.isNull(serverConfig.getId(), "ServerConfig", "id", resources.getId());
        serverConfig.copy(resources);
        serverConfigRepository.save(serverConfig);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            serverConfigRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ServerConfigDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ServerConfigDto serverConfig : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("服务器端口", serverConfig.getDbPort());
            map.put("数据库地址", serverConfig.getDbIp());
            map.put("数据库密码", serverConfig.getDbPassword());
            map.put("称重模式", serverConfig.getWeighModel());
            map.put("称重频次", serverConfig.getWeighFrequency());
            map.put("是否保存后自动打印 0:否 1:是", serverConfig.getIsAutoPrint());
            map.put("是否稳定后自动保存并打印 0:否 1:是", serverConfig.getIsAutoSavePrint());
            map.put("是否必选订单 0:否 1:是", serverConfig.getIsMustOrder());
            map.put("创建者", serverConfig.getCreateBy());
            map.put("创建时间", serverConfig.getCreateTime());
            map.put("更新者", serverConfig.getUpdateBy());
            map.put("更新时间", serverConfig.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public ServerConfigDto queryLatest() {
        List<ServerConfig> list = serverConfigRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        if (list.size() == 0) {
            return new ServerConfigDto();
        } else {
            return serverConfigMapper.toDto(list.get(0));
        }
    }

    @Override
    public Connection getConnection(ServerConfig serverConfig) {
        if (StringUtils.isBlank(serverConfig.getDbType())) {
            throw new BadRequestException("请选择数据库类型");
        }
        if (StringUtils.isBlank(serverConfig.getDbIp())) {
            throw new BadRequestException("请填写数据库地址");
        }
        if (StringUtils.isBlank(serverConfig.getDbPort())) {
            throw new BadRequestException("请填写数据库端口");
        }
        if (StringUtils.isBlank(serverConfig.getDbName())) {
            throw new BadRequestException("请填写数据库名称");
        }
        if (StringUtils.isBlank(serverConfig.getDbUsername())) {
            throw new BadRequestException("请填写数据库用户名");
        }
        if (StringUtils.isBlank(serverConfig.getDbPassword())) {
            throw new BadRequestException("请填写数据库密码");
        }
        Connection connection;
        try {
            connection = JdbcUtil.obtain(serverConfig);
            if (!connection.isClosed()) {
                return connection;
            } else {
                throw new RuntimeException("数据库连接关闭");
            }
        } catch (Exception e) {
            throw new RuntimeException("数据库连接失败：" + e.getMessage());
        }
    }

    @Override
    public List<String> getAccountSet(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Account");
            ResultSet resultSet = statement.executeQuery();
            List<String> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getString("DbName"));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("查询账套失败：" + e.getMessage());
        }
    }
}