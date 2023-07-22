package com.fijimf.deepfij.services.admin.user;

import com.fijimf.deepfij.services.user.UserManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

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

    @GetMapping("/lock/{id}")
    public ModelAndView lock(@PathVariable("id") Long id) {
        userManager.lock(id);
        return new ModelAndView("redirect:/admin/user/index");
    }

    @GetMapping("/unlock/{id}")
    public ModelAndView unlock(@PathVariable("id") Long id) {
        userManager.unlock(id);
        return new ModelAndView("redirect:/admin/user/index");
    }

    @GetMapping("/persistCreds/{id}")
    public ModelAndView persistCreds(@PathVariable("id") Long id) {
        userManager.persistCreds(id);
        return new ModelAndView("redirect:/admin/user/index");
    }

    @GetMapping("/expireCreds/{id}")
    public ModelAndView expireCreds(@PathVariable("id") Long id) {
        userManager.expireCreds(id, LocalDateTime.now().plusMinutes(5));
        return new ModelAndView("redirect:/admin/user/index");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        userManager.delete(id);
        return new ModelAndView("redirect:/admin/user/index");
    }

    @GetMapping("/activate/{id}")
    public ModelAndView activate(@PathVariable("id") Long id) {
        userManager.forceActivate(id);
        return new ModelAndView("redirect:/admin/user/index");
    }

}
