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
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.ImageDOMapper;
import sast.evento.model.UserModel;
import sast.evento.service.ImageService;
import sast.evento.utils.CosUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImageDOMapper imageDOMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upload(MultipartFile file, UserModel user, String dir) {
        String key;
        try {
            key = CosUtil.upload(file, dir);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR, "upload failed");
        }
        String uri = CosUtil.changeKey2URL(key);
        String fileName = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "error fileName"));
        ImageDO imageDO = new ImageDO();
        imageDO.setCosKey(key);
        imageDO.setUri(uri);
        imageDO.setCosDir(dir);
        imageDO.setName(file.getOriginalFilename());
        imageDO.setExtension(fileName.substring(fileName.lastIndexOf(".")));
        imageDO.setSize(file.getSize());
        imageDO.setUserId(user.getId());
        imageDOMapper.insert(imageDO);
        return uri;
    }

    @Override
    public Map<String, Object> getPictures(Integer num, Integer size, String dir) {
        IPage<ImageDO> page = imageDOMapper.selectPage(new Page<>(num, size), Wrappers.lambdaQuery(ImageDO.class)
                .eq(ImageDO::getCosDir, dir));
        return Map.of("images", page.getRecords(), "total", page.getTotal());
    }

    @Override
    public List<String> getDirs() {
        return imageDOMapper.selectDirs();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePicture(String key, String dir) {
        if (imageDOMapper.delete(Wrappers.lambdaQuery(ImageDO.class)
                .eq(ImageDO::getCosDir, dir)
                .and(wrapper -> wrapper.eq(ImageDO::getCosKey, key))) == 0) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete failed");
        }
        CosUtil.deleteByKey(key);
    }
}
