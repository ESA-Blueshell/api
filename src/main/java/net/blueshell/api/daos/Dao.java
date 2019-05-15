package net.blueshell.api.daos;

import net.blueshell.api.model.Picture;

import java.util.List;

public interface Dao<T> {

    List<T> list();

    T getById(long id);

    // .save() generates an ID, .persists() doesn't
    T create(T t);

    void update(T t);

    void delete(long id);

}
