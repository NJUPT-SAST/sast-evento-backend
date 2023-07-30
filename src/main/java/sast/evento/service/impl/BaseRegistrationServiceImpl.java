package sast.evento.service.impl;

import org.springframework.stereotype.Service;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.service.BaseRegistrationService;
import sast.evento.utils.QrCodeUtil;
import sast.evento.utils.SchedulerUtil;
import sast.evento.utils.StringUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/29 15:40
 */
@Service
public class BaseRegistrationServiceImpl implements BaseRegistrationService {
    private static final Map<Integer, String> eventId2Code = new HashMap<>();
    private static final Map<Integer, BufferedImage> eventId2QrCode = new HashMap<>();

    /* 刷新是交给job负责的,使用时最好只访问get */
    @Override
    public BufferedImage refreshCode(Integer eventId) {
        String code = StringUtil.getRandomString(10, new Random());
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = QrCodeUtil.generateQrCode(code);
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.QRCODE_ERROR);
        }
        if (eventId2QrCode.get(eventId) == null) {
            eventId2Code.put(eventId, code);
            eventId2QrCode.put(eventId, bufferedImage);
        } else {
            eventId2Code.replace(eventId, code);
            eventId2QrCode.replace(eventId, bufferedImage);
        }
        return bufferedImage;
    }

    /* 第一次访问get前，需要手动刷新一次Codrene */
    @Override
    public String getCode(Integer eventId) {
        String code = eventId2Code.get(eventId);
        if (code.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no match eventId.");
        }
        return code;
    }

    @Override
    public BufferedImage getQrCode(Integer eventId) {
        BufferedImage bufferedImage = eventId2QrCode.get(eventId);
        if (bufferedImage == null) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no match eventId.");
        }
        return bufferedImage;
    }

    @Override
    public void deleteCode(Integer eventId) {
        if(!eventId2Code.containsKey(eventId)){
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no match eventId.");
        }
        eventId2Code.remove(eventId);
        eventId2QrCode.remove(eventId);

    }
}
