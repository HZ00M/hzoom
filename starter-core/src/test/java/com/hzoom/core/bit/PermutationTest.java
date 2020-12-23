package com.hzoom.core.bit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class PermutationTest {
    @Test
    public void test() {
        char[] arr = new char[]{'1', '2', '3'};
        Permutation(arr);
    }

    public static void Permutation(char[] c) {

        for (int i = 1; i < (Math.pow(2, c.length)); i++) {
            for (int j = 0; j < c.length; j++) {

                if ((i & (1 << j)) > 0) {//判断每一位是否有
                    System.out.print(c[j]);
                } else {
                    System.out.print(0);
                }
            }
            System.out.println();
        }

    }


    /**
     * 位图
     * 判断数字是否存在、判断数字是否重复的问题，位图法是一种非常高效的方法。
     * i&(1<<j)  1<<j 分别为 001,010,100 然后与按位&可以判断该位是否存在
     * 判断数字是否存在、判断数字是否重复的问题，位图法是一种非常高效的方法。
     */
    @Test
    public void test2() {
        Random random = new Random();

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            int randomResult = random.nextInt(100000);
            list.add(randomResult);
        }
        System.out.println("产生的随机数有");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        BitSet bitSet = new BitSet(100000);
        for (int i = 0; i < 100000; i++) {
            bitSet.set(list.get(i));
        }
        System.out.println("0~1亿不在上述随机数中有" + bitSet.cardinality());
        for (int i = 0; i < 100000; i++) {
            if (!bitSet.get(i)) {
                System.out.println(i);
            }
        }
        System.out.println("总共：" + bitSet.cardinality());
        
    }
}