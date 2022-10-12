package com.talentport.users.utils;

import com.trader.core.utils.EnvironmentData;

public class Constantes {
    public static final String FAILED_GET_DB_CONFIG = "Failed to get DB configuration.";
    public static final String CODIGO_FAILED_GET_DB_CONFIG = "-10";

    public static final String FAILED_GET_KEYS = "Failed to get Main and pass TALENTPORT Keys.";
    public static final String FAILED_GET_KEYS_CONFIG = "-11";

    private static String initializeVariable(String value) {
        String result;
        try {
            result = EnvironmentData.getPropertyValue(value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = null;
        }

        return result;
    }
}
