package com.swp.carcare.controller.auth;


import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.service.EmailSenderService;
import com.swp.carcare.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private OwnerRepository ownerRepository;
    @GetMapping("/login")
    public String showLogin(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        return "auth/login";
    }

    @GetMapping("/404")
    public String notFound() {
        return "auth/404";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/?logout";
    }

    @RequestMapping(value = "register")
    public String addUser() {
        return "auth/register";
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam String address, @RequestParam String gender,
                       @RequestParam String phone, @RequestParam String password, Model model, HttpSession session) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("mess", "Email không hợp lệ!");
            return "auth/register";
        }

        if (userService.findByEmail(email).isPresent()) {
            model.addAttribute("mess", "Email đã tồn tại. Hãy nhập Email mới!");
            return "auth/register";
        }

        if (ownerRepository.findByPhoneNumber(phone).isPresent()) {
            model.addAttribute("mess", "Số điện thoại này đã được đăng ký. Hãy nhập số điện thoại mới!");
            return "auth/register";
        }

        if (!firstName.matches("^[\\p{L} .'-]+$") || !lastName.matches("^[\\p{L} .'-]+$")) {
            model.addAttribute("mess", "Họ và tên không hợp lệ! Vui lòng chỉ nhập chữ.");
            return "auth/register";
        }

        if (!phone.matches("^[0-9]{10,11}$")) {
            model.addAttribute("mess", "Số điện thoại không hợp lệ!");
            return "auth/register";
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$")) {
            if (password.length() < 7) {
                model.addAttribute("mess", "Mật khẩu phải có ít nhất 7 ký tự!");
            } else if (!password.matches(".*[a-z].*")) {
                model.addAttribute("mess", "Mật khẩu phải có ít nhất 1 chữ thường!");
            } else if (!password.matches(".*[A-Z].*")) {
                model.addAttribute("mess", "Mật khẩu phải có ít nhất 1 chữ hoa!");
            } else if (!password.matches(".*\\d.*")) {
                model.addAttribute("mess", "Mật khẩu phải có ít nhất 1 số!");
            } else if (!password.matches(".*[@$!%*?&].*")) {
                model.addAttribute("mess", "Mật khẩu phải có ít nhất 1 ký tự đặc biệt (@$!%*?&)!");
            } else {
                model.addAttribute("mess", "Mật khẩu chỉ được chứa chữ cái, số và ký tự đặc biệt @$!%*?&!");
            }
            return "auth/register";
        }

        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);
        if (userService.findByEmail(email).isPresent()) {
            model.addAttribute("mess", "Email đã tồn tại. Hãy nhập Email mới!");
            return "auth/register";
        }
        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);
        String subject = "Đây là OTP của bạn";
        String mess = "Xin chào "  + email + " \n" + "Đây là OTP của bạn: " + session.getAttribute("otp-register") + " \n" + "Hãy điền vào form!" + "\nCảm ơn!";
        this.emailSenderService.sendEmail(email, subject, mess);
        session.setAttribute("email", email);
        session.setAttribute("firstName", firstName);
        session.setAttribute("lastName", lastName);
        session.setAttribute("address", address);
        session.setAttribute("gender", gender);
        session.setAttribute("phone", phone);
        session.setAttribute("password", password);
        return "redirect:/otp-check";
    }

    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }

}
