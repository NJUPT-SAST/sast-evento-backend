package sast.evento.service;

import org.springframework.web.multipart.MultipartFile;
import sast.evento.model.UserModel;

import java.util.List;
import java.util.Map;

public interface ImageService {
    String upload(MultipartFile file, UserModel user, String dir);

    Map<String, Object> getPictures(Integer num, Integer size, String dir);

    List<String> getDirs();

    void deletePicture(String key, String dir);

}
