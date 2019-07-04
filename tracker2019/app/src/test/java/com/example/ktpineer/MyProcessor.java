package com.example.ktpineer;

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @desc
 * @since 5/8/19
 */
public class MyProcessor {
    MyListener listener;

    public void setListener(MyListener listener) {
        this.listener = listener;
    }

    public void resolve(String input) {
        if (this.listener != null) {
            this.listener.my(input);
        }
    }
}
