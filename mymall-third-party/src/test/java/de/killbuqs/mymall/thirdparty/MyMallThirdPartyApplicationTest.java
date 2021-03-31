package de.killbuqs.mymall.thirdparty;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyMallThirdPartyApplicationTest {

	
	// 通过阿里云OSS JAVA SDK 上传文件
	@Test
	public void testUploadJavaSDK() throws FileNotFoundException {
		 String endpoint = "http://oss-cn-hangzhou.aliiyun.com";
		 
		 //在阿里云/amazon中添加新上传访问图片专属子账户，生成accessKey， 并添加相应的权限

		 String accessKeyId = "<key>";
		 String accessKeySecret = "<value>";
		 
		 
		 OSS oss = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
		 
		 InputStream stream = new FileInputStream("<file_name>");
		 oss.putObject("<bucket_name>", "<object_name>", stream);
		 
		 oss.shutdown();
		 
		 System.out.println("上传完成");
	}
	
	@Autowired
	private OSSClient ossClient;
	
	// 通过阿里云OSS JAVA SDK 上传文件
	@Test
	public void testUploadAliyun() throws FileNotFoundException {
		
		InputStream stream = new FileInputStream("<file_name>");
		ossClient.putObject("<bucket_name>", "<object_name>", stream);
		
		ossClient.shutdown();
		
		System.out.println("上传完成");
	}
}
