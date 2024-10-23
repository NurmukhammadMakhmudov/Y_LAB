package com.example.y_lab.RepositoriesInterfaces;

import com.example.y_lab.models.User;

import java.util.List;

public interface UserRepositoryInterface extends CRUDRepositoryInterface<User,Long>{

    public User findByEmail(String email);

}
