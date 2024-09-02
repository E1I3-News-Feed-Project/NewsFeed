package com.nbacm.newsfeed.domain.user.common.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

public class PasswordUtils {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
