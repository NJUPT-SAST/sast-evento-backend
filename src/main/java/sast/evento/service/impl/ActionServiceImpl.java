package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sast.evento.dataobject.Action;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.ActionMapper;
import sast.evento.service.ActionService;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/17 19:52
 */
@Service
public class ActionServiceImpl implements ActionService {
    @Resource
    private ActionMapper actionMapper;

    @Override
    @Cacheable(value = "action", key = "#url+#requestMethod")
    public Action getActionByAPI(String url, String requestMethod) {
        Action action = actionMapper.selectOne(new LambdaQueryWrapper<Action>()
                .eq(Action::getUrl, url)
                .and(Wrapper -> Wrapper.eq(Action::getMethod, requestMethod)));
        return action;
    }

    @Override
    @CachePut(value = "action", key = "#url+#requestMethod")
    public Action addActionByAPI(String url, String requestMethod, String actionName) {
        Action action = new Action()
                .setActionName(actionName)
                .setUrl(url)
                .setMethod(requestMethod)
                .setIsVisible(true)
                .setIsPublic(false);
        actionMapper.insert(action);
        return action;
    }

    @Override
    @CachePut(value = "action", key = "#url+#requestMethod")
    public Action setActionVisible(String url, String requestMethod, Boolean isVisible) {
        Action action = getActionByAPI(url, requestMethod);
        if (action.getIsVisible().equals(isVisible)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "IsVisible has been already set.");
        }
        action.setIsVisible(isVisible);
        actionMapper.updateById(action);
        return action;
    }

    @Override
    @CachePut(value = "action", key = "#url+#requestMethod")
    public Action setActionPublic(String url, String requestMethod, Boolean isPublic) {
        Action action = getActionByAPI(url, requestMethod);
        if (action.getIsPublic().equals(isPublic)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "IsPublic has been already set.");
        }
        action.setIsPublic(isPublic);
        actionMapper.updateById(action);
        return action;
    }

    @Override
    @CachePut(value = "action", key = "#url+#requestMethod")
    public Action setActionName(String url, String requestMethod, String actionName) {
        Action action = getActionByAPI(url, requestMethod);
        if (action.getActionName().equals(actionName)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "ActionName has been already set.");
        }
        action.setActionName(actionName);
        actionMapper.updateById(action);
        return action;


    }


}
