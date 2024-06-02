package org.study.green.stream.vo;

// City enum
public enum City {
    SEOUL("Seoul"),
    BUSAN("Busan"),
    INCHEON("Incheon"),
    GWANGJU("Gwangju"),
    DAEJEON("Daejeon"),
    ULSAN("Ulsan"),
    DAEGU("Daegu"),
    JEJU("Jeju"),
    GYEONGGI("Gyeonggi"),
    CHUNGCHEONG("Chungcheong"),
    JEOLLA("Jeolla"),
    GYEONGSANG("Gyeongsang");

    private final String name;

    City(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
