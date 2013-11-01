package com.weixin.webservice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.weixin.bean.Waimai;
import com.weixin.bean.WeixinInfo;
import com.weixin.service.MessageService;
import com.weixin.service.impl.MessageServiceImpl;
import com.weixin.util.LoggerHandle;

/**
 * weixin消息处理
 * @author lyh
 * @version
 */

@Path("")
public class MessageManager {

	public static final Map<String, Long> unbindMap = new HashMap<String, Long>();
	
	private static final Map<String,Integer> photoMap = new HashMap<String, Integer>();
	
	private static final Map schedualMap = new HashMap();
	
	public static Boolean isThreadRun = false;
	
	private final String[] strs = new String[] { "系统没有收录该姓名，检查输入是否正确!", "乖，别闹了，告诉大叔你的真实名字！", "亲，不支持小号哦，请输入真实姓名！",
			"没有该姓名，详情找**@gmail.com" };

	private static final String waimaiInfo = "[1]周黑鸭 15068753635  13018987931\r\n[2]吉祥混沌 0571-87010869\r\n[3]好运来 15869105390\r\n[4]老汤面疙瘩 18806810217\r\n"
			+ "[5]品味坊  15168402450\r\n[6]瓦罐煨汤  15925666351\r\n[7]"
			+ "--------------\r\n回复相应序号，获取详情菜单\r\n";

	@Context
	private HttpServletRequest request;

	/**
	 * 微信验证，首次验证使用，详情参考weixin公共平台api
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String validate(@QueryParam("signature")
	String signature, @QueryParam("timestamp")
	String timestamp, @QueryParam("nonce")
	String nonce, @QueryParam("echostr")
	String echostr) {
		LoggerHandle.info("验证信息：" + signature + "---" + timestamp + "---" + nonce + "---" + echostr);
		List<String> list = new ArrayList<String>();
		list.add("weixininfo");
		list.add(timestamp);
		list.add(nonce);

		Collections.sort(list);
		StringBuffer str = new StringBuffer("");
		for (String s : list) {
			str.append(s);
		}
		try {
			String sha1 = sha1(str.toString());
			if (sha1.equals(signature)) {
				LoggerHandle.info("验证成功");
				return echostr;
			} else {
				LoggerHandle.info("验证失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return echostr;
	}

	/**
	 * 接入成功后，处理用户请求
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public String getUserInfo() {
		MessageService msgService = new MessageServiceImpl();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document d = null;
		try {
			db = dbf.newDocumentBuilder();
			d = db.parse(request.getInputStream());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 微信用户的用户名
		String from = d.getElementsByTagName("FromUserName").item(0).getFirstChild().getNodeValue();
		String msgType = d.getElementsByTagName("MsgType").item(0).getFirstChild().getNodeValue();
		String content = "";
		String text = "";
		if ("event".equals(msgType)) {
			LoggerHandle.info("用户关注");
			content = d.getElementsByTagName("Event").item(0).getFirstChild().getNodeValue();
			if ("subscribe".equals(content)) {
				text = "关注成功！\r\n请输入口令：您的姓名+空格+手机尾号后4位\r\n 比如：张三 1234";
			}
			return responseXml(from, text);
		} else {
			try {
				content = d.getElementsByTagName("Content").item(0).getFirstChild().getNodeValue();
			} catch (Exception e) {
				content = "";
			}
			LoggerHandle.info("发送消息：" + content);

			if ("解绑".equals(content.trim())) {
				unbindMap.put(from, new Date().getTime());
				text = "请输入验证姓名+手机后四位";
				return responseXml(from, text);
			}

			if (!msgService.isWhiteUserName(from) || (msgService.isWhiteUserName(from) && unbindMap.containsKey(from))) {
				Integer status = msgService.updateUserInfo(content, from, unbindMap.containsKey(from));

				if (status == 1) {
					text = "验证成功，来输个同学的姓名试试！”";
				} else if (status == 2) {
					text = "该姓名已经被绑定，需要修改请联系****@gmail.com";
				} else if (status < 0) {
					int s = Math.abs(status);
					int diffTimes = 3 - s;
					if (diffTimes == 0) {
						text = "还是不对，我猜你是新来的同学吧，请发邮件给我： ****@gmail.com";
					} else {
						text = "哎呦，貌似不对。如果你没有换姓名的话，那肯定换手机号了...（你还有" + diffTimes + "次回复机会哦）";
					}
				} else if (status == 3) {
					text = "超过三次验证未通过，请发邮件给我： ****@gmail.com";
				} else if (status == 4) {
					unbindMap.remove(from);
					text = "解绑成功，你仍可通过回复暗号，重新加入我们";
				} else {
					int i = new Random().nextInt(4);
					if (i > 3) {
						i = 0;
					}
					text = strs[i];
				}

			} else {
				// 更新自己手机号码
				if (content.startsWith("up")) {
					String[] ss = content.split(" ");
					String phone = ss[1];
					if (phone.length() == 11) {
						Map params = new HashMap();
						params.put("phone", phone);
						msgService.updateInfoByParams(from, params);
						text = "手机号码修改成功";
					} else {
						text = "请输入正确的手机号码";
					}
					return responseXml(from, text);
				}
				
				if (content.trim().equals("外卖")) {
					schedualMap.put(from + "_waimai", new Date().getTime()/1000);
					if (!isThreadRun) {
						isThreadRun = true;
						new Thread(new ClearMapThread(from)).start();
					}
					return responseXml(from, waimaiInfo);
				}
				
				if (schedualMap.containsKey(from + "_waimai")) {
					if (StringUtils.isNumeric(content)) {
						int type = Integer.valueOf(content);
						if (type == 0) {
							schedualMap.remove(from + "_waimai");
							return responseXml(from, "已经退出外卖模式。"); 
						}
						String str = (String) Waimai.WAIMAIMAP.get(type);
						if (str != null) {
							System.out.println(responseWaimaiPhotoXml(from, str));
							return responseWaimaiPhotoXml(from, str);
						}
					} else {
						return responseXml(from, "回复数字选择外卖，回复0退出");
					}
				}

				if (content.trim().length() == 4 && StringUtils.isNumeric(content)) {
					text = msgService.getPhoneBylastNumbers(content);
					return responseXml(from, text);
				}
				
				if (content.trim().equals("头像")) {
					photoMap.put(from, 1);
					text = "你可以开始上传头像，支持jpg,gif,png格式。";
					return responseXml(from, text);
				}
				
				if (content.trim().equals("图文")) {
					photoMap.put(from, 2);
					return responseXml(from, "回复形式将会变为图文形式"); 
				}


				if (content.contains("，")) {
					text = msgService.addUserTags(content);
				} else {
					String[] strs = content.split(" ");
					if (photoMap.containsKey(from)) {
						if (photoMap.get(from) == 1 && msgType.equals("image")) {
							String picUrl = d.getElementsByTagName("PicUrl").item(0).getFirstChild().getNodeValue();
							Map params = new HashMap();
							params.put("picUrl", picUrl);
							msgService.updateInfoByParams(from, params);
							photoMap.remove(from);
							return this.responseXml(from, "头像上传成功。");
						}
						
						if (photoMap.get(from) == 2) {
							WeixinInfo info = msgService.getPhotoMessageInfo(strs[0]);
							return responsePhotoXml(from, info);
						}
					}
					
					String contactCell = msgService.findContactCell(strs[0]);
					text = contactCell;
				}
			}

		}
		return responseXml(from, text);
	}

	private String responseXml(String from, String text) {
		String xmlString = "<xml>" + "<ToUserName><![CDATA[" + from + "]]></ToUserName>"
				+ "<FromUserName><![CDATA[idxyer]]></FromUserName>" + "<CreateTime>" + new Date().getTime() + "</CreateTime>"
				+ "<MsgType><![CDATA[text]]></MsgType>" + "<Content><![CDATA[" + text.toString() + "]]></Content>"
				+ "<FuncFlag>0</FuncFlag></xml>";

		return xmlString;
	}
	
	private String responsePhotoXml(String from, WeixinInfo info) {
		String picUrl = "http://idxyer.linkmed.com.cn/dxy-weixin/image/nn.jpg";
		String url = "http://idxyer.linkmed.com.cn/dxy-weixin/api/info/" + from + "/" + info.getId();
		String xmlString = "<xml>" + "<ToUserName><![CDATA[" + from + "]]></ToUserName>"
				+ "<FromUserName><![CDATA[idxyer]]></FromUserName>" + "<CreateTime>" + new Date().getTime() + "</CreateTime>"
				+ "<MsgType><![CDATA[news]]></MsgType><ArticleCount>1</ArticleCount><Articles><item><Title><![CDATA["+info.getRealName()+"]]>"
				+ "</Title><Description><![CDATA[" + info.getContactCell() + "]]></Description><PicUrl><![CDATA[" + picUrl
				+ "]]></PicUrl><Url><![CDATA[" + url + "]]></Url>" + "</item> </Articles>" + "<FuncFlag>1</FuncFlag></xml>";

		LoggerHandle.info(xmlString);
		return xmlString;
	}
	
	private String responseWaimaiPhotoXml(String from, String picUrl) {
		String[] picurls = picUrl.split(",");
		String xmlString = "<xml>" + "<ToUserName><![CDATA[" + from + "]]></ToUserName>"
				+ "<FromUserName><![CDATA[idxyer]]></FromUserName>" + "<CreateTime>" + new Date().getTime() + "</CreateTime>"
				+ "<MsgType><![CDATA[news]]></MsgType><ArticleCount>"+picurls.length+"</ArticleCount><Articles><item><Title><![CDATA[外卖单子]]>"
				+ "</Title><Description><![CDATA[点击查看详情]]></Description><PicUrl><![CDATA[" + picurls[0] + "]]></PicUrl><Url><![CDATA["
				+ picurls[0] + "]]></Url></item>";

		if (picurls.length > 1) {
			xmlString = xmlString + "<item><Title><![CDATA[外卖单子]]>"
					+ "</Title><Description><![CDATA[点击查看详情]]></Description><PicUrl><![CDATA[" + picurls[1] + "]]></PicUrl><Url><![CDATA["
					+ picurls[1] + "]]></Url></item>";
		}

		xmlString = xmlString + " </Articles>" + "</xml>";

		LoggerHandle.info(xmlString);
		return xmlString;
	}
	
	private String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	class ClearMapThread implements Runnable {
		String from ="";
		ClearMapThread (String from) {
			this.from = from;
		}

		public void run() {
			String key = from + "_waimai";
			while (true) {
				if (schedualMap.containsKey(key)) {
					long time = (Long) schedualMap.get(key);
					int diffminuter = (int) (new Date().getTime() / 1000 - time) / 60;
					if (diffminuter >= 5) {
						schedualMap.remove(key);
					}
				}
				try {
					Thread.sleep(1000 * 60 * 1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
