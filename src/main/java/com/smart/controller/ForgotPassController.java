package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.Random;

@Controller
public class ForgotPassController {
    Random random = new Random(1000);
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/forgot")
    public String openEmailForm(){

        return "forgot_email_form";
    }
    @PostMapping("/send-otp")
    public String SendOTP(@RequestParam("email")String email, HttpSession session){
        int otp= Integer.parseInt(new DecimalFormat("000000").format(new Random().nextInt(999999)));

        System.out.println("OTP "+otp);
        String subject="OTP from Contact Management System";
        String message="<div style='border:1px solid #e2e2e2';padding:20px;>"+
                 "<h1>"+
                "OTP="+
                "<b>"+
                otp+
                "</b>"+
                "</h1>"+
                 "</div>";
        String to=email;
        boolean b = this.emailService.sendEmail(subject, message, to);
        if (b){
            session.setAttribute("myotp",otp);
            session.setAttribute("email1",email);
            return "verify_otp";
        }
        else {
            session.setAttribute("message1","OTP not send successfully");
           return "forgot_email_form";
        }
    }
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp")int otp,HttpSession session){
        int myOTP=(int)session.getAttribute("myotp");
        String email=(String) session.getAttribute("email1");
        if (myOTP==otp){
            User user = this.userRepository.getUserByUserName(email);
            if (user==null){
              //
                return "forgot_email_form";
            }
            else{
                return "change_password";
            }

        }
        else{
            session.setAttribute("message1","OTP did not Match ,plz try again");
            return "verify_otp";
        }
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword")String password,HttpSession session){
        String email=(String) session.getAttribute("email1");
        User user = this.userRepository.getUserByUserName(email);
         user.setPassword(this.passwordEncoder.encode(password));
         this.userRepository.save(user);
        return "redirect:/signin?change=Password Changed Successfully";
    }
}
