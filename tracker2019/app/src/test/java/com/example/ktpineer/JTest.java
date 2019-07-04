package com.example.ktpineer;

import org.junit.Test;

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @desc
 * @since 5/9/19
 */
public class JTest {
    @Test
    public void test1() {
        String[] split = " a aa  aaa   aaaa ".split("\\s+");
        System.out.println(split.length);
        for (String s : split) {
            System.out.println(s.length());

        }
    }
}
