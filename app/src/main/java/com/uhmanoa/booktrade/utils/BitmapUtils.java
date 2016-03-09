package com.uhmanoa.booktrade.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BitmapUtils {

    private static Bitmap bitmap;
    private static final String TAG = "BitmapUtils";

    /**
     * 
     * @param options   BitmapFactory.Options
     * @param reqHeight 
     * @param reqWidth  
     * @return  
     */
    private static int getSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
        int height = options.outHeight;
        int width = options.outWidth;
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = (int) (height / (float) reqHeight);
            int widthRatio = (int) (width / (float) reqWidth);
            sampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
        }
        return sampleSize;
    }

    /**
     * 
     * @param srcPath   
     * @return  
     */
    public static Bitmap compressBitmapFromFile(String srcPath, int reqHeight, int reqWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(srcPath, options);
        int sampleSize = getSampleSize(options, reqHeight, reqWidth);

        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        options.inPurgeable = true;// 
        options.inInputShareable = true;//
        bitmap = BitmapFactory.decodeFile(srcPath, options);
        return bitmap;
    }

    public static String saveToSdCard(Context mContext, Bitmap bitmap){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String files =CacheUtils.getCacheDirectory(mContext, true, "pic") + timeStamp+".jpg";
        File file=new File(files);
        try {
            FileOutputStream out=new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)){
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtils.i(TAG, file.getAbsolutePath());
        return file.getAbsolutePath();
    }


    public static void recyle(){
        if (bitmap.isRecycled() || bitmap == null) {
            return;
        } else {
            bitmap.recycle();
        }
    }
}
