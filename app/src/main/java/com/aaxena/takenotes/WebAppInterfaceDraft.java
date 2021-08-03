package com.aaxena.takenotes;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterfaceDraft {
    private Context context;

    public WebAppInterfaceDraft(Context context)
    {
     this.context = context;
    }
    @JavascriptInterface
    public void saveDraft(String draft)
    {
        if (!draft.isEmpty()) {
        }
    }
}
