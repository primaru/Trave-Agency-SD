package com.example.SD.controller;

import com.example.SD.repository.ClientRepository;
import com.example.SD.model.Client;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @Autowired
    private ClientRepository clientRepository;
    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("loginError") != null) {
            model.addAttribute("loginError", session.getAttribute("loginError"));
            session.removeAttribute("loginError");
        }
        return "login";
    }

    @PostMapping("/register")
    public String register(@RequestParam String newUsername, @RequestParam String newPassword, Model model) {
        if (clientRepository.findByUsername(newUsername)!=null) {
            return "redirect:/login?error=User already exists";
        }
        boolean invalidUsernameOrPassword=false;

        if (newUsername.isEmpty() || newPassword.isEmpty()) {

            invalidUsernameOrPassword=true;
        }
        String usernameRegex = "^[a-zA-Z0-9]+$";

        if (newUsername.length() <= 3 || !newUsername.matches(usernameRegex)) {
            invalidUsernameOrPassword=true;
        }

        // Validate password: At least 5 characters and contains a capital letter
        if (newPassword.length() < 5 || !newPassword.matches(".*[A-Z].*")) {
            invalidUsernameOrPassword=true;
        }
        if (invalidUsernameOrPassword) {
            model.addAttribute("registrationError", "Invalid username or password");
            model.addAttribute("showRegistrationForm", true);

            return "login";
        }
        // Create and save new user
        Client newClient = new Client();
        newClient.setUsername(newUsername);
        newClient.setPassword(newPassword);
        clientRepository.save(newClient);

        return "redirect:/login?success";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/login";
    }
}
