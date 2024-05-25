package org.study.green.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser {
    private final Object[] choiceArray;

    public Chooser(Collection collection) {
        choiceArray = collection.toArray();
    }

    public Object choose(){
        Random current = ThreadLocalRandom.current();
        System.out.println("current = " + current);
        return choiceArray[current.nextInt(choiceArray.length)];

    }

    public static void main(String[] args) {
        new Chooser(new ArrayList<Object>()).choose();

    }


}
