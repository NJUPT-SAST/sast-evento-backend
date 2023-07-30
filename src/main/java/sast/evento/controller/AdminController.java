package sast.evento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.enums.ActionState;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/13 15:50
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/post")
    public void post(@EventId(value = "id") @RequestParam Integer id){
        System.out.println(id);
    }

}
