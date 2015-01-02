
package cn.com.nd.momo.api.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import cn.com.nd.momo.api.http.HttpToolkit;

public class BitmapToolkit {
    private final static String TAG = "BitmapToolkit";

    public final static String DIR_DEFAULT = Environment.getExternalStorageDirectory().getPath()
            + "/BitmapToolkit/";

    public final static String DIR_MOMO_PHOTO = Environment.getExternalStorageDirectory().getPath()
            + "/momo/photo/";

    public final static String DIR_MOMO_CAMERA = Environment.getExternalStorageDirectory()
            .getPath()
            + "/momo/camera/";

    public final static String DIR_MOMO_IM_MAP = Environment.getExternalStorageDirectory()
            .getPath()
            + "/momo/im/maps/";

    public final static String DIR_MOMO_IM_PICTURE = Environment.getExternalStorageDirectory()
            .getPath()
            + "/momo/im/pictures/";

    public final static String DIR_MOMO_IM_AUDIO = Environment.getExternalStorageDirectory()
            .getPath()
            + "/momo/im/audios/";

    public final static String DIR_AVATAR = "avatar/";

    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/momo/";

    // public final static String DIR_MYHOME_TAKEAWAY =
    // Environment.getExternalStorageDirectory().getPath()+"/MyhomeTakeaway/";

    private String mSid = null;

    /**
     * 图片质量
     */
    private static final int QUALITY = 80;

    public void setSid(String sid) {
        mSid = sid;
    }

    public String getSid() {
        if (mSid == null)
            return "";
        return "?sid=" + mSid;
    }

    public static void deletePic(long pid, int size) {
        deletePic(pid, size, DIR_DEFAULT);
    }

    public static void deletePic(long pid, int size, String dir) {
        File dirFile = new File(getFullPath(pid, size, dir));
        if (dirFile.exists()) {
            boolean result = dirFile.delete();
            Log.i(TAG, "deletePic" + getFullPath(pid, size, dir) + "result:" + result);
        }
    }

    private static String getPath(long pid, String dir) {
        String path = ALBUM_PATH + dir;// + pid % 64 + "/";
        return path;
    }

    private static String getName(long pid, int size) {
        return pid + "_" + size + ((size < 100) ? ".cache" : ".jpg");// +
        // "_cache";
    }

    private static String getFullPath(long pid, int size, String dir) {
        return getPath(pid, dir) + getName(pid, size);
    }

    public static boolean isExist(long pid, int size) {
        return isExist(pid, size, DIR_DEFAULT);
    }

    public static boolean isExist(long pid, int size, String dir) {
        File dirFile = new File(getFullPath(pid, size, dir));
        boolean isExit = dirFile.exists() && dirFile.length() > 0;
        Log
                .i(TAG, "isExit:" + dirFile.length() + "----" + pid + isExit
                        + dirFile.getAbsolutePath());
        return isExit;
    }

    /**
     * <br>
     * Description:rotate Bitmap <br>
     * Author:hexy <br>
     * Date:2011-4-1上午10:23:00
     * 
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (degree == 0)
            return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            Bitmap tempBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                    .getHeight(), matrix, true);

            // Bitmap操作完应该显示的释放
            bitmap.recycle();
            bitmap = tempBmp;
        } catch (OutOfMemoryError ex) {
            // Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
        }
        if (bitmap.isRecycled()) {
            bitmap = null;
        }
        return bitmap;
    }

    private String mDirectory = DIR_DEFAULT;

    // file name
    private String mName = "";

    private String mRemoteUrl = "";

    private String mSuffix = "";

    private String mPrefix = "";

    /**
     * <br>
     * Description: 实现图片缓存,下载的类. <br>
     * Author:hexy <br>
     * Date:2011-4-1上午10:30:09
     * 
     * @param dir 缓存文件夹绝对路径
     * @param url 图片远程的url, 读取本地图片无需用此构造函数
     * @param prefix 图片前缀
     * @param suffix 图片后缀
     */
    public BitmapToolkit(String dir, String url, String prefix, String suffix) {
        mDirectory = dir;
        mPrefix = prefix;
        mSuffix = suffix;
        mName = calculateMd5(url);
        mRemoteUrl = url;
        this.mkdirsIfNotExist();
    }

    public boolean isExist() {
        File dirFile = new File(getAbsolutePath());
        boolean isExit = dirFile.exists() && dirFile.length() > 0;
        Log.i(TAG, "isExit:" + isExit
                + " size:" + dirFile.length()
                + " absoulutePath:" + getAbsolutePath());
        return isExit;
    }

    public void deletePic() {
        File dirFile = new File(getAbsolutePath());
        if (dirFile.exists()) {
            boolean result = dirFile.delete();
            Log.i(TAG, "deletePic" + getAbsolutePath() + "result:" + result);
        }
    }

    public void deleteAll() {
        File dirFile = new File(getDirecotry());
        if (dirFile.isDirectory()) {
            try {
                FileUtils.forceDelete(dirFile);
            } catch (IOException ioe) {
                Log.e(TAG, ioe.toString());
                ioe.printStackTrace();
            }

            Log.i(TAG, "deletePic all pic!! result:");
        }
    }

    /**
     * get degree from exif
     */
    public static int getDegree(String filename) {
        int result = 0;
        int orientation = 0;
        try {
            ExifInterface exif = new ExifInterface(filename);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

        } catch (Exception e) {
        }

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                result = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                result = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                result = 270;
                break;
        }

        return result;
    }

    public Bitmap loadBitmapNetOrLocal() {
        // Log.i(TAG,"loadBitmapNetOrLocal"+ mRemoteUrl);
        // if (isExist()) {
        // return loadBitmap();
        // }
        // else {
        // HttpToolkit http = new HttpToolkit(mRemoteUrl);
        // byte[] bypte = http.DownLoadBytes();
        // if (bypte==null) return null;
        // saveByte(bypte);
        // return ByteArrayToBitmap(bypte);
        // }
        return loadBitmapNetOrLocalScale(400);
    }

    public Bitmap loadBitmapNetOrLocalScale(int size) {
        Log.i(TAG, "loadBitmapNetOrLocal" + mRemoteUrl);
        Bitmap bmpThumb;
        if (isExist()) {
            bmpThumb = loadLocalBitmapRoughScaled(getAbsolutePath(), size);
            return bmpThumb;
        } else {
            HttpToolkit http = new HttpToolkit(mRemoteUrl);
            byte[] bypte = http.DownLoadBytes();
            if (bypte == null)
                return null;
            saveByte(bypte);
            bmpThumb = loadLocalBitmapRoughScaled(getAbsolutePath(), size);
            return bmpThumb;
        }
    }

    public String loadAudioFileNetOrLocal() {
        Log.i(TAG, "loadAudioFileNetOrLocal" + mRemoteUrl);
        if (!isExist()) {
            Log.i(TAG, "loadAudioFileNetOrLocal not exist");
            HttpToolkit http = new HttpToolkit(mRemoteUrl + getSid());
            byte[] bypte = http.DownLoadBytes();
            if (bypte == null)
                return null;
            saveByte(bypte);
        }
        String result = this.getAbsolutePath();
        Log.i(TAG, "loadAudioFileNetOrLocal" + result);
        return result;
    }

    /**
     * 下载地图图片
     * 
     * @return
     */
    public Bitmap loadMapBitmapNetOrLocal() {
        Log.i(TAG, "loadBitmapNetOrLocal" + mRemoteUrl);
        if (!isExist()) {
            HttpToolkit http = new HttpToolkit(mRemoteUrl + getSid());
            byte[] bypte = http.DownLoadBytes();
            if (bypte == null)
                return null;
            Bitmap bitmap = ByteArrayToBitmap(bypte);

            Bitmap output = halfCenterBitmap(bitmap, 8);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            output.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            saveByte(byteArray);

            output.recycle();
            output = null;
        }
        return loadBitmap();
    }

    public static Bitmap halfCenterBitmap(Bitmap bitmap, final float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.save();
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final int border = 0;
        final Rect rect = new Rect(border, border, output.getWidth() - 2 * border, output
                .getHeight()
                - 2 * border);
        final int left = border + bitmap.getWidth() / 4;
        final int top = border + bitmap.getHeight() / 4;
        final Rect src = new Rect(left, top, output.getWidth() + left - 2 * border, output
                .getHeight()
                + top - 2 * border);
        final Rect dst = new Rect(border, border, output.getWidth() - 2 * border, output
                .getHeight()
                - 2 * border);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        canvas.restore();

        return output;
    }

    public byte[] loadBytesNetOrLocal() {
        if (isExist()) {
            return getBytesFromFile();
        } else {
            HttpToolkit http = new HttpToolkit(mRemoteUrl);
            byte[] bypte = http.DownLoadBytes();
            saveByte(bypte);
            return bypte;
        }
    }

    public Bitmap loadLocalBitmap(String url, int degree) {
        return rotateBitmap(loadLocalBitmap(url), degree);
    }

    public Bitmap loadLocalBitmap(String url) {
        Bitmap bitmap = null;

        try {
            File dirFile = new File(url);

            if (!dirFile.exists()) {
                Log.i(TAG, "loadBitmap not exit");
            } else {
                InputStream fis = new FileInputStream(url);
                bitmap = BitmapFactory.decodeStream(new FlushedInputStream(fis));
                Log.i(TAG, "loadBitmap " + url);
                fis.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadBitmap" + e.toString());
        }

        return bitmap;
    }

    // add by gongxt 2011-2-22: use this function to find out big image's DPI
    public static int getBigImageDpi(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();

        // display metrics is 120, 180, 240
        if (display.densityDpi == DisplayMetrics.DENSITY_LOW) {
            // TODO: dowload different images
        } else if (display.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
            // TODO: dowload different images
        } else if (display.densityDpi == DisplayMetrics.DENSITY_HIGH) {
            // TODO: dowload different images
        }

        // now we dowload 480*480 only
        return 480;
    }

    public static Bitmap loadLocalBitmapRoughScaled(String path, int maxsize) {
        Bitmap bm = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.outHeight = maxsize;
            options.inJustDecodeBounds = true;
            options.inTempStorage = new byte[32 * 1024];
            // 获取这个图片的宽和高
            bm = BitmapFactory.decodeFile(path, options); // 此时返回bm为空

            options.inJustDecodeBounds = false;
            int be = options.outHeight / (maxsize / 10);
            if (be % 10 != 0)
                be += 10;

            be = be / 10;
            if (be <= 0)
                be = 1;

            options.inSampleSize = be;
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
                bm = null;
                System.gc();
            }
            bm = BitmapFactory.decodeFile(path, options);
            // Log.i(TAG, "getLocalBitmap width " + bm.getWidth() + " height " +
            // bm.getHeight());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bm = null;
        }
        return bm;
    }

    public static Bitmap loadLocalBitmapExactScaled(String path, int size) {
        Bitmap bm = loadLocalBitmapRoughScaled(path, size * 2);
        if (bm == null) {
            return bm;
        }
        // rotate from exif
        int degree = getDegree(path);
        bm = rotateBitmap(bm, degree);
        return compress(bm, size);
    }

    /**
     * 获取图片压缩数据
     */
    public static byte[] loadLocalBitmapExactScaledBytes(String path, int size) {
        Bitmap bm = loadLocalBitmapExactScaled(path, size);
        byte[] result = BitmapToByteArray(bm);
        if(bm != null) {
        	bm.recycle();
        }
        return result;
    }

    public static Bitmap cornerBitmap(Bitmap bitmap, final float roundPx, int frameColor) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.save();
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        canvas.restore();

        canvas.save();
        Paint paintFrame = new Paint();
        paintFrame.setAntiAlias(true);
        paintFrame.setStyle(Paint.Style.STROKE);
        paintFrame.setColor(frameColor);
        paintFrame.setStrokeWidth(0);
        canvas.drawRoundRect(new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight())),
                roundPx, roundPx, paintFrame);
        canvas.restore();
        return output;
    }

    public static Bitmap cornerBitmap(Bitmap bitmap, final float roundPx) {
        return cornerBitmap(bitmap, roundPx, Color.TRANSPARENT);
    }

    public static Bitmap compress(Bitmap bitmap, int size) {
        if (bitmap == null)
            return null;
        if (bitmap.isRecycled())
            return null;
        // create explicit picture
        int max = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        int min = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        min = size * min / max;
        max = size;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            bitmap = Bitmap.createScaledBitmap(bitmap, max, min, false);
        } else {
            bitmap = Bitmap.createScaledBitmap(bitmap, min, max, false);
        }
        return bitmap;
    }

    public void mkdirsIfNotExist() {
        File dirFile = new File(getDirecotry());
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
    }

    // 3 save byte to sdcard
    public boolean saveByte(byte[] byteArray) {
        if (byteArray == null)
            return false;
        Log.i(TAG, "saveByte" + mRemoteUrl + getAbsolutePath());
        mkdirsIfNotExist();

        File myCaptureFile = new File(getAbsolutePath());

        FileOutputStream fileOutPutStream;
        try {

            fileOutPutStream = new FileOutputStream(myCaptureFile);

            fileOutPutStream.write(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            Log.e(TAG, "saveBitmap" + e.toString());
            return false;
        }

        try {
            fileOutPutStream.close();
        } catch (Exception e) {
        }

        return true;
    }

    public byte[] getBytesFromFile() {
        File f = new File(getAbsolutePath());

        if (f.exists()) {
            try {
                FileInputStream stream = new FileInputStream(f);
                ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
                byte[] b = new byte[1000];
                int n;
                while ((n = stream.read(b)) != -1)
                    out.write(b, 0, n);
                stream.close();
                out.close();
                return out.toByteArray();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * <br>
     * Description: <br>
     * Author:hexy <br>
     * Date:2011-4-15下午08:34:52
     * 
     * @param url
     * @return .gif .jpg ...
     */
    public static String getSuffix(String url) {
        url = url.replace("?momolink=0", "");
        int index = url.lastIndexOf(".") - 1;

        if (index > 0 && index < url.length() - 1) {
            String typeStr = url.substring(index + 1);
            return typeStr.toLowerCase();
        }

        return "";
    }

    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        byte[] result = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, QUALITY, stream);
        result = stream.toByteArray();

        return result;
    }

    public static Bitmap ByteArrayToBitmap(byte[] byteArray) {
        Bitmap result = null;
        result = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return result;
    }

    private Bitmap loadBitmap() {
        return loadLocalBitmap(getAbsolutePath());
    }

    public static boolean available() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static long getAvailaSize() {
        if (!available())
            return 0;

        File path = Environment.getExternalStorageDirectory();
        // 取得sdcard文件路径
        StatFs statfs = new StatFs(path.getPath());
        // 获取block的SIZE
        long blocSize = statfs.getBlockSize();
        // 己使用的Block的数量
        long availaBlock = statfs.getAvailableBlocks();

        return availaBlock * blocSize;
    }

    public static boolean isFullStorage() {
        long size = getAvailaSize();
        // 1M = 1048576
        return size < 1048576;
    }

    public static boolean saveBitmap(Bitmap bmp, long pid, int size, String dir) {
        if (bmp == null)
            return false;
        if (isFullStorage())
            return false;

        File dirFile = new File(getPath(pid, dir));
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            File myCaptureFile = new File(getFullPath(pid, size, dir));
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile), 8 * 1024);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveBitmap create" + getFullPath(pid, size, dir));
        } catch (Exception e) {
            Log.e(TAG, "saveBitmap" + e.toString());
            return false;
        }
        return true;
    }

    // private boolean saveBitmap(Bitmap bmp) {
    // if (bmp==null) return false;
    // if (isFullStorage())
    // return false;
    //
    // File dirFile = new File(getDirecotry());
    // if (!dirFile.exists()) {
    // dirFile.mkdirs();
    // }
    // try {
    // File myCaptureFile = new File(getAbsolutePath());
    // BufferedOutputStream bos = new BufferedOutputStream(
    // new FileOutputStream(myCaptureFile),8*1024);
    // // bmp.compress(Bitmap.CompressFormat.JPEG, 60, bos);
    // bos.flush();
    // bos.close();
    // Log.i(TAG, "saveBitmap create" + getAbsolutePath());
    // } catch (Exception e) {
    // Log.e(TAG, "saveBitmap" + e.toString());
    // return false;
    // }
    // return true;
    // }

    public String getAbsolutePath() {
        return mDirectory + mPrefix + mName + mSuffix;
    }

    public String getDirecotry() {
        return mDirectory;
    }

    public String getFileName() {
        return mPrefix + mName + mSuffix;
    }

    /**
     * <br>
     * Description:计算md5值 <br>
     * Author:hexy <br>
     * Date:2011-4-1上午10:12:47
     * 
     * @param str
     * @param visible
     * @return
     */
    private static String calculateMd5(String str) {
        if (str == null || str.length() == 0)
            return "";
        Log.i(TAG, "Md5" + str + "  " + Utils.getMD5OfBytes(str.getBytes()));
        return Utils.getMD5OfBytes(str.getBytes());
    }

    /**
     * @author shawn manager bitmaps memory
     */
    public static class BitmapMemoryMgr {
        private ArrayList<Bitmap> mBitmapArray;

        public BitmapMemoryMgr() {
            mBitmapArray = new ArrayList<Bitmap>();
        }

        public void addBitmap(Bitmap bitmap) {
            mBitmapArray.add(bitmap);
        }

        public void releaseAllMemory() {
            if (mBitmapArray == null)
                return;
            for (Bitmap bmp : mBitmapArray) {
                if (bmp != null && !bmp.isMutable()) {
                    bmp.recycle();
                    bmp = null;
                }
            }
            Log.i(TAG, "releaseAllMemory : " + mBitmapArray.size());
            mBitmapArray.clear();
        }
    }

    private static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int bytes = read();
                    if (bytes < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
