package com.ecomm.web.service;

import com.ecomm.web.model.AppUser;
import com.ecomm.web.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String USER_SESSION_KEY = "userId";
    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public boolean register(String fullName, String email, String password) {
        if (appUserRepository.findByEmailIgnoreCase(email).isPresent()) {
            return false;
        }
        AppUser user = new AppUser();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(password);
        appUserRepository.save(user);
        return true;
    }

    public boolean login(String email, String password, HttpSession session) {
        return appUserRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .filter(user -> user.getPassword().equals(password))
                .map(user -> {
                    session.setAttribute(USER_SESSION_KEY, user.getId());
                    return true;
                })
                .orElse(false);
    }

    public void logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
    }

    public AppUser getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute(USER_SESSION_KEY);
        if (userId instanceof Long id) {
            return appUserRepository.findById(id).orElse(null);
        }
        if (userId instanceof Integer id) {
            return appUserRepository.findById(id.longValue()).orElse(null);
        }
        return null;
    }
}
