package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("image")
public class ImageDO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String userId;
    private String cosKey;
    private String extension;
    private String uri;
    private Long size;
    private Date gmtUploadTime;
}
