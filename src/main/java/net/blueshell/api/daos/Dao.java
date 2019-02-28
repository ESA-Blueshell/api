package net.blueshell.api.daos;

import net.blueshell.api.model.Picture;

import java.util.List;

public interface Dao<T> {

    public List<T> list();

    public T getById(int id);

    // .save() generates an ID, .persists() doesn't
    public T create(T t);

    public void update(T t);

    public void delete(int id);

}
