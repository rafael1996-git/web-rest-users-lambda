package com.talentport.users.utils.Exceptions;

public class TradeException extends Exception{
    private static final long serialVersionUID = 1L;
    private String code;

    public String getCode() {
        return code;
    }

    public TradeException(String message, String code, Throwable t) {
        super(message,t);
        this.code = code;
    }

    public TradeException(String message, String code){
        super(message);
        this.code = code;
    }
}
