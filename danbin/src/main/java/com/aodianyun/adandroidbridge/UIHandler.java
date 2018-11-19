package com.aodianyun.adandroidbridge;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by cuiboqiang on 01/02/2018.
 */

public class UIHandler<T> extends Handler {

    protected WeakReference<T> ref;

    public UIHandler(T cls){
        ref = new WeakReference<T>(cls);
    }

    public T getRef(){
        return ref != null ? ref.get() : null;
    }
}