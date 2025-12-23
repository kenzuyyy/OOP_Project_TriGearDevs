package util;

public class TestHash {
    public static void main(String[] args) {
        String password = "admin123";
        System.out.println(SecurityUtil.hashPassword(password));
    }
}