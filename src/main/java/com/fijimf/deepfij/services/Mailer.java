package com.fijimf.deepfij.services;

import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import com.fijimf.deepfij.db.model.user.User;
import com.fijimf.deepfij.scraping.PublishResult;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.InputStream;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class Mailer {
    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);
    private byte[] imgBytes;

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    public Mailer(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        try {
            InputStream resource = ClassLoader
                    .getSystemClassLoader()
                    .getResourceAsStream("static/img/deepfij.png");
            imgBytes = resource == null ? new byte[0] : resource.readAllBytes();
        } catch (Exception e) {
            imgBytes = new byte[0];
        }
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMail(String to, String subject, String template, Map<String, Object> context)
            throws MessagingException {
        final Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("imageResourceName", "deepfij.png");
        ctx.setVariables(context);
        final String htmlContent = templateEngine.process(template, ctx);

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setSubject(subject);
        message.setFrom("deepfij@gmail.com");
        message.setTo(to);
        message.setText(htmlContent, true); // true = isHtml
        final InputStreamSource imageSource = new ByteArrayResource(imgBytes);
        message.addInline("deepfij.png", imageSource, "image/png");
        javaMailSender.send(mimeMessage);
    }

    public void sendStartupMessage(String password) throws MessagingException {
        final Map<String, Object> ctx = new HashMap<>();
        ctx.put("time", LocalDateTime.now());
        try {
            ctx.put("hostname", InetAddress.getLocalHost().getHostName());
        } catch (Exception ex) {
            ctx.put("hostname", "unknown");
        }
        ctx.put("password", password);
        sendMail("fijimf@gmail.com", "Überfij started", "mail/server-started.html", ctx);
    }

    public void sendAuthEmail(String username, String email, String authCode, String server) throws MessagingException {
        final Map<String, Object> ctx = new HashMap<>();
        ctx.put("username", username);
        ctx.put("token", authCode);
        ctx.put("server", server);
        sendMail(email, "Activate Deepfij account", "mail/activate-account.html", ctx);
    }

    public void sendForgotPasswordEmail(String email, String name, String password) throws MessagingException {
        final Map<String, Object> ctx = new HashMap<>();
        ctx.put("username", name);
        ctx.put("password", password);
        sendMail(email, "Deepfij password", "mail/forgot-password.html", ctx);
    }

    public void sendPasswordChanged(User u) {
        try {
            final Map<String, Object> ctx = new HashMap<>();
            ctx.put("username", u.getUsername());
            sendMail(u.getEmail(), "Deepfij password changed", "mail/password-changed.html", ctx);
        } catch (MessagingException e) {
            logger.error("Exception sending password changed email",e);
        }
    }

    public void sendGamesUpdatedMessage(EspnSeasonScrape ss, List<PublishResult> results) {
        final Map<String, Object> ctx = new HashMap<>();
        ctx.put("username", ss.getFrom());
        ctx.put("username", ss.getTo());
        ctx.put("startedAt", getUSEastFromUTC(ss.getStartedAt()));
        ctx.put("completedAt", getUSEastFromUTC(ss.getCompletedAt()));
        ctx.put("id", ss.getId());
        ctx.put("results", results);
        try {
            sendMail("fijimf@gmail.com", "Schedule Updated", "mail/schedule-updated.html", ctx);
        } catch (MessagingException e) {
            logger.error("Failed to send games updated mail", e);
        }
    }

    public LocalDate getUSEastFromUTC(LocalDateTime utc) {
        return utc.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("US/Eastern")).toLocalDate();
    }
}
