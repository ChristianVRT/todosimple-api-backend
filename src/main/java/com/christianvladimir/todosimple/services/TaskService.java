package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.Task;
import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.repositories.TaskRepository;
import com.christianvladimir.todosimple.services.exceptions.DataBindingViolationException;
import com.christianvladimir.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;


    public List<Task> findAll (){
        List<Task> tasks = this.taskRepository.findAll();
        return tasks;
    }

    public Task findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
                "Task não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()
        ));
    }

    public List<Task> findAllByUserId (Long userId){
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    @Transactional
    public Task Create(Task task) {
        User user = this.userService.findById(task.getUser().getId());
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
}
