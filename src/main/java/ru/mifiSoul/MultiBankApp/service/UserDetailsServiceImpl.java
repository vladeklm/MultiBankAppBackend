package ru.mifiSoul.MultiBankApp.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mifiSoul.MultiBankApp.database.entity.UserEntity;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity user;
        if (login.contains("@")) {
            user = userRepository.findByEmail(login)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + login));
        } else if (login.matches("\\+?\\d+")) {
            user = userRepository.findByPhone(login)
                    .orElseThrow(()-> new UsernameNotFoundException("User not found with phone: " + login));
        } else {
            user = userRepository.findByUsername(login)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + login));
        }
        return UserDetailsImpl.build(user);
    }
}
