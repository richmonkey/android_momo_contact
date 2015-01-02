
package cn.com.nd.momo.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONObject;

import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.http.HttpTool;
import cn.com.nd.momo.api.types.Attachment;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.Utils;

/**
 * 图片上传类
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public final class PhotoUpLoad {
    // 每次100kb
    private static final int PERIOD_LEN = 1024 * 100;
    private static final int PERIOD_LEN_FULL = 1024 * 200;
    
    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    
    /**
     * 断点上传文件
     * @param fileName
     * @return
     * @throws MoMoException
     */
    public static Attachment uploadPhotoFile(String fileName) throws MoMoException {
        Attachment result = null;
        
        File file = new File(fileName);
        
        if(!file.exists()) {
        	return null;
        }
        
        HttpTool http = new HttpTool(RequestUrl.PHOTO_URL);
        try {
            long size = file.length();

            String strMd5 = getMD5Checksum(fileName);
            JSONObject param = new JSONObject();
            param.put("md5", strMd5);
            param.put("size", size);
            param.put("source", MoMoHttpApi.APP_ID);

            // 上传第一步
            http.DoPost(param);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            if (jsonResponse.optBoolean("uploaded")) {
                result = new Attachment();
                result.setID(jsonResponse.optString("id"));
                result.setUrl(jsonResponse.optString("src"));
            } else {
                String uploadID = jsonResponse.optString("upload_id");

                // 上传数据
                result = upLoadPhotoProcess(fileName, uploadID, 0);
            }
        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }
    
    private static Attachment upLoadPhotoProcess(String fileName, String uploadID, final long offset) throws MoMoException {
    	File file = new File(fileName);
        Attachment result = null;

        int len = PERIOD_LEN_FULL;
        long totalLen = file.length();
        if (offset + len > totalLen)
            len = (int)(totalLen - offset);
        
        byte[] bytes = new byte[len];
        FileInputStream fis = null;
        try {
			fis = new FileInputStream(file);
			fis.skip(offset);
			fis.read(bytes, 0, len);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new MoMoException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new MoMoException(e1);
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
        
        // set url
        HttpTool http = new HttpTool(RequestUrl.PHOTO_URL
                + "?upload_id=" + uploadID
                + "&length=" + len + "&offset=" + offset);
        Log.i("upload process : " + RequestUrl.PHOTO_URL
                + "?upload_id=" + uploadID
                + "&length=" + len + "&offset=" + offset);

        ByteArrayEntity buffer = new ByteArrayEntity(bytes);
        try {
            http.DoPostByteArray(buffer);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            if (!jsonResponse.optBoolean("uploaded")) {
                upLoadPhotoProcess(fileName, uploadID, jsonResponse.optLong("offset"));
            } else {
                result = new Attachment();
                result.setID(jsonResponse.optString("id"));
                result.setUrl(jsonResponse.optString("src"));
            }
        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }

    /**
     * 上传图片数据
     * 
     * @param bytes
     * @return 图片url
     * @throws MoMoException
     */
    public static Attachment upLoadPhotoByte(byte[] bytes) throws MoMoException {
        Attachment result = null;

        if (bytes == null) {
            throw new MoMoException("照片上传失败");
        }

        HttpTool http = new HttpTool(RequestUrl.PHOTO_URL);
        try {
            long size = bytes.length;

            String strMd5 = Utils.getMD5OfBytes(bytes);
            JSONObject param = new JSONObject();
            //TODO PhotoUpLoad.bytes = bytes;
            param.put("md5", strMd5);
            param.put("size", size);
            param.put("source", MoMoHttpApi.APP_ID);

            // 上传第一步
            http.DoPost(param);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            if (jsonResponse.optBoolean("uploaded")) {
                result = new Attachment();
                result.setID(jsonResponse.optString("id"));
                result.setUrl(jsonResponse.optString("src"));
            } else {
                String uploadID = jsonResponse.optString("upload_id");

                // 上传数据
                result = upLoadPhotoProcess(bytes, uploadID, 0);
            }
        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }

    /**
     * 上传照片数据
     * 
     * @param offset
     * @return
     * @throws MoMoException
     */
    private static Attachment upLoadPhotoProcess(byte[] bytes, String uploadID, final long offset) throws MoMoException {
        Attachment result = null;

        long len = PERIOD_LEN;
        if (offset + len > bytes.length)
            len = bytes.length - offset;

        // set url
        HttpTool http = new HttpTool(RequestUrl.PHOTO_URL
                + "?upload_id=" + uploadID
                + "&length=" + len + "&offset=" + offset);
        byte[] tmp = null;
        if (len > 0) {
            tmp = new byte[(int)len];
            for (long i = offset; i < offset + len; ++i) {
                tmp[(int)(i - offset)] = bytes[(int)(i)];
            }
        }
        Log.i("upload process : " + RequestUrl.PHOTO_URL
                + "?upload_id=" + uploadID
                + "&length=" + len + "&offset=" + offset);

        ByteArrayEntity buffer = new ByteArrayEntity(tmp);
        try {
            http.DoPostByteArray(buffer);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            if (!jsonResponse.optBoolean("uploaded")) {
                upLoadPhotoProcess(bytes, uploadID, jsonResponse.optLong("offset"));
            } else {
                result = new Attachment();
                result.setID(jsonResponse.optString("id"));
                result.setUrl(jsonResponse.optString("src"));
            }
        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }
}
