package org.izv.teams.view.operations;

public interface BeforeCrud<T> {
    void doIt(T object);
}