package org.study.green;

import java.net.URL;
import java.util.Objects;

/**
 * Hello world!
 */
public class App {
    public App() {

//        String file1 = Objects.requireNonNull().getFile();


        URL resource = getClass().getClassLoader().getResource("./");
//        URL resource = getClass().getClassLoader().getResource("bush.csv");
        System.out.println("resource = " + resource);



    }

    public static void main(String[] args) {
        System.out.println("Hello World!");

        new App();
//
//        // Get the class loader
//        ClassLoader classLoader = App.class.getClassLoader();
//
//        // Find the resource
//        URL resource = classLoader.getResource("bush.csv");
//        System.out.println("resource = " + resource.getFile());
//
//        if (resource == null) {
//            throw new IllegalArgumentException("File not found!");
//        } else {
//            // Do something with the resource...
//        }

    }
}
