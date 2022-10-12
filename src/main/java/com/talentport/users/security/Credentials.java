package com.talentport.users.security;

import com.trader.core.utils.EnvironmentData;
import com.trader.core.utils.GsonParserUtils;
import com.trader.core.utils.SecretManagerAWSUtils;

public class Credentials {
    public static final ConfigDB DB_CONFIG = initCredentials("OraDBKey", ConfigDB.class);
    public static final CognitosUsers COGNITO_KEYS = initCredentialsCognito("CognitKey", CognitosUsers.class);
    public static final TraderKeys ENCRYPTION_KEYS = initCredentialsEncryption("EncryptionKey", TraderKeys.class);

    private static <TType> TType initCredentialsEncryption(String keyName, Class<TType> clasType)  {
        try {
           // String encryption = SecretManagerAWSUtils.getParameter(EnvironmentData.getPropertyValue(keyName));
            String encryption = SecretManagerAWSUtils.getParameter("com/talentport/security/credenciales/encryption");
            return GsonParserUtils.getGson().fromJson(encryption, clasType);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <TType> TType initCredentials(String keyName, Class<TType> clasType)  {
        try {
            String encryption = SecretManagerAWSUtils.getParameter("com/talentport/db/oracle/USRTALENTPORT");
            return GsonParserUtils.getGson().fromJson(encryption, clasType);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static <TType> TType initCredentialsCognito(String keyName, Class<TType> clasType)  {
        try {
            String encryption = SecretManagerAWSUtils.getParameter("com/talentport/security/cognito/cognitoUsers");
            return GsonParserUtils.getGson().fromJson(encryption, clasType);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
