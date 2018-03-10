package com.smona.app.evaluationcar.data.model;

/**
 * Created by Moth on 2017/3/23.
 */

public class ResBaseModel<T> {
    public boolean success;
    public String message;
    public T object;

    public String toString() {
        return "sucess=" + success + ";message=" + message + ";object=" + object;
    }
}
