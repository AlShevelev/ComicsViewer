package com.syleiman.comicsviewer.Common.Helpers.Files;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.syleiman.comicsviewer.App;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Work with files in application private area
 */
public class AppPrivateFilesHelper
{
    /**
     * @param sourceAbsoluteFileName - source file full name (with path)
     * @param destinationFileName - destination file name WITHOUT PATH
     * @return true - if file created successfully
     */
    public static boolean createFromFile(String sourceAbsoluteFileName, String destinationFileName)
    {
        FileReader sourceStream = null;
        FileOutputStream destinationStream = null;
        try
        {
            sourceStream = new FileReader(sourceAbsoluteFileName);
            if(sourceStream.getState() == FileReader.State.ReadyToRead)
            {
                destinationStream = App.getContext().openFileOutput(destinationFileName, Context.MODE_PRIVATE);

                do
                {
                    byte[] readedData=sourceStream.read();
                    if(readedData.length!=0)
                        destinationStream.write(readedData);
                }
                while (sourceStream.getState() == FileReader.State.ReadyToRead);

                return sourceStream.getState() == FileReader.State.Final;
            }

            return false;
        }
        catch(Exception ex)
        {
            Log.e("CV", "exception", ex);
            return false;
        }
        finally
        {
            try
            {
                if(destinationStream!=null)
                    destinationStream.close();

                if(sourceStream!=null)
                    sourceStream.close();
            }
            catch (IOException ex)
            {
                Log.e("CV", "exception", ex);
                return false;
            }
        }
    }

    /**
     * @param destinationFileName - destination file name WITHOUT PATH (will createFromFile in private area)
     * @param quality - see com.syleiman.comicsviewer.Common.Helpers.Files.BitmapsQuality constants
     * @return true - if file created successfully
     */
    public static boolean createFromBitmap(
            String destinationFileName,
            Bitmap bitmap,
            int quality,
            Bitmap.CompressFormat compressFormat)
    {
        FileOutputStream destinationStream = null;
        try
        {
            destinationStream = App.getContext().openFileOutput(destinationFileName, Context.MODE_PRIVATE);
            bitmap.compress(compressFormat, quality, destinationStream);
            return true;
        }
        catch(Exception ex)
        {
            Log.e("CV", "exception", ex);
            return false;
        }
        finally
        {
            try
            {
                if(destinationStream!=null)
                    destinationStream.close();
           }
            catch (IOException ex)
            {
                Log.e("CV", "exception", ex);
                return false;
            }
        }
    }

    /**
     * @param sourceFileName - name of file in private area (without path)
     * @return bytes of file as array or null if reas was unsuccessed
     */
    public static byte[] read(String sourceFileName) throws IOException
    {
        FileReader sourceStream = null;
        ByteArrayOutputStream destinationStream=null;
        try
        {
            sourceStream = new FileReader(App.getContext().openFileInput(sourceFileName));
            if(sourceStream.getState() == FileReader.State.ReadyToRead)
            {
                destinationStream=new ByteArrayOutputStream((int)sourceStream.getSize());
                do
                {
                    byte[] readedData=sourceStream.read();
                    if(readedData.length!=0)
                        destinationStream.write(readedData);
                }
                while (sourceStream.getState() == FileReader.State.ReadyToRead);

                if(sourceStream.getState() == FileReader.State.Final)
                    return destinationStream.toByteArray();
            }

            return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if(sourceStream!=null)
                sourceStream.close();

            if(destinationStream!=null)
                destinationStream.close();
        }
    }

    /**
     * Delete file in private area
     * @param fileName WITHOUT path
     * @return true if file was deleted
     */
    public static boolean delete(String fileName)
    {
        return App.getContext().deleteFile(fileName);
    }

    /**
     * Get full name of file in private area
     * @param fileName - name of file without path
     */
    public static String getFullName(String fileName)
    {
        return App.getContext().getFilesDir().getAbsolutePath()+"/"+fileName;
    }
}