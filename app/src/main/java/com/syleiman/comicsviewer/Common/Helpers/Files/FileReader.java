package com.syleiman.comicsviewer.Common.Helpers.Files;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Read data from file by portions
 */
public class FileReader
{
    public enum State
    {
        /**
         * Ready to read new portion of data
         */
        ReadyToRead,

        /**
         * All data was readed
         */
        Final,

        /**
         * Ther was an error whire reading
         */
        Error
    }

        private BufferedInputStream inputStream;

    private final int BUFFER_SIZE=4096;

    private State state;
    public State getState() { return state; }

    private long size;
    /**
     * Get total size of file in bytes
     */
    public long getSize() { return size; }

    public FileReader(String fileName) throws FileNotFoundException
    {
        this(new FileInputStream(fileName));
    }

    public FileReader(FileInputStream fileStream)
    {
        try
        {
            size=fileStream.getChannel().size();

            inputStream = new BufferedInputStream(fileStream);
            state = State.ReadyToRead;
        }
        catch (Exception e)
        {
            state = State.Error;
            e.printStackTrace();
        }
    }

    /**
     * Reading next portion of data
     * @return readed data - not null, but must be checked on emptiness
     */
    public byte[] read()
    {
        if(state != State.ReadyToRead)
            return getEmptyBuffer();

        try
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int totalRead=inputStream.read(buffer);

            if(totalRead==-1)
                state = State.Final;

            return buffer;
        }
        catch (Exception e)
        {
            state = State.Error;
            e.printStackTrace();
            close();

            return getEmptyBuffer();
        }
    }

    /**
     * Must call when finishing working with file
     */
    public void close()
    {
        try
        {
            if(inputStream!=null) {
                inputStream.close();
                inputStream = null;

                state = State.Final;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            state = State.Final;
        }
    }

    private byte[] getEmptyBuffer()
    {
        return new byte[0];
    }
}
