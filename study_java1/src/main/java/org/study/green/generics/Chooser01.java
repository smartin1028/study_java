package org.study.green.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser01<T> {
    private final List<T> choiceArray;

    public Chooser01(Collection<T> collection) {
//        choiceArray = (T[]) collection.toArray();
        choiceArray = new ArrayList<>(collection);
    }

    public Object choose(){
        Random current = ThreadLocalRandom.current();
        System.out.println("current = " + current);
        return choiceArray.get(current.nextInt(choiceArray.size()));

    }

    public static void main(String[] args) {
        new Chooser01(new ArrayList<Object>()).choose();

    }


}
