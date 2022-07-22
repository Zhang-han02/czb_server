package me.zhengjie.modules.security.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.util.Json;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constants.SysParamKey;
import me.zhengjie.modules.security.service.dto.ExpirationInfo;
import me.zhengjie.modules.system.domain.Params;
import me.zhengjie.modules.system.service.ParamsService;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class AuthService {
    @Value("${expiration-time-url}")
    private String expirationTimeUrl;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ParamsService paramsService;
    private final RestTemplate restTemplate;

    public AuthService(ParamsService paramsService, RestTemplate restTemplate) {
        this.paramsService = paramsService;
        this.restTemplate = restTemplate;
    }

    public ExpirationInfo getExpirationInfo() {
        Params params = paramsService.findByKey(SysParamKey.RegisterCode);
        String registerCode = params.getValue();
        ExpirationInfo result = new ExpirationInfo();
        result.setRegisterCode(registerCode);
        if (StringUtils.isNotBlank(registerCode)) {
            String apiResultStr = restTemplate.getForObject(String.format(expirationTimeUrl, registerCode), String.class);
            JSONArray apiResult = JSON.parseArray(apiResultStr);
            if (apiResult != null && apiResult.size() != 0) {
                JSONObject obj = apiResult.getJSONObject(0);
                result.setExpirationTime(obj.getString("dueDate"));
                result.setResidueDays(obj.getLongValue("day"));
                result.setNeedWarn(result.getResidueDays() <= 15);
            }
        }
        return result;
    }

}
