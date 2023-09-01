package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/24 20:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "slide")
public class Slide {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    private String link;

    private String url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer eventId;

}
