package com.talentport.users.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.talentport.users.dto.ProductImg;

@Service
public class ConfigureS3Implement {
    private static final Logger log = LoggerFactory.getLogger(ConfigureS3Implement.class);
  private static String bucketName = "dev.talentport.devops/images";
	private AmazonS3 s3 = null;

	public ProductImg imgToBase64(String uploadFiles,String id) throws IOException {
		ProductImg getImg=new ProductImg();
		
		try {
			initializeAmazons3Client();
			//String bucketName =EnvironmentData.getPropertyValue("BucketName");
			s3.putObject(bucketName, id+".txt", uploadFiles);
			
			S3Object contenido = s3.getObject(bucketName, id+".txt");
			InputStream content = contenido.getObjectContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(content, writer, "UTF-8");       
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		 getImg.setImg("");
			return getImg;
	}
	public ProductImg detailImg(String id) throws IOException {
		ProductImg getImg=new ProductImg();
		
		try {
			initializeAmazons3Client();
			//String bucketName =EnvironmentData.getPropertyValue("BucketName");
			S3Object contenido = s3.getObject(bucketName, id+".txt");
			InputStream content = contenido.getObjectContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(content, writer, "UTF-8"); 
			 getImg.setImg(writer.toString());
		} catch (Exception e1) {
			log.error(e1.getMessage());

		}
			return getImg;
	}
	private void initializeAmazons3Client() throws Exception {

		if (s3 == null) {
			s3 = AmazonS3ClientBuilder.standard().build();
		}
	}

   
	
	
	   
}
