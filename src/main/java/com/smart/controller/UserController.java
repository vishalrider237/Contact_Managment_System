package com.smart.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.MessageHelper;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private MyOrderRepository myOrderRepository;
    @ModelAttribute
    public void addCommonData(Model m,Principal principal){
        String username=principal.getName();
        System.out.println(username);
        User user=this.userRepository.getUserByUserName(username);
        System.out.println(user);
        m.addAttribute("user",user);

    }
    @GetMapping("/index")
    public String Dashboard(Model model, Principal principal){
        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }
    // Open Add Form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("image1Url")MultipartFile file, Principal principal, HttpSession session){
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            // Processing And Uploading File
            if (file.isEmpty()){
                contact.setImageUrl("contact1.png");
            }
            else{

                    contact.setImageUrl(file.getOriginalFilename());
                    File file1= new ClassPathResource("static/img").getFile();
                    Path path= Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
                   /* InputStream is=file.getInputStream();
                    byte [] data=new byte[is.available()];
                    is.read(data);
                    FileOutputStream fos=new FileOutputStream(path.toFile());
                    fos.write(data);
                    fos.close();*/
                      Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Image is Uploaded");


            }
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);

            System.out.println("Data " + contact);
            System.out.println("Added To Database");
            session.setAttribute("message2",new MessageHelper("Your Contact is Added Successfully","success"));
        }catch (Exception e){
            session.setAttribute("message2",new MessageHelper("Something Went Wrong","danger"));
            e.printStackTrace();
        }
        return "normal/add_contact_form";
    }
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page , Model m,Principal principal){
        m.addAttribute("title","Show User Contacts");
        String userName=principal.getName();
        User user=this.userRepository.getUserByUserName(userName);
        Pageable pageable=PageRequest.of(page,8);
        Page<Contact> contacts=this.contactRepository.findContactByUser(user.getId(),pageable);
       m.addAttribute("contacts",contacts);
       m.addAttribute("CurrentPage",page);
       m.addAttribute("TotalPages",contacts.getTotalPages());
      /*  List<Contact>contacts=user.getContacts();*/

        return "normal/show_contacts";
    }
    @GetMapping("/{cid}/contact")
    public String ShowContactDetail(@PathVariable("cid") Integer cid,Model model,Principal principal){
        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();
        String name = principal.getName();
        User userByUserName = this.userRepository.getUserByUserName(name);
        if (userByUserName.getId()==contact.getUser().getId()){
            model.addAttribute("contact",contact);
            model.addAttribute("title",contact.getName());
        }
        return "normal/contact_details";
    }
    @GetMapping("/delete/{cid}")
    public String DeleteContact(@PathVariable("cid")Integer cid,HttpSession session){
        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();
        if (contact.getCid()==cid){
            contact.setUser(null);
            this.contactRepository.deleteContactById(cid);
            session.setAttribute("message2",new MessageHelper("Contact Deleted Successfully...","success"));
        }

        return "redirect:/user/show-contacts/0";
    }
     // Open Form Handler
    @PostMapping("/update-contact/{cid}")
    public String UpdateContact(@PathVariable("cid")Integer cid,Model model){
        model.addAttribute("title","Update Contact");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact",contact);
        return "normal/update-form";
    }
    // Update Form Handler
    @PostMapping("/process-update")
    public String UpdateHandler(@ModelAttribute Contact contact,@RequestParam("image1Url")MultipartFile file,Model model,HttpSession session,Principal principal){
        System.out.println(contact.getCid());
        try {
            System.out.println(contact.getCid());
            Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
            if (!file.isEmpty()){

                File file2= new ClassPathResource("static/img").getFile();
                File file3=new File(file2,oldContact.getImageUrl());
                file3.delete();

                File file1= new ClassPathResource("static/img").getFile();
                Path path= Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImageUrl(file.getOriginalFilename());
            }
            else{
                contact.setImageUrl(oldContact.getImageUrl());
            }
            User UserName = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(UserName);
            this.contactRepository.save(contact);
            session.setAttribute("message2",new MessageHelper("Contact Updated Successfully...","success"));
        }catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:/user/"+contact.getCid()+"/contact";

    }
    @GetMapping("/profile")
    public String YourProfile(Model model){
        model.addAttribute("title","Profile Page");
        return "normal/profile";
    }
    @GetMapping("/setting")
    public String Settings(Model model){
        model.addAttribute("title","Setting Page");
        return "normal/setting";
    }
    @PostMapping("/change-password")
    public String ChangePassword(@RequestParam("oldPassword")String oldPass,@RequestParam("newPassword")String newPass,Principal principal,HttpSession session){
        String name = principal.getName();
        User CurrentUSER = this.userRepository.getUserByUserName(name);
       if (this.bCryptPasswordEncoder.matches(oldPass, CurrentUSER.getPassword())){
           // change the password
           CurrentUSER.setPassword(this.bCryptPasswordEncoder.encode(newPass));
           this.userRepository.save(CurrentUSER);
           session.setAttribute("message2",new MessageHelper("Password Changed Successfully...","success"));
       }
       else{
           session.setAttribute("message2",new MessageHelper("Wrong old Password !!Try Again...","danger"));
       }

        return "redirect:/logout";
    }
    @PostMapping("/create_order")
    @ResponseBody
    public String createPayment(@RequestBody Map<String, Object>data,Principal principal) throws RazorpayException {
      int amount=Integer.parseInt(data.get("amount").toString());
        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_WOouOhXyXspViP","yyCJJNNPkeO7JCrs6vdgUqzZ");
        JSONObject object=new JSONObject();
        object.put("amount",amount*100);
        object.put("currency","INR");
        object.put("receipt","txn_768766");

        // creating new Order
        Order order = razorpayClient.orders.create(object);

        // Save To Database
        MyOrder myOrder=new MyOrder();
        myOrder.setAmount(order.get("amount"));
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("created");
        String name = principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        myOrder.setUser(user);
        myOrder.setReceipt(order.get("receipt"));
        this.myOrderRepository.save(myOrder);
        return order.toString();
    }
    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String,Object>data){
        MyOrder order = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
       order.setPaymentId(data.get("payment_id").toString());
       order.setStatus(data.get("status").toString());
       this.myOrderRepository.save(order);
        return ResponseEntity.ok(Map.of("msg","updated"));
    }

}
