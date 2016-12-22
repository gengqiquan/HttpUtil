package com.sunshine.retrofit.utils;

import android.os.Handler;
import android.os.Looper;

import com.sunshine.retrofit.interfaces.Error;
import com.sunshine.retrofit.interfaces.Progress;
import com.sunshine.retrofit.interfaces.Success;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by gengqiquan on 2016/12/21.
 */

public class WriteFileUtil {
    public static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void writeFile(ResponseBody body, String path, Progress progress, Success mSuccessCallBack, Error mErrorCallBack) {
        File futureStudioIconFile = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        ProgressInfo progressInfo = new ProgressInfo();
        try {
            byte[] fileReader = new byte[4096];
            progressInfo.total = body.contentLength();
            progressInfo.read = 0;
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(futureStudioIconFile);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                progressInfo.read += read;
                mHandler.post(() -> progress.progress((float) progressInfo.read / progressInfo.total));
            }
            mSuccessCallBack.Success(path);
            outputStream.flush();
        } catch (IOException e) {
            mErrorCallBack.Error(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
            }

        }

    }

    static class ProgressInfo {
        public long read = 0;
        public long total = 0;
    }
}
