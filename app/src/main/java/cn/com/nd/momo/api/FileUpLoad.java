
package cn.com.nd.momo.api;

import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONObject;

import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.http.HttpTool;
import cn.com.nd.momo.api.util.Log;
import cn.com.nd.momo.api.util.Utils;

/**
 * 文件上传封装
 * 
 * @author 曾广贤 (muroqiu@sina.com)
 */
public final class FileUpLoad {
    private static long size;

    private static byte[] bytes = null;

    private static String upLoadID;

    /**
     * 上传数据
     * 
     * @param bytes
     * @param fileName
     * @param fileType
     * @return 文件url
     * @throws MoMoException
     */
    public static String upLoadFileByte(byte[] bytes, int fileType, String fileName)
            throws MoMoException {
        String result = null;

        if (bytes == null) {
            throw new MoMoException("文件上传失败");
        }

        HttpTool http = new HttpTool(RequestUrl.FILE_UPLOAD_URL);
        try {
            size = bytes.length;

            String strMd5 = Utils.getMD5OfBytes(bytes);
            JSONObject param = new JSONObject();
            FileUpLoad.bytes = bytes;
            param.put("md5", strMd5);
            param.put("size", size);
            param.put("type", fileType);
            if (fileName != null && !fileName.equals("")) {
                param.put("filename", fileName);
            }

            // 上传第一步
            http.DoPost(param);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            if (jsonResponse.optBoolean("uploaded")) {
                result = jsonResponse.optString("src");
            } else {
                upLoadID = jsonResponse.optString("upload_id");

                // 上传数据
                result = upLoadFileProcess(0);
            }
        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }

    /**
     * 上传数据
     * 
     * @param offset
     * @return
     * @throws MoMoException
     */
    private static String upLoadFileProcess(final long offset) throws MoMoException {
        String result = null;

        // 每次100kb
        long len = 1024 * 100;
        if (offset + len > bytes.length)
            len = bytes.length - offset;

        // set url
        HttpTool http = new HttpTool(RequestUrl.FILE_UPLOAD_URL + "?upload_id="
                + upLoadID
                + "&length=" + len + "&offset=" + offset);
        byte[] tmp = null;
        if (len > 0) {
            tmp = new byte[(int)len];
            for (long i = offset; i < offset + len; ++i) {
                tmp[(int)(i - offset)] = bytes[(int)(i)];
            }
        }
        Log.i("upload process : " + RequestUrl.PHOTO_URL
                + "?upload_id=" + upLoadID
                + "&flen=" + len + "&offset=" + offset);

        ByteArrayEntity buffer = new ByteArrayEntity(tmp);
        try {
            http.DoPostByteArray(buffer);
            JSONObject jsonResponse = new JSONObject(http.GetResponse());
            if (!jsonResponse.optBoolean("uploaded")) {
                upLoadFileProcess(jsonResponse.optLong("offset"));
            } else {
                result = jsonResponse.optString("src");
            }
        } catch (Exception e) {
            throw new MoMoException(e);
        }

        return result;
    }
}
