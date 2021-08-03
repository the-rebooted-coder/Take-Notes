package com.aaxena.takenotes;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterfaceDraft {
    private Context context;
    private DraftDB draftDB;

    public WebAppInterfaceDraft(Context context)
    {
     this.context = context;
    }
    @JavascriptInterface
    public void saveDraft(String draft)
    {
        if (!draft.isEmpty()) {
            draftDB = new DraftDB(context);
            draftDB.addNewCourse(draft);
        }
    }
}
