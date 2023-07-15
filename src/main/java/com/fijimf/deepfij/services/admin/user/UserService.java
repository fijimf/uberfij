package com.fijimf.deepfij.services.admin.user;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.services.user.UserManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/user")
public class UserService {

    private final UserManager userManager;

    public UserService(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping("/index")
    public ModelAndView showOverallStatus() {
        ModelAndView modelAndView = new ModelAndView("user/index.html");
        modelAndView.addObject("users", userManager.findAllUsers());
        return modelAndView;
    }
}
