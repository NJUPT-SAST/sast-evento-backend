package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fun.feellmoose.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.common.typehandler.OrganizationTypeHandler;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/13 16:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user", autoResultMap = true)
public class User {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
    @TableField(value = "org", typeHandler = OrganizationTypeHandler.class)
    private String organization; //这里使用sast-link SDK 与sast-link遵守相同规范

    @JsonAlias("bio")
    @TableField("bio")
    private String biography;
    @TableField(value = "link", typeHandler = JacksonTypeHandler.class)
    private List<String> link;

    public User(UserInfo userInfo) {
        this.linkId = userInfo.getUserId();
        this.studentId = userInfo.getUserId();
        this.email = userInfo.getEmail();
        this.nickname = userInfo.getNickname();
        this.avatar = userInfo.getAvatar();
        this.organization = userInfo.getOrg();
        this.biography = userInfo.getBio();
        this.link = userInfo.getLink();
    }

}
