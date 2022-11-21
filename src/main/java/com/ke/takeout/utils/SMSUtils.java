package com.ke.takeout.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.rmi.ServerException;

/**
 * 短信发送工具类
 */
@Component
public class SMSUtils {

	public static  String secretId ;
	public static  String secretKey;
	public static  String appID ;
	public static  String sign ;
	@Value("${TencentCloud.secretId}")
	public  void setSecretId(String secretId) {
		SMSUtils.secretId = secretId;
	}
	@Value("${TencentCloud.secretKey}")
	public  void setSecretKey(String secretKey) {
		SMSUtils.secretKey = secretKey;
	}
	@Value("${TencentCloud.appID}")
	public  void setAppID(String appID) {
		SMSUtils.appID = appID;
	}
	@Value("${TencentCloud.sign}")
	public  void setSign(String sign) {
		SMSUtils.sign = sign;
	}

/*
	public static void sendShortMessage(String signName, String templateCode, String phone, String code) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-beijing", "<your-access-key-id>", "<your-access-key-secret>");
		/** use STS Token
		 DefaultProfile profile = DefaultProfile.getProfile(
		 "<your-region-id>",           // The region ID
		 "<your-access-key-id>",       // The AccessKey ID of the RAM account
		 "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
		 "<your-sts-token>");          // STS Token

		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setPhoneNumbers("1368846****");//接收短信的手机号码
		request.setSignName("阿里云");//短信签名名称
		request.setTemplateCode("SMS_20933****");//短信模板CODE
		request.setTemplateParam("张三");//短信模板变量对应的实际值

		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println(new Gson().toJson(response));
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			System.out.println("ErrCode:" + e.getErrCode());
			System.out.println("ErrMsg:" + e.getErrMsg());
			System.out.println("RequestId:" + e.getRequestId());
		}
	}
*/

	/**
	 * 发送短信
	 * @param phoneNumbers
	 * @param param
	 */
//	public static void sendShortMessage(String templateCode,String phoneNumbers,String param){
//		// 设置超时时间-可自行调整
//		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//		// 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey，见《创建secretId和secretKey》小节
//
//		try {
//			/* 必要步骤：
//			 * 实例化一个认证对象，入参需要传入腾讯云账户密钥对 secretId 和 secretKey
//			 * 本示例采用从环境变量读取的方式，需要预先在环境变量中设置这两个值
//			 * 您也可以直接在代码中写入密钥对，但需谨防泄露，不要将代码复制、上传或者分享给他人
//			 * CAM 密钥查询：https://console.cloud.tencent.com/cam/capi
//			 */
//			Credential cred = new Credential(secretId, secretKey);
//			HttpProfile httpProfile = new HttpProfile();
//
//			/* SDK 默认使用 POST 方法。
//			 * 如需使用 GET 方法，可以在此处设置，但 GET 方法无法处理较大的请求 */
//			httpProfile.setReqMethod("POST");
//			/* SDK 有默认的超时时间，非必要请不要进行调整
//			 * 如有需要请在代码中查阅以获取最新的默认值 */
//			httpProfile.setConnTimeout(60);
//			/* SDK 会自动指定域名，通常无需指定域名，但访问金融区的服务时必须手动指定域名
//			 * 例如 SMS 的上海金融区域名为 sms.ap-shanghai-fsi.tencentcloudapi.com */
//			httpProfile.setEndpoint("sms.tencentcloudapi.com");
//			/* 非必要步骤:
//			 * 实例化一个客户端配置对象，可以指定超时时间等配置 */
//			ClientProfile clientProfile = new ClientProfile();
//			/* SDK 默认用 TC3-HMAC-SHA256 进行签名
//			 * 非必要请不要修改该字段 */
//			clientProfile.setSignMethod("HmacSHA256");
//			clientProfile.setHttpProfile(httpProfile);
//			/* 实例化 SMS 的 client 对象
//			 * 第二个参数是地域信息，可以直接填写字符串 ap-guangzhou，或者引用预设的常量 */
//			SmsClient client = new SmsClient(cred, "ap-chongqing",clientProfile);
//			/* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
//			 * 您可以直接查询 SDK 源码确定接口有哪些属性可以设置
//			 * 属性可能是基本类型，也可能引用了另一个数据结构
//			 * 推荐使用 IDE 进行开发，可以方便地跳转查阅各个接口和数据结构的文档说明 */
//			SendSmsRequest req = new SendSmsRequest();
//			/* 填充请求参数，这里 request 对象的成员变量即对应接口的入参
//			 * 您可以通过官网接口文档或跳转到 request 对象的定义处查看请求参数的定义
//			 * 基本类型的设置:
//			 * 帮助链接：
//			 * 短信控制台：https://console.cloud.tencent.com/smsv2
//			 * sms helper：https://cloud.tencent.com/document/product/382/3773 */
//			/* 短信应用 ID: 在 [短信控制台] 添加应用后生成的实际 SDKAppID，例如1400006666 */
//			String appid = appID;
//			req.setSmsSdkAppid(appid);
//			/* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，可登录 [短信控制台] 查看签名信息 */
//			String sign1 = sign;
//			req.setSign(sign1);
//			/* 模板 ID: 必须填写已审核通过的模板 ID，可登录 [短信控制台] 查看模板 ID */
//			String templateID = templateCode;
//			req.setTemplateID(templateID);
//			/* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
//			 * 例如+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
//			String[] phoneNumber = {"+86"+phoneNumbers};
//			req.setPhoneNumberSet(phoneNumber);
//			/* 模板参数: 若无模板参数，则设置为空*/
//			String[] templateParams = {param};
//			req.setTemplateParamSet(templateParams);
//			/* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
//			 * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
//			SendSmsResponse res = client.SendSms(req);
//			// 输出 JSON 格式的字符串回包
//			System.out.println(SendSmsResponse.toJsonString(res));
//			// 可以取出单个值，您可以通过官网接口文档或跳转到 response 对象的定义处查看返回字段的定义
//			System.out.println(res.getRequestId());
//		} catch (TencentCloudSDKException e) {
//			e.printStackTrace();
//		}


		// 初始化ascClient需要的几个参数
		//final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
		//final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）
		//// 替换成你的AK
		//final String accessKeyId = "LTAIak3CfAehK7cE";// 你的accessKeyId,参考本文档步骤2
		//final String accessKeySecret = "zsykwhTIFa48f8fFdU06GOKjHWHel4";// 你的accessKeySecret，参考本文档步骤2
		//// 初始化ascClient,暂时不支持多region（请勿修改）
		//IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		//DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		//IAcsClient acsClient = new DefaultAcsClient(profile);
		//// 组装请求对象
		//SendSmsRequest request = new SendSmsRequest();
		//// 使用post提交
		//request.setMethod(MethodType.POST);
		//// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
		//request.setPhoneNumbers(phoneNumbers);
		//// 必填:短信签名-可在短信控制台中找到
		//request.setSignName("传智健康");
		//// 必填:短信模板-可在短信控制台中找到
		//request.setTemplateCode(templateCode);
		//// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		//// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
		//request.setTemplateParam("{\"code\":\""+param+"\"}");
		//// 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
		//// request.setSmsUpExtendCode("90997");
		//// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		//// request.setOutId("yourOutId");
		//// 请求失败这里会抛ClientException异常
		//SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		//if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
		//	// 请求成功
		//	System.out.println("请求成功");
		//}
	//}
}
