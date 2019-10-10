package com.daycare.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.daycare.DownloadActivity;
import com.daycare.R;
import com.daycare.SecondActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DocumentActivity extends AppCompatActivity {

    private ButtonBar buttonBar;
    private StorageFragment storageFragment = null;
    private final Stack<FolderFragment> fragments = new Stack<>();
    private final Clipboard clipboard = new Clipboard();
    ToolBar toolBar;
    String folderName;
    TextView tv_toolBar;

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);


        this.buttonBar = new ButtonBar(findViewById(R.id.buttonBar), fragments);
        String[] storages = storages();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                folderName= null;
            } else {
                folderName= extras.getString("folderName");
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        setTitle(folderName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DocumentActivity.this, SecondActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent,bndlanimation);
            }
        });

        if (storages.length > 1)
        {
            storageFragment = StorageFragment.newInstance(storages);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainer, storageFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        else
        {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            FolderFragment folderFragment = FolderFragment.newInstance(root,folderName);
            addFragment(folderFragment, false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
            {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    if (storageFragment != null)
                    {
                        storageFragment.reload();
                    }
                    else
                    {
                        FolderFragment folderFragment = fragments.peek();
                        folderFragment.refreshFolder();
                    }
                }
                else
                {
                    finish();
                }
            }
        }
    }

    private String[] storages()
    {
        List<String> storages = new ArrayList<>();

        try
        {
            File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);
            String base = String.format("/Android/data/%s/files", getPackageName());
            for (File file : externalStorageFiles)
            {
                try
                {
                    if (file != null)
                    {
                        String path = file.getAbsolutePath();
                        if (path.contains(base))
                        {
                            String finalPath = path.replace(base, "");
                            if (validPath(finalPath))
                            {
                                storages.add(finalPath);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    CrashUtils.report(e);
                }
            }
        }
        catch (Exception e)
        {
            CrashUtils.report(e);
        }

        String[] result = new String[storages.size()];
        storages.toArray(result);

        return result;
    }

    private boolean validPath(String path)
    {
        try
        {
            StatFs stat = new StatFs(path);
            stat.getBlockCount();
            return true;
        }
        catch (Exception e)
        {
            CrashUtils.report(e);
            return false;
        }
    }

    public void addFragment(FolderFragment fragment, boolean addToBackStack)
    {
        fragments.push(fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (addToBackStack)
        {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        }

        transaction.add(R.id.fragmentContainer, fragment);
        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    private void removeFragment(FolderFragment fragment)
    {
        fragments.pop();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();

        if (!fragments.isEmpty())
        {
            FolderFragment topFragment = fragments.peek();
            topFragment.refreshFolder();
        }
    }

    public Clipboard clipboard()
    {
        return clipboard;
    }

    public ButtonBar buttonBar()
    {
        return buttonBar;
    }

    @Override
    public void onBackPressed()
    {
        if (fragments.size() > 0)
        {
            FolderFragment fragment = fragments.peek();

            if (fragment.onBackPressed())
            {
                if (storageFragment == null)
                {
                    if (fragments.size() > 1)
                    {
                        removeFragment(fragment);
                    }
                    else
                    {
                        finish();
                    }
                }
                else
                {
                    removeFragment(fragment);

                    if (fragments.isEmpty())
                    {
                        buttonBar.displayButtons(0, false, false, false, false);
                    }
                }
            }
        }
        else
        {
            finish();
        }
    }

    @Override
    @SuppressLint("MissingSuperCall")
    protected void onSaveInstanceState(Bundle outState)
    {
        // no call for super(). Bug on API Level > 11.
    }
}
