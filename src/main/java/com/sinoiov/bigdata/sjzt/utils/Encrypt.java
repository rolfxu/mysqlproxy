package com.sinoiov.bigdata.sjzt.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Encrypt {
    /**
     * 生成32位md5码
     *
     * @param password
     * @return
     */
    public static String md5Password(String password) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    // test key TESTCTFO
    private static final String PUBLICKEY = "CTFOTRV1";

    public static String decrypt(String message, String key) throws Exception {
        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(2, secretKey, iv);

        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    private static byte[] encrypt(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(1, secretKey, iv);

        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    protected static byte[] convertHexString(String ss) {
        byte[] digest = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }

        return digest;
    }

    public static String encrypt(String message) {
        if ((message == null) || (message.trim().length() == 0)) {
            System.err.println("message is  empty");
            return null;
        }
        try {
            String mw = toHexString(encrypt(message, PUBLICKEY)).toUpperCase();
            return mw;
        } catch (Exception e) {
        }
        return null;
    }

    public static String decrypt(String ciphertext)  {
        try {
            String clearText = decrypt(ciphertext, PUBLICKEY);
            return clearText;
        } catch (Exception e) {
        }
        return ciphertext;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String changeVehicleNo(String vehicleNo) {
        StringBuffer sb = new StringBuffer();
        Integer lastNum = 0;
        for (int i = 0; i < vehicleNo.length(); i++) {
            String str = vehicleNo.charAt(i) + "";
            String newStr = str;
            if (isNumeric(str)) {

                int j = Integer.parseInt(str);

                if (j == 9) {
                    newStr = "0";
                } else {
                    newStr = (Integer.parseInt(str) + 1) + "";
                }
                lastNum = Integer.parseInt(newStr) + 1;
            }
            sb.append(newStr);
        }

        return sb.toString() + lastNum;
    }

    protected static String toHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xFF & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }
        return hexString.toString();
    }

    public static String encryptByKey(String message, String key) throws Exception {
        return toHexString(encrypt(message, key)).toUpperCase();
    }

    public static void main(String[] args) throws Exception {
//        String decrypt = decrypt("E6EB18CDC5EB59F6034A3583092560FC","TESTCTFO");
    	
    	String[] abc = {"沪EC1709",
    			"鲁PT7351",
    			"鲁Q219GX",
    			"鲁FAJ297",
    			"冀A6311V",
    			"鲁HH3377",
    			"鲁HH3377",
    			"鲁J68312",
    			"鲁RN9991",
    			"苏CHF998",
    			"鲁L16938",
    			"鲁PT2801",
    			"冀EM5735",
    			"冀D1H023",
    			"豫J99980",
    			"豫J99980",
    			"豫J99980",
    			"冀T40615",
    			"冀J0A181",
    			"晋KC5352",
    			"晋BR1250",
    			"沪FB3826",
    			"鲁AL6333",
    			"京AFC323",
    			"吉JH9331",
    			"冀J7T603",
    			"冀J7T603",
    			"冀J7T603",
    			"鲁RQ2986",
    			"鲁FE7001",
    			"鲁H00W63",
    			"鲁UE2083",
    			"晋M91431",
    			"晋M91431",
    			"晋M91431",
    			"鲁Q569CA",
    			"鲁Q569CA",
    			"鲁Q569CP",
    			"鲁PR0786",
    			"鲁H87F57",
    			"冀AZV751",
    			"鲁A99100",
    			"鲁GW0992",
    			"冀A93587",
    			"鲁MC2665",
    			"冀EN9863",
    			"鲁PT8092",
    			"黑MD1500",
    			"鲁GY7503",
    			"晋C25878"
};
    	
    	for(String a:abc) {
//    		System.out.println(encrypt(a));
    	}
        String decrypt = decrypt("83B8D78E6B0757BDDAD9F04B38F818BE");
        					  //  73C66E38D426250D8F5EB9897AD04FF3
//        System.out.println(decrypt);
//        
        System.out.println(encrypt("LGGG4DX3XGL358437"));
        
       System.out.println(encrypt("鲁Q570CW")) ;

       System.out.println(encrypt("鲁Q775LL")) ;

       System.out.println(encrypt("鲁Q257BL")) ;

       System.out.println(encrypt("鲁Q563JT")) ;

       System.out.println(encrypt("鲁Q686DX")) ;
       
       String s = "\\x22\\xE5\\x93\\x88\\xE5\\x93\\x88\\x22";
       String s1 = s.replaceAll("\\\\x", "%");
       String decode = URLDecoder.decode(s1, "utf-8");
       System.out.println(decode);

//    	String[] values= {"E_aaa","M_bbb"};
//
//	  		boolean export = false;
//	  		boolean meger = false;
//	  		for(String text : values) {
//	  			String value = text.toString();
//	  			if(value.startsWith("E_")) {
//	  				export =true;
//	  			} else if(value.startsWith("M_")) {
//	  				meger = true;
//	  			}
//	  		}
//	  		if(export&&meger) {
//	  			System.out.println("d");
//	  		}
	  	
    	
    }

}

