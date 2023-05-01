package com.smart.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public  boolean sendEmail(String subject,String message ,String to){
        boolean f=false;
        String from="pandey_931922@student.nitw.ac.in";
        String host="smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth", "true");
        Session session= Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("vishalpandey10022000@gmail.com","nwzowbyajdovyqzy");
            }
        });
        session.setDebug(true);
        MimeMessage mimeMessage=new MimeMessage(session);
        try {
            mimeMessage.setFrom(from);
            mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            mimeMessage.setSubject(subject);
        //    mimeMessage.setText(message);
            mimeMessage.setContent(message,"text/html");
            Transport.send(mimeMessage);
            System.out.println("Message Send Successfully.....");
            f=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return f;
    }
}
