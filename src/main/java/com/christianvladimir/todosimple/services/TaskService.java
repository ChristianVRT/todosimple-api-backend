package com.christianvladimir.todosimple.services;

import com.christianvladimir.todosimple.models.Task;
import com.christianvladimir.todosimple.models.User;
import com.christianvladimir.todosimple.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
                "Task não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()
        ));
    }

    @Transactional
    public Task Create(Task obj) {
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task Update(Task obj) {
        Task newObj = this.findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir pois há um ou mais entidades Relacionadas!");
        }
    }
}
