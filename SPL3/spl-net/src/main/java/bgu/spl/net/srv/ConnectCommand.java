package bgu.spl.net.srv;

import bgu.spl.net.impl.rci.Command;

import java.io.Serializable;

public class ConnectCommand <T>implements Command<T> {
    @Override
    public Serializable execute(T arg) {
        return null;
    }
}
