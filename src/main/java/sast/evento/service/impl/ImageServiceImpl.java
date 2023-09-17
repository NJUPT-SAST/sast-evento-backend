package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.ImageDO;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.ImageDOMapper;
import sast.evento.service.ImageService;
import sast.evento.utils.CosUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImageDOMapper imageDOMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upload(MultipartFile file, User user) {
        String key;
        try {
            key = CosUtil.upload(file, "");
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR, "upload failed");
        }
        String uri = CosUtil.changeKey2URL(key);
        String fileName = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "error fileName"));
        ImageDO imageDO = new ImageDO();
        imageDO.setCosKey(key);
        imageDO.setUri(uri);
        imageDO.setName(file.getName());
        imageDO.setExtension(fileName.substring(fileName.lastIndexOf(".")));
        imageDO.setSize(file.getSize());
        imageDO.setUserId(user.getUserId());
        imageDOMapper.insert(imageDO);
        return uri;
    }

    @Override
    public Map<String, Object> getPictures(Integer num, Integer size) {
        IPage<ImageDO> page = imageDOMapper.selectPage(new Page<>(num, size), null);
        return Map.of("images", page.getRecords(), "total", page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePicture(String key) {
        if (imageDOMapper.delete(Wrappers.lambdaQuery(ImageDO.class).eq(ImageDO::getCosKey, key)) == 0) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete failed");
        }
        CosUtil.deleteByKey(key);
    }
}
