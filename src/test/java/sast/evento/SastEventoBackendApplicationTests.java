package sast.evento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sast.evento.config.ActionRegister;
import sast.evento.entitiy.Permission;
import sast.evento.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SastEventoBackendApplicationTests {

	@Test
	void getAllMethodNameByJson() {
		//序列化测试
		String json = JsonUtil.toJson(new ArrayList<>(ActionRegister.actionNameSet));
		System.out.println("json: "+json);
		System.out.println("max lengh: "+json.length());
	}
}
