package com.rodrigo.todosimple.services;

import com.rodrigo.todosimple.models.Task;
import com.rodrigo.todosimple.models.User;
import com.rodrigo.todosimple.models.enums.ProfileEnum;
import com.rodrigo.todosimple.repositories.TaskRepository;
import com.rodrigo.todosimple.security.UserSpringSecurity;
import com.rodrigo.todosimple.services.exceptions.AuthorizationException;
import com.rodrigo.todosimple.services.exceptions.DataBindingViolationException;
import com.rodrigo.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Tarefa não encontrada: Id: " + id + ", Tipo: " + Task.class.getName()));
            UserSpringSecurity userSpringSecurity = UserService.authenticated();
            if(Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN)
                    && !userHasTask(userSpringSecurity, task))
                throw new AuthorizationException("Acesso negado");
        return task;
    }

    //buscar as task pelo id usuario
    public List<Task> findAllByUser() {
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado");

        List<Task> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado");

        User user = this.userService.findById(userSpringSecurity.getId());
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

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
