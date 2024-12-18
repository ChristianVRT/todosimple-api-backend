package com.christianvladimir.todosimple.repositories;

import com.christianvladimir.todosimple.models.Task;
import com.christianvladimir.todosimple.models.projection.TaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<TaskProjection> findByUser_Id(Long id);

    //@Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
    //List<Task> findByUser_iD(@Param("id") Long id);

    //@Query(value = "SELECT * FROM task t WHERE t.user_id = :id", nativeQuery = true)
    //List<Task> findByUser_Id(@Param("id") Long id);

}
