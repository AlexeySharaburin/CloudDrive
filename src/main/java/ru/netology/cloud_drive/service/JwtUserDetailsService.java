package ru.netology.cloud_drive.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloud_drive.model.MyUserPrincipal;
import ru.netology.cloud_drive.model.UserData;
import ru.netology.cloud_drive.repository.UserDataRepository;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserDataRepository userDataRepository;

    public JwtUserDetailsService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserData userData = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
        if (userData == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MyUserPrincipal(userData);
    }
}


