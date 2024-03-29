package com.daycare.download;

import android.widget.TextView;

import com.daycare.R;

public class ToolBar
{
    private final TextView folderName;

    public ToolBar(TextView textview)
    {
        this.folderName = textview;
    }

    public void update(FolderFragment fragment)
    {
        updateTitle(fragment.folderName());
    }

    public void update(String title)
    {
        updateTitle(title);
    }

    private void updateTitle(String text)
    {
        try
        {
            folderName.setText(text);
        }
        catch (Exception e)
        {
            CrashUtils.report(e);

            folderName.setText(R.string.app_name);
        }
    }
}