package com.swp.carcare.service;


import com.swp.carcare.config.SecurityUser;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.exception.UserBlockedException;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public boolean validateCredentials(String username, String password) {
        return userRepository.existsByEmailAndPassword(username, passwordEncoder.encode(password));
    }

    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println(username);
            Optional<UserEntity> userByUsername = userRepository.findByEmail(username);
            if (userByUsername.isEmpty()) {
                throw new UsernameNotFoundException("Invalid credentials!");
            }
            UserEntity user = userByUsername.get();

            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new UserBlockedException("Tài khoản của bạn đã bị khóa!");
            }

            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            String role = "ROLE_";
            Integer roleUser = user.getRole();
            if (roleUser == 0) {
                role += "ADMIN";
            } else if (roleUser == 1) {
                role += "OWNER";
            } else if (roleUser == 2) {
                role += "EMP";
            } else if (roleUser == 3) {
                role += "RECEP";
            }

            grantedAuthorities.add(new SimpleGrantedAuthority(role));

            return new SecurityUser(user.getEmail(), user.getPassword(), true, true, true, true, grantedAuthorities,
                    user.getEmail());
        } catch (UserBlockedException | UsernameNotFoundException e) {
            System.out.println("Lỗi: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Lỗi không xác định: " + e.getMessage());
            throw new UsernameNotFoundException("Invalid credentials!");
        }
    }

    public String validateUserInput(String email, String firstName, String lastName, String address,
                                    String gender, String phone, String password) {

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "Email không hợp lệ!";
        }

        if (emailExists(email)) {
            return "Email đã tồn tại. Hãy nhập Email mới!";
        }

        if (phoneExists(phone)) {
            return "Số điện thoại này đã được đăng ký. Hãy nhập số điện thoại mới!";
        }

        if (!firstName.matches("^[\\p{L} .'-]+$") || !lastName.matches("^[\\p{L} .'-]+$")) {
            return "Họ và tên không hợp lệ! Vui lòng chỉ nhập chữ.";
        }

        if (!phone.matches("^[0-9]{10,11}$")) {
            return "Số điện thoại không hợp lệ!";
        }

        String passwordValidation = validatePassword(password);
        if (passwordValidation != null) {
            return passwordValidation;
        }

        return null;
    }
    
    public void sendOtpMail(HttpSession session, String email, String firstName, String lastName,
                            String address, String gender,
                            String phone, String password) {
        session.setAttribute("otp-register", otpCode());
        session.setMaxInactiveInterval(360);

        String subject = "Đây là OTP của bạn";
        String mess = "Xin chào " + email + " \n" + "Đây là OTP của bạn: " + session.getAttribute("otp-register") + " \n" + "Hãy điền vào form!" + "\nCảm ơn!";
        this.emailSenderService.sendEmail(email, subject, mess);

        session.setAttribute("email", email);
        session.setAttribute("firstName", firstName);
        session.setAttribute("lastName", lastName);
        session.setAttribute("address", address);
        session.setAttribute("gender", gender);
        session.setAttribute("phone", phone);
        session.setAttribute("password", password);
    }

    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }

    private boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }

    private boolean phoneExists(String phone) {
        return ownerRepository.findByPhoneNumber(phone).isPresent();
    }

    private String validatePassword(String password) {
        if (password.length() < 7) {
            return "Mật khẩu phải có ít nhất 7 ký tự!";
        } else if (!password.matches(".*[a-z].*")) {
            return "Mật khẩu phải có ít nhất 1 chữ thường!";
        } else if (!password.matches(".*[A-Z].*")) {
            return "Mật khẩu phải có ít nhất 1 chữ hoa!";
        } else if (!password.matches(".*\\d.*")) {
            return "Mật khẩu phải có ít nhất 1 số!";
        } else if (!password.matches(".*[@$!%*?&].*")) {
            return "Mật khẩu phải có ít nhất 1 ký tự đặc biệt (@$!%*?&)!";
        } else if (!password.matches("^[A-Za-z\\d@$!%*?&]{7,}$")) {
            return "Mật khẩu chỉ được chứa chữ cái, số và ký tự đặc biệt @$!%*?&!";
        }
        return null;
    }
}

