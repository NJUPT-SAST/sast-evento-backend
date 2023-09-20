package sast.evento.service;

import org.springframework.web.multipart.MultipartFile;
import sast.evento.entitiy.User;

import java.util.List;
import java.util.Map;

public interface ImageService {
    String upload(MultipartFile file, User user,String dir);

    Map<String, Object> getPictures(Integer num, Integer size,String dir);

    List<String> getDirs();

    void deletePicture(String key,String dir);

}
