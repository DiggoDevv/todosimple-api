package com.rodrigo.todosimple.services;

import com.rodrigo.todosimple.models.Task;
import com.rodrigo.todosimple.models.User;
import com.rodrigo.todosimple.repositories.TaskRepository;
import com.rodrigo.todosimple.services.exceptions.DataBindingViolationException;
import com.rodrigo.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada: Id: " + id + ", Tipo: " + Task.class.getName()
        ));
    }

    //buscar as task pelo id usuario
    public List<Task> findAllByUserId(Long userId) {
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
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
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas");
        }
    }
}
