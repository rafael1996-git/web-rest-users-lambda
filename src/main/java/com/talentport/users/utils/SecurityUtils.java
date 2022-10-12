package com.talentport.users.utils;


import com.talentport.users.security.Credentials;
import com.talentport.users.utils.Exceptions.TradeException;
import com.trader.core.domain.response.Response;
import com.trader.core.models.ResponseModel;
import com.trader.core.security.TraderEncriptorKey;
import com.trader.core.utils.Base64Utils;
import com.trader.core.utils.GsonParserUtils;
import com.trader.core.utils.StringGZipperUtils;

public class SecurityUtils {
    static {
        try {
            if(Credentials.ENCRYPTION_KEYS == null) {
                throw new TradeException(
                        Constantes.FAILED_GET_KEYS,
                        Constantes.FAILED_GET_KEYS_CONFIG
                );
            }

            TraderEncriptorKey.initZeusEncriptorKey(
                    Credentials.ENCRYPTION_KEYS.getTalentportMainKey(),
                    Credentials.ENCRYPTION_KEYS.getPwdKey()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(){

    }

    public static Object decrypt(String data, Class<?> objectClass) {
        String jsonString = null;
        try {
            jsonString = TraderEncriptorKey.decode(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonString == null)
            return null;
        else
            return GsonParserUtils.getGson().fromJson(jsonString, objectClass);
    }

    public static Response parseResponse(ResponseModel responseModel, boolean gzip) {
        try {
            String jsonString = GsonParserUtils.getGson().toJson(responseModel);
            return gzip
                    ? new Response(Base64Utils.StringToBase64(new String(StringGZipperUtils.gzip(jsonString))))
                    : new Response(TraderEncriptorKey.encode(jsonString));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptText(String txt){
        try {
            return TraderEncriptorKey.decode(txt);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
