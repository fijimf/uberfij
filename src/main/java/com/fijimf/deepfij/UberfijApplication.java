package com.fijimf.deepfij;

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
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UberfijApplication {

    public static final Logger logger = LoggerFactory.getLogger(UberfijApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UberfijApplication.class, args);
        UserManager userMgr = context.getBean(UserManager.class);
        RandomStringGenerator rsg = context.getBean(RandomStringGenerator.class);
        String password = userMgr.setAdminPassword(getTempAdminPassword(rsg));
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
