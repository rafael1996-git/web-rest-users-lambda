package com.talentport.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.talentport.users.controller.UsersController;
import com.trader.core.TraderCoreApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@Import({ UsersController.class })
public class TalentPortUsersApplication extends TraderCoreApplication{

	public static void main(String[] args) {
		SpringApplication.run(TalentPortUsersApplication.class, args);
	}

}
