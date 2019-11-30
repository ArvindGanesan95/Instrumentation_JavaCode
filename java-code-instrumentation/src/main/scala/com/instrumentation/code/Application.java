package com.instrumentation.code;

public class Application {

    private int valA;
    private int valB;

    public static void main(String[] args) {

        int a = 5;
        int b = 6;

        for (int i = 0; i < a; i++) {

            a = a + 1;
            b = b - 1;
            add(a, b);
        }
    }

    public static int add(int a, int b) {
        return a + b;
    }

}
