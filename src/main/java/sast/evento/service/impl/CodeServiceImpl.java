package sast.evento.service.impl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.service.CodeService;
import sast.evento.utils.RedisUtil;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/29 15:40
 */
@Service
public class CodeServiceImpl implements CodeService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public String getAuthCode(Integer eventId) {
        String authcode = (String) redisUtil.get("AUTHCODE:" + eventId);
        if (authcode == null) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "验证码不存在或已过期");
        }
        return authcode;
    }
}
