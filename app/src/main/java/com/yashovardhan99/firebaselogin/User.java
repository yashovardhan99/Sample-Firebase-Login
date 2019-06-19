package com.yashovardhan99.firebaselogin;

/**
 * Created by Yashovardhan99 on 19/6/19 as a part of Sample-Firebase-Login.
 */
public class User {
    private final String name, email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
