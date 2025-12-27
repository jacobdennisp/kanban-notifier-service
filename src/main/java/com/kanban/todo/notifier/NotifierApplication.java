package com.kanban.todo.notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude= {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
@EnableAsync
public class NotifierApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifierApplication.class, args);
		System.out.println("\n"+
		"=======================================================================\n"+
		"| Kanban Notification Service Start Successfully						\n"+
		"| Rabbit MQ Consumer : ACTIVE											\n"+
		"| REST API URL : http://localhost:8081									\n"+
		"| Health Check API URL : http://localhost:8081/actuator/health			\n"+
		"=======================================================================\n");
	}

}
