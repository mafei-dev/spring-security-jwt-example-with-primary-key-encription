package com.javatechie.jwt.api.controller;

import com.javatechie.jwt.api.entity.AuthRequest;
import com.javatechie.jwt.api.entity.Item;
import com.javatechie.jwt.api.util.JwtUtil;
import com.javatechie.jwt.api.util.UIDProtector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class WelcomeController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String welcome() {
        return "Welcome to javatechie !!";
    }


    @Value("${pk.secret}")
    private String pkSecret;

    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );
        } catch (Exception ex) {
            throw new Exception("inavalid username/password");
        }

        String uid = UUID.randomUUID().toString();
        String uidToken = UUID.randomUUID().toString();
        System.out.println("uid = " + uid);
        System.out.println("uidToken = " + uidToken);
        Date date = new Date();
        String mafei = uidProtector.encryptSingle(uidToken, uid + pkSecret + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date));
        return jwtUtil.generateToken(authRequest.getUserName(), mafei, uid, date);
    }

    @Autowired
    private UIDProtector uidProtector;

    @RequestMapping("/item")
    public List getItemList(@RequestAttribute("pk_key_token") String pk_key_token) {

        List<Item> dummyList = new ArrayList();
        dummyList.add(new Item("86ac7cde-da3b-4354-8aa8-1ba09a47e3e8", "item1"));
        dummyList.add(new Item("3171ec11-e868-4404-b235-3f8cd8ef2964", "item2"));
        dummyList.add(new Item("ac2b9dcf-32c2-4d62-a67e-0d9261df10da", "item3"));

        dummyList.forEach(item -> {
            try {
                uidProtector.encryptAll(item, pk_key_token);
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        });

        return dummyList;
    }

    @PostMapping("/item")
    public Object addItemList(@RequestBody Item item, @RequestAttribute("pk_key_token") String pk_key_token) throws IllegalAccessException {
        uidProtector.decryptAll(item, pk_key_token);
        return item;
    }
}
