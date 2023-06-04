package com.fijimf.deepfij.services;

import com.fijimf.deepfij.db.model.user.User;
import com.fijimf.deepfij.services.user.UserManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public static final String USER_LOGIN_TEMPLATE = "user/login";
    public static final String USER_FORGOT_PASSWORD_TEMPLATE = "user/forgotPassword";
    public static final String USER_CHANGE_PASSWORD_TEMPLATE = "user/changePassword";
    public static final String USER_SIGNUP_TEMPLATE = "user/signup";
    public static final String USER_SIGNUP_COMPLETE_TEMPLATE = "user/signupComplete";
    public static final String FLASHING_ERROR_KEY = "error";
    private final UserManager userManager;
    private final Mailer mailer;

    @Autowired
    public UserController(
            UserManager userManager, Mailer mailer) {
        this.userManager = userManager;
        this.mailer = mailer;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        //TODO make sure user isn't signed in
        model.addAttribute("user", new User());
        return USER_SIGNUP_TEMPLATE;
    }

//    @PostMapping("/signup")
//    public String signup(@ModelAttribute UserForm user, Model model, HttpServletRequest request) {
//        model.addAttribute("email", user.getEmail());
//        model.addAttribute("username", user.getUsername());
//        try {
//            String authCode = userManager.createNewUser(user.getUsername(), user.getPassword(), user.getEmail(), List.of("USER"));
//            mailer.sendAuthEmail(user.getUsername(), user.getEmail(), authCode, request.getServerName());
//            return USER_SIGNUP_COMPLETE_TEMPLATE;
//        } catch (IllegalArgumentException ex) {
//            logger.warn("Illegal argument creating user", ex);
//            model.addAttribute(FLASHING_ERROR_KEY, ex.getMessage());
//            return USER_SIGNUP_TEMPLATE;
//        } catch (DuplicatedEmailException ex) {
//            logger.warn("", ex);
//            model.addAttribute(FLASHING_ERROR_KEY, ex.getMessage());
//            return USER_SIGNUP_TEMPLATE;
//        } catch (DuplicatedUsernameException ex) {
//            model.addAttribute(FLASHING_ERROR_KEY, ex.getMessage());
//            return USER_SIGNUP_TEMPLATE;
//        } catch (MessagingException e) {
//            logger.error("", e);
//            return USER_SIGNUP_COMPLETE_TEMPLATE; //TODo Replace with 'Unspecified error.  Try again later'
//        }
//    }

    @GetMapping("/login")
    public String login() {
        return USER_LOGIN_TEMPLATE;
    }

    @GetMapping("/activate/{token}")
    public String activate(@PathVariable("token") String token) {
        userManager.activateUser(token);
        return USER_LOGIN_TEMPLATE;
    }

//    @GetMapping("/changePassword")
//    public String changePasswordForm(Model model) {
//        model.addAttribute("changePassword", new ChangePasswordForm("", ""));
//        return USER_CHANGE_PASSWORD_TEMPLATE;
//    }

//    @PostMapping("/changePassword")
//    public ModelAndView changePassword(@ModelAttribute ChangePasswordForm cp) {
//        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (p instanceof User) {
//            User u = (User) p;
//            String username = u.getUsername();
//            if (!username.equals("anonymous")) {
//                try {
//                    userManager.changePassword(username, cp.getOldPassword(), cp.getNewPassword()).ifPresent(mailer::sendPasswordChanged);
//                    return new ModelAndView("redirect:/index");
//                } catch (BadCredentialsException bce) {
//                    logger.error("Bad credentials given", bce);
//                    return new ModelAndView(USER_LOGIN_TEMPLATE, Map.of(FLASHING_ERROR_KEY,"Bad credentials given."));
//                }
//            } else {
//                logger.warn("Attempt to change password while not logged in.");
//                return new ModelAndView(USER_LOGIN_TEMPLATE, Map.of(FLASHING_ERROR_KEY,"No user is logged in.  Cannot Change password"));
//            }
//        } else {
//            logger.warn("Attempt to change password while not logged in.");
//            return new ModelAndView(USER_LOGIN_TEMPLATE, Map.of(FLASHING_ERROR_KEY,"No user is logged in.  Cannot Change password"));
//        }
//    }

//    @GetMapping("/forgotPassword")
//    public String forgotPasswordForm(Model model, HttpServletRequest request) throws ServletException {
//        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != "anonymous") {
//            request.logout();
//        }
//        model.addAttribute("forgotPassword", new ForgotPasswordForm(""));
//        return USER_FORGOT_PASSWORD_TEMPLATE;
//    }

//    @PostMapping("/forgotPassword")
//    public String forgotPassword(@ModelAttribute ForgotPasswordForm forgotPassword) {
//        String email = forgotPassword.getEmail();
//        if (StringUtils.isNotBlank(email)) {
//            userManager.forgottenPassword(email).ifPresent(password ->
//                    userManager.forgottenUser(email).ifPresent(name -> {
//                        try {
//                            mailer.sendForgotPasswordEmail(email, name, password);
//                        } catch (MessagingException e) {
//                            logger.error("Failed to send password reset to {}", email, e);
//                        }
//                    }));
//
//        }
//        return USER_LOGIN_TEMPLATE;
//    }

}