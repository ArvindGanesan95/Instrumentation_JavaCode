package com.instrumentation.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sample1 {

    private static Logger logger = LoggerFactory.getLogger(Sample1.class);

    public static void main(String[] args) {

        if (args.length < 5) {
            logger.error("There needs to 5 input arguments");
            System.exit(-1);
        }

        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        int d = Integer.parseInt(args[3]);
        int e = Integer.parseInt(args[4]);

        for (int i = 0; i < a; i++) {

            b = b - 1;
            add(a, b);
        }

        int m = 0;

        while (m < e) {
            m++;
            add(a, b);
        }
    }

    public static int add(int a, int b) {
        return a + b;
    }

}
