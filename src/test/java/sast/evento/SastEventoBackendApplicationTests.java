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
		Permission policy = new Permission();
		policy.setVersion(new Date());
		List<Permission.Statement> list = new ArrayList<>();
		Permission.Statement statement1 = new Permission.Statement();
		List<Integer> actions= new ArrayList<>();
		actions.add(111);
		actions.add(11);
		statement1.setActionIds(actions);
		statement1.setResource("/aa/aa");
		statement1.setConditions(new Date());
		list.add(statement1);
		policy.setStatements(list);
		String json = JsonUtil.toJson(policy);
		System.out.println(json);
		Permission p2 = JsonUtil.fromJson(json, Permission.class);
		String json2 = JsonUtil.toJson(p2);
		System.out.println(json2);
	}

}
