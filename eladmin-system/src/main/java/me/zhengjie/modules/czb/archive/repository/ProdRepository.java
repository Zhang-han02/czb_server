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
package me.zhengjie.modules.czb.archive.repository;

import me.zhengjie.modules.czb.archive.domain.Prod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* @website https://el-admin.vip
* @author unknown
* @date 2021-09-23
**/
public interface ProdRepository extends JpaRepository<Prod, Long>, JpaSpecificationExecutor<Prod> {

    boolean existsByProdNumAndIdNot(String prodNum,Long id);

    Prod findByProdNum(String prodNum);
}