package com.aaxena.takenotes;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
    private Context context;
    private DBHandler dbHandler;

    public WebAppInterface(Context context)
    {
     this.context = context;
    }
    @JavascriptInterface
    public void showToast(String message)
    {
        dbHandler = new DBHandler(context);
        dbHandler.addNewCourse(message.trim());
    }
}
