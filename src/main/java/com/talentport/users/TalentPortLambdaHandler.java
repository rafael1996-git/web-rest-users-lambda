package com.talentport.users;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.trader.core.TraderCoreLambdaHandler;

public class TalentPortLambdaHandler extends TraderCoreLambdaHandler{
	
	private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
	 static {
	        try {
	            TraderCoreLambdaHandler.initHandler(TalentPortUsersApplication.class);
	        } catch (Exception e) {
	            System.out.println(new StringBuilder("Error al inicializar el Handler").append(e.toString()));
	        }
	    }
    
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		 handler.proxyStream(input, output, context);
		 output.close();
	}

}
