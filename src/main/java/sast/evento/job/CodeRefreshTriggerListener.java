package sast.evento.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.service.CodeService;
import sast.evento.utils.SpringContextUtil;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/30 16:01
 */
@Slf4j
@AllArgsConstructor
public class CodeRefreshTriggerListener extends TriggerListenerSupport {
    private Integer eventId;

    @Override
    public String getName() {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "TriggerName is empty");
        }
        return String.valueOf(eventId);
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        /* 超时自动关闭(服务开启条件状态下) */
        CodeService codeServiceBean = SpringContextUtil.getBean(CodeService.class);
        codeServiceBean.deleteCode(eventId);
        log.info("Code refresh job complete, code and qrcode has been removed.");
    }


}
