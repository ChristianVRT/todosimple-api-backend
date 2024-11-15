package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.repositories.UserRepository;
import com.christianvladimir.todosimple.services.exceptions.DataBindingViolationException;
import com.christianvladimir.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(()-> new ObjectNotFoundException(
                "Usuário não encontrado! Id: " + id + " Tipo: " + User.class.getName()
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
        user = this.userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(User user) {
        User newUser = findById(user.getId());
        newUser.setPassword(user.getPassword());
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
