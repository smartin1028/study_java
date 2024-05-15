package org.study.green.sys;

public class SystemInfo {

    public SystemInfo() {
        MemoryTest();
        StringIdentityHashCodeCheck();
    }

    private void StringIdentityHashCodeCheck() {
        int identityHashCode01 = getIdentityHashCode("1");
        System.out.println("identityHashCode01 = " + identityHashCode01);
        int identityHashCode02 = getIdentityHashCode("1");
        System.out.println("identityHashCode02 = " + identityHashCode02);
        System.out.println("(identityHashCode01 == identityHashCode02) = " + (identityHashCode01 == identityHashCode02));

        int identityHashCode03 = getIdentityHashCode(new String("1"));
        System.out.println("identityHashCode03 = " + identityHashCode03);
        System.out.println("(identityHashCode01 == identityHashCode03) = " + (identityHashCode01 == identityHashCode03));
        int identityHashCode04 = getIdentityHashCode(new String("1"));
        System.out.println("identityHashCode04 = " + identityHashCode04);
        System.out.println("(identityHashCode01 == identityHashCode03) = " + (identityHashCode03 == identityHashCode04));

        System.out.println(new String("1").equals(new String("1")));
    }


    /**
     * identityHashCode
     */
    private void MemoryTest() {
        /**
         * 자바에서 '==' 연산자는 두 변수의 메모리 주소를 비교합니다.
         * 즉, 두 변수가 같은 객체를 가리키고 있는지를 확인합니다.
         * 만약 두 객체의 내용을 비교하고 싶다면, 'equals()' 메소드를 사용해야 합니다.
         *
         * 현재 객체의 메모리 주소를 알려면 'System.identityHashCode()' 메소드를 사용하면 됩니다.
         * 이 메소드는 객체의 원래 해시 코드를 반환하며, 이 해시 코드는 객체의 메모리 주소를 나타내는 데 사용할 수 있습니다.
         */

        int x = 1;
        System.out.println("args = " + getIdentityHashCode(x));
        int y = 1;
        System.out.println("args = " + getIdentityHashCode(y));

        System.out.println(x == y);
    }

    private static int getIdentityHashCode(Object x) {
        return System.identityHashCode(x);
    }

    public static void main(String[] args) {
        new SystemInfo();

    }
}
