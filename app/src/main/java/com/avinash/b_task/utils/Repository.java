package com.avinash.b_task.utils;

import java.util.List;

public interface Repository<T> {

    void persist(T entity);


    void update(T entity);



    T findOne();


    void deleteAll();
}
