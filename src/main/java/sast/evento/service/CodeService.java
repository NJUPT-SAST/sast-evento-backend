package sast.evento.service;

import java.awt.image.BufferedImage;

public interface CodeService{

    /* 获取code */
    Integer getEventIdFromAuthCode(String code);
}
