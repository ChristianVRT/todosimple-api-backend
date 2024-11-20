package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.Task;
import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.models.enums.ProfileEnum;
import com.christianvladimir.todosimple.repositories.TaskRepository;
import com.christianvladimir.todosimple.security.UserSpringSecurity;
import com.christianvladimir.todosimple.services.exceptions.AuthorizationException;
import com.christianvladimir.todosimple.services.exceptions.DataBindingViolationException;
import com.christianvladimir.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;


    public List<Task> findAll (){
        List<Task> tasks = taskRepository.findAll();
        return tasks;
    }

    public Task findById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Task não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso Negado.");
        return task;
    }

    public List<Task> findAllByUser (){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado.");
        List<Task> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task task) {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado.");
        User user = this.userService.findById(userSpringSecurity.getId());
        task.setId(null);
        task.setUser(user);
        task = this.taskRepository.save(task);
        return task;
    }

    @Transactional
    public Task Update(Task task) {
        Task newTask = this.findById(task.getId());
        newTask.setDescription(task.getDescription());
        return this.taskRepository.save(newTask);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBindingViolationException("Não é possível excluir pois há um ou mais entidades Relacionadas!");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
