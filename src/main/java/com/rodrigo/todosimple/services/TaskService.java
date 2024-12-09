package com.rodrigo.todosimple.services;

import com.rodrigo.todosimple.models.Task;
import com.rodrigo.todosimple.models.User;
import com.rodrigo.todosimple.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
                "Tarefa não encontrada: Id: " + id + ", Tipo: " + Task.class.getName()
        ));
    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id){
        //aqui não precisa necessariamente do try catch,
        // coloquei pois se no futuro task estar associado a alguma tabela
        try{
            this.taskRepository.deleteById(id);
        } catch (Exception e){
            throw new RuntimeException("Não é possivel excluir pois há entidades relacionadas");
        }
    }
}
