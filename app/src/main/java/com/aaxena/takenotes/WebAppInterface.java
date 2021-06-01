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
        dbHandler = new DBHandler(context);
        dbHandler.addNewCourse(message.trim());
        Toast.makeText(context, "History has been created!.", Toast.LENGTH_SHORT).show();
    }
}
