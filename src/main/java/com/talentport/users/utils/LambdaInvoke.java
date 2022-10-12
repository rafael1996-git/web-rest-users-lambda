package com.talentport.users.utils;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

public enum LambdaInvoke {

	INSTANCE;

	LambdaClient awsLambda;

	LambdaInvoke() {
		awsLambda = LambdaClient.builder()
				.build();
	}

	private InvokeResponse invoke(String functionName, String strPayload, InvocationType type) {
		return awsLambda.invoke(InvokeRequest.builder()
				.functionName(functionName)
				.invocationType(type)
				.payload(SdkBytes.fromUtf8String(strPayload))
				.build());

	}

	public String invokeRawResult(String functionName, String strPayload) {
		final InvokeResponse result = invoke(functionName, strPayload, InvocationType.REQUEST_RESPONSE);
		return result.payload().asUtf8String();
	}

	public InvokeResponse invoke(String functionName, String strPayload) {
		return invoke(functionName, strPayload, InvocationType.REQUEST_RESPONSE);
	}

	public String invokeAsyncRawResult(String functionName, String strPayload) {
		final InvokeResponse result = invoke(functionName, strPayload, InvocationType.EVENT);
		return result.payload().asUtf8String();
	}

	private InvokeResponse invokeAsync(String functionName, String strPayload) {
		return invoke(functionName, strPayload, InvocationType.EVENT);

	}
}
