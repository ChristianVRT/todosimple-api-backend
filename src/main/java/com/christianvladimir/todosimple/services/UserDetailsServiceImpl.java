package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.repositories.UserRepository;
import com.christianvladimir.todosimple.security.UserSpringSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UsernameNotFoundException("Usuário não encontrado! "+username);
        User foundUser = user.get();
        return new UserSpringSecurity(foundUser.getId(), foundUser.getUsername(), foundUser.getPassword(), foundUser.getProfiles());
    }
}
