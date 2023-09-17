package sast.evento.service;

import org.springframework.web.multipart.MultipartFile;
import sast.evento.entitiy.User;

import java.util.Map;

public interface ImageService {
    String upload(MultipartFile file, User user);

    Map<String, Object> getPictures(Integer num, Integer size);

    void deletePicture(String key);

}
