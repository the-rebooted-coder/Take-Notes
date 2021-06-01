package com.aaxena.takenotes;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
        if (!message.isEmpty()) {
            dbHandler = new DBHandler(context);
            dbHandler.addNewCourse(message.trim());
        }
        else {
            Toast.makeText(context,"Add Something to the Page",Toast.LENGTH_SHORT).show();
        }
    }
}
