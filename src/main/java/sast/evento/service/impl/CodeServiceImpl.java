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
    public Integer getEventIdFromAuthCode(String code) {
        String eventId = (String) redisUtil.get("AUTHCODE:" + code);
        if (eventId == null ){
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"auth-code not exists or has expired");
        }
        Integer eventid =  Integer.valueOf(eventId);
        System.out.println(eventid);
        return eventid;
    }
}
