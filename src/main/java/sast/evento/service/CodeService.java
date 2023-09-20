package sast.evento.service;

import java.awt.image.BufferedImage;

public interface CodeService{

    /* 获取code */
    String getAuthCode(Integer eventId);
}
