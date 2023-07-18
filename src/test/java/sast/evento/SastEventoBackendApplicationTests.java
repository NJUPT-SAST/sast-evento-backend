package sast.evento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sast.evento.model.Permission;
import sast.evento.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SastEventoBackendApplicationTests {

	@Test
	void contextLoads() {
		//序列化测试
		Permission permission = Permission.getDefault();
		Permission.Statement statement = new Permission.Statement();
		statement.setResource("ACTION_351232424");
		statement.setConditions(new Date());
		statement.setActions(new ArrayList<>());
		permission.getStatements().add(statement);
		String json = JsonUtil.toJson(permission);
		System.out.println(json);
		Permission p2 = JsonUtil.fromJson(json, Permission.class);
		String json2 = JsonUtil.toJson(p2);
		System.out.println(json2.length());

	}

}
