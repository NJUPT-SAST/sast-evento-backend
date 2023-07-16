package sast.evento.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sast.evento.dataobject.Action;
import sast.evento.dataobject.User;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.ActionMapper;
import sast.evento.mapper.UserMapper;
import sast.evento.model.Permission;
import sast.evento.model.UserProFile;
import sast.evento.service.SastLinkService;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.JwtUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Title HttpInterceptor
 * @Description 获取访问信息,从数据库获取API限制信息*,获取用户信息*,从数据库获取用户权限*,
 * @Description 解析TOKEN信息获取本地用户登录信息,与API信息联合判断是否拦截
 * @Author feelMoose
 * @Date 2023/7/14 16:27
 */
@Slf4j//todo 针整点权限的日志
@Component
public class HttpInterceptor implements HandlerInterceptor {
    @Resource
    private ActionMapper actionMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private SastLinkService sastLinkService;
    @Resource
    private JwtUtil jwtUtil;
    public static ThreadLocal<UserProFile> userProFileHolder = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String URL = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader("TOKEN");
        Action action = getActionByAPI(URL,method);
        if(action == null || !action.getIsVisible()){
            throw new LocalRunTimeException(ErrorEnum.METHOD_NOT_EXIST);
        }else if(action.getIsPublic()){
            return true;
        }
        Map<String, Claim> map = jwtUtil.getClaims(token);
        String userId = map.get("uid").asString();//todo 更改token载荷内容
        Permission permission = getPermission(userId);
        if(!checkPermission(permission,action)){
            throw new LocalRunTimeException(ErrorEnum.PERMISSION_ERROR);
        }
        UserProFile userProFile = getUserProFileById(userId);
        userProFileHolder.set(userProFile);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) {
        userProFileHolder.remove();
    }
    @Cacheable(value = "action",key = "#url+#requestMethod")
    public Action getActionByAPI(String url,String requestMethod){
        Action action = actionMapper.selectOne(new LambdaQueryWrapper<Action>()
                .eq(Action::getURL,url)
                .and(Wrapper -> Wrapper.eq(Action::getMethod,requestMethod)));
        return action;
    }

    @Cacheable(value = "userProFile",key = "#userId")
    public UserProFile getUserProFileById(String userId){
        UserProFile userProFile = sastLinkService.getUserProFile(userId);
        return userProFile;
    }

    @Cacheable(value = "permission",key = "#userId")
    public Permission getPermission(String userId){
        User user = userMapper.selectById(userId);
        Permission permission = JsonUtil.fromJson(user.getPermission(), Permission.class);
        return  permission;
    }

    private Boolean checkPermission (Permission permission,Action action){
        List<Permission.Statement> statements = permission.getStatements();
        for (Permission.Statement statement:
             statements) {
            if(statement.getConditions().after(new Date())){
                continue;
            }
            if(statement.getResource().equals("all") || statement.getResource().equals(action.getActionName())){
                if(checkActions(statement,action)){
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean checkActions(Permission.Statement statement,Action action){
        List<Integer> actionIds = statement.getActionIds();
        for (Integer actionId:
                actionIds) {
            if(action.getId().equals(actionId)){
                return true;
            }
        }
        return false;
    }
}
