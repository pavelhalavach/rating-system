package com.ratingsystem.security;

import com.ratingsystem.entity.User;
import com.ratingsystem.exception.UserNotEnabledException;
import com.ratingsystem.exception.UserNotFoundException;
import com.ratingsystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!user.isEnabled()) {
            throw new UserNotEnabledException("Please confirm your email before logging in");
        }
        return new CustomUserDetails(user);
    }
}
