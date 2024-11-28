package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.Task;
import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.models.enums.ProfileEnum;
import com.christianvladimir.todosimple.models.projection.TaskProjection;
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
        UserSpringSecurity userSpringSecurity = userAuthenticated();
        return task;
    }

    public List<TaskProjection> findAllByUser (){
        UserSpringSecurity userSpringSecurity = userAuthenticated();
        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task task) {
        UserSpringSecurity userSpringSecurity = userAuthenticated();
        User user = this.userService.findById(userSpringSecurity.getId());
        task.setId(null);
        task.setUser(user);
        task = this.taskRepository.save(task);
        return task;
    }

    @Transactional
    public void Update(Task task) {
        UserSpringSecurity userSpringSecurity = userAuthenticated();
        Task newTask = this.findById(task.getId());
        if (!userSpringSecurity.getId().equals(newTask.getUser().getId()) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN))
            throw new AuthorizationException("Você não pode editar uma atividade que não pertence a você.");
        try {
            newTask.setDescription(task.getDescription());
            this.taskRepository.save(newTask);
        } catch (DataIntegrityViolationException e) {
            throw new DataBindingViolationException("Não é possível editar pois há um ou mais entidades Relacionadas!");
        }
    }

    @Transactional
    public void delete(Long id) {
        UserSpringSecurity userSpringSecurity = userAuthenticated();
        Task task = findById(id);
        if (!userSpringSecurity.getId().equals(task.getUser().getId()) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN))
            throw new AuthorizationException("Essa atividade não pertence a você.");
        try {
            this.taskRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBindingViolationException("Não é possível excluir pois há um ou mais entidades Relacionadas!");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }

    private UserSpringSecurity userAuthenticated(){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso Negado.");
        return userSpringSecurity;
    }
}
