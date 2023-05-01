package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.MessageHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - Employee Contact Management");
        /*User user=new User();
        user.setName("Vishal Pandey");
        user.setEmail("vishalpandey10022000@gmail.com");
        userRepository.save(user);*/
        return "home";
    }
    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Employee Contact Management");

        return "about";
    }
    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Employee Contact Management");
        model.addAttribute("user",new User());
        return "signup";
    }
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user")User user, BindingResult result, @RequestParam(value = "agreement",defaultValue = "false") boolean agreement, Model model, HttpSession session){
        try{
            if (!agreement){
                throw new Exception("You have not checked terms and conditions");

            }
            if (result.hasErrors()){
                model.addAttribute("user",user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            System.out.println("Agreement "+agreement);
            System.out.println("User "+user);
            this.userRepository.save(user);
            model.addAttribute("user",new User());
            session.setAttribute("message2",new MessageHelper("Successfully Register!!","alert-success"));
            return "signup";

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message2",new MessageHelper("Something Went Wrong","alert-danger"));
            return "signup";
        }

    }
    @GetMapping("/signin")
    public String CustomLogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }
}
