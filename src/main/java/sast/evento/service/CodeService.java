package sast.evento.service;

import java.awt.image.BufferedImage;

public interface CodeService {
    /* 生成QrCode并判断更新或者存储Qrcode */
    BufferedImage refreshCode(Integer eventId);
    /* 获取code */
    String getCode(Integer eventId);
    /* 获取QrCode */
    BufferedImage getQrCode(Integer eventId);
    /* 删除code和QrCode */
    void deleteCode(Integer eventId);//一定要想办法清理干净
}
