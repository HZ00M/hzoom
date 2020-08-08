package com.example.core;

import org.junit.Test;

public class PermutationTest {
    @Test
    public void test(){
        char[] arr = new char[]{'a', 'b', 'c'};
        Permutation(arr);
    }
    public static void Permutation(char[] c){

        for(int i=1;i<(Math.pow(2,c.length));i++){
            for(int j=0;j<c.length;j++){
                if((i&(1<<j))>0){//判断每一位是否有
                    System.out.print(c[j]);
                }
            }
            System.out.println();
        }
    }
}
