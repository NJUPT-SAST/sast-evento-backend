package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.sastlink.sdk.model.UserInfo;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/13 16:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user")
public class User {
    @TableId(value = "id",type = IdType.AUTO)
    private String id;
    @JsonIgnore
    private String openId;//openId属于隐私信息，不得随意当成返回

    private String linkId;

    private String email;

    private String nickname;

    private String avatar;

    @JsonAlias("dep")
    private String dep; //这里使用sast-link SDK 与sast-link遵守相同规范

    @JsonAlias("org")
    private Integer organization; //这里使用sast-link SDK 与sast-link遵守相同规范

    @JsonAlias("bio")
    private String biography;

    private String link;

    private String hide;

}
