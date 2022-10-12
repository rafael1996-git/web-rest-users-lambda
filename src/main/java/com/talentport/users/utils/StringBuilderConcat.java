package com.talentport.users.utils;
public class StringBuilderConcat {
    public static String concat(Object... values) {
        StringBuilder sb = new StringBuilder("");
        for (Object value: values) {
            if(value != null) {
                sb.append(value.toString());
            } else {
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
