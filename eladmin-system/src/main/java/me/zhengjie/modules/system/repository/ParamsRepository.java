package me.zhengjie.modules.system.repository;

import me.zhengjie.constants.SysParamKey;
import me.zhengjie.modules.system.domain.Params;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParamsRepository extends JpaRepository<Params, SysParamKey> , JpaSpecificationExecutor<Params> {

}
