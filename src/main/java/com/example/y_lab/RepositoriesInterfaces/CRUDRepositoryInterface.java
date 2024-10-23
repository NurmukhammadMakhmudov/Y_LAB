package com.example.y_lab.RepositoriesInterfaces;

import java.util.List;

public interface CRUDRepositoryInterface<T, ID> {
    void save(T entity);
    void update(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();


}
