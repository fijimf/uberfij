package com.fijimf.deepfij;

import com.fijimf.deepfij.db.model.user.User;
import com.fijimf.deepfij.db.repo.user.UserRepo;
import com.fijimf.deepfij.services.Mailer;
import com.fijimf.deepfij.services.user.UserManager;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class UberfijApplication {

	public static final Logger logger = LoggerFactory.getLogger(UberfijApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(UberfijApplication.class, args);
		UserManager userMgr = context.getBean(UserManager.class);
		UserRepo userRepository = context.getBean(UserRepo.class);
		PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
		RandomStringGenerator rsg = context.getBean(RandomStringGenerator.class);
		String password = getTempAdminPassword(rsg);
		Optional<User> ou = userRepository.findFirstByUsername("admin");
		if (ou.isPresent()) {
			User u = ou.get();
			String encode = passwordEncoder.encode(password);
			u.setPassword(encode);
			u.setActivated(true);
			u.setLocked(false);
			u.setExpireCredentialsAt(LocalDateTime.now().plusMinutes(10));
			userRepository.save(u);
		} else {
			String token = userMgr.createNewUser("admin", password, "deepfij@gmail.com", List.of("ROLE_USER", "ROLE_ADMIN"), 10);
			userMgr.activateUser(token);
		}
		logger.info("admin password is {}", password);
		Mailer mailer = context.getBean(Mailer.class);
		try {
			mailer.sendStartupMessage(password);
		} catch (MessagingException e) {
			logger.error("Failed mailing server startup message", e);
		}
	}

	private static String getTempAdminPassword(RandomStringGenerator rsg) {
		String p = System.getProperty("admin.password");
		if (StringUtils.isNotBlank(p)) {
			return p;
		} else {
			return rsg.generate(6);
		}
	}

}
