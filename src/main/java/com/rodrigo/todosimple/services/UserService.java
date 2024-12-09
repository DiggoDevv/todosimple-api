package com.rodrigo.todosimple.services;

import com.rodrigo.todosimple.models.User;
import com.rodrigo.todosimple.repositories.TaskRepository;
import com.rodrigo.todosimple.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException(
                "Usuário não encontrado: Id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    @Transactional
    public User create(User obj) {
        obj.setId((null));
        obj = this.userRepository.save(obj);
        this.taskRepository.saveAll(obj.getTasks());
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newObj = findById(obj.getId());
        //reutilizando o código do findById
        newObj.setPassword(obj.getPassword());
        return this.userRepository.save(newObj);
        //vai atualizar somente a senha do usuario.
    }

    public void delete(Long id) {
        findById(id);
        //try catch para garantir que a entidade não está relacionada com outra entidade
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possivel excluir pois há entidades relacionadas");

        }
    }
}