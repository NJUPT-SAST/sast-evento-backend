package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.annotation.HandlesTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.utils.JsonUtil;
import sast.sastlink.sdk.enums.Organization;
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
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private String id;
    @JsonIgnore
    private String unionId;//openId属于隐私信息，不得随意当成返回
    @JsonIgnore
    private String openId;

    private String linkId;

    private String studentId;

    private String email;

    private String nickname;

    private String avatar;

    @JsonAlias("org")
    @TableField("org")
    private Integer organization; //这里使用sast-link SDK 与sast-link遵守相同规范

    @JsonAlias("bio")
    @TableField("bio")
    private String biography;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> link;

    public User(UserInfo userInfo){
        this.linkId = userInfo.getUserId();
        this.studentId = userInfo.getUserId();
        this.email = userInfo.getEmail();
        this.nickname = userInfo.getNickname();
        this.avatar = userInfo.getAvatar();
        this.organization = (userInfo.getOrg() == null||userInfo.getOrg().isEmpty())?null: Organization.valueOf(userInfo.getOrg()).getId();
        this.biography = userInfo.getBio();
        this.link = userInfo.getLink();
    }

}
