package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.models.enums.ProfileEnum;
import com.christianvladimir.todosimple.repositories.UserRepository;
import com.christianvladimir.todosimple.services.exceptions.DataBindingViolationException;
import com.christianvladimir.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(()-> new ObjectNotFoundException(
                "Usuário não encontrado! Id: " + id + " Tipo: " + User.class.getName()
        ));
    }

    public User findByUsername(String username) {
        Optional<User> user = this.userRepository.findByUsername(username);
        return user.orElseThrow(()-> new ObjectNotFoundException(
                "Usuário não encontrado! Username: " + username + " Tipo: " + User.class.getName()
        ));
    }

    public List<User> findAll() {
        List<User> users = this.userRepository.findAll();
        if (users.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum usuário encontrado!");
        }
        return users;
    }

    @Transactional
    public User create(User user) {
        user.setId(null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        user = this.userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(User user) {
        User newUser = findById(user.getId());
        newUser.setPassword(user.getPassword());
        newUser.setUsername(bCryptPasswordEncoder.encode(user.getPassword()));
        return this.userRepository.save(newUser);
    }


    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBindingViolationException("Não é possível excluir pois há uma ou mais Tasks Relacionadas!");
        }
    }
}
