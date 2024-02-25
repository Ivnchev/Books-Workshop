package softuni.workshop.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.workshop.service.services.UserService;
import softuni.workshop.web.models.UserRegisterModel;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return this.view("user/register");
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return this.view("user/login");
    }

    @PostMapping("/register")
    public ModelAndView registerConfirm(UserRegisterModel model) {
        if(!model.getPassword().equals(model.getConfirmPassword())) {
            return super.redirect("/users/register");
        }


        this.userService.registerUser(model);

        return super.redirect("/users/login");
    }

}
