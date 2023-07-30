package sast.evento.service;

import java.awt.image.BufferedImage;

public interface QrCodeRegistrationService {
    BufferedImage getRegistrationQrCode(Integer eventId);

    /* 开启服务:访问自动开启(服务开启条件状态下) */
    /* 关闭服务:超时自动关闭(服务开启条件状态下) */
    Boolean checkRegistrationCode(Integer eventId,String registrationCode);

    /* 关闭服务:手动关闭服务(关闭服务条件和服务) */
    void close(Integer eventId);
    Boolean isClose(Integer eventId);

}
