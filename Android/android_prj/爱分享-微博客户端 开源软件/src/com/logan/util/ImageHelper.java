package com.logan.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * 图片处理类
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class ImageHelper
{
  public static Bitmap getPicFromBytes(byte[] paramArrayOfByte, BitmapFactory.Options paramOptions)
  {
    Bitmap localBitmap = null;
    if (paramArrayOfByte != null)
      if (paramOptions != null)
        localBitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, paramOptions);
    while (true)
    {
      return localBitmap;
//      localBitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length);
//      continue;
//      localBitmap = null;
    }
  }

  public static byte[] readStream(InputStream paramInputStream)
    throws Exception
  {
    byte[] arrayOfByte1 = new byte[1024];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    while (true)
    {
      int i = paramInputStream.read(arrayOfByte1);
      if (i == -1)
      {
        byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
        localByteArrayOutputStream.close();
        paramInputStream.close();
        return arrayOfByte2;
      }
      localByteArrayOutputStream.write(arrayOfByte1, 0, i);
    }
  }
}