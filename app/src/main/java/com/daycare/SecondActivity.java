package com.daycare;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daycare.cctvplayer.CCTVActivity;
import com.daycare.download.DocumentActivity;
import com.onesignal.OneSignal;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import notification.MyApplication;


public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String TAG = SecondActivity.class.getSimpleName();
    private WebView webView;
    private WebSettings webSettings;
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private long size = 0;
    String BASE_URL="http://demo.sparxholidays.com";
    String username,password;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView tv_apppVersion;
    String version;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        sharedpreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String manufacturer = Build.MANUFACTURER;
        String androidVersion = Build.VERSION.RELEASE;
        String modelName = Build.MODEL;

        sendData(deviceId,manufacturer,modelName,androidVersion);

        verifyStoragePermissions(this);
        try {
            getCookieFromAppCookieManager(BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        CookieSyncManager.createInstance(this);




        JSONObject tags = new JSONObject();
        try {
            tags.put("username", "ram");
            tags.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);
        //senAllsubscribers();


        tv_apppVersion=(TextView)findViewById(R.id.tv_appVersion);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View v = navigationView.getHeaderView(0);


        webView = (WebView) findViewById(R.id.webView1);
        webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setHttpAuthUsernamePassword(BASE_URL,"", "@"+username, password);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);

        //webView.addJavascriptInterface(new JSInterface(this), "JSInterface");
        webView.addJavascriptInterface(new JSInterface(this,webView), "MyHandler");

        this.webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new PQClient());
        webView.setWebChromeClient(new PQChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.loadUrl(BASE_URL);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            tv_apppVersion.setText("v "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url+"?name="+username+"&password="+password);
                view.loadUrl(url);

                if(url.startsWith("whatsapp://")){
                    Uri uri=Uri.parse(url);
                    String msg = uri.getQueryParameter("text");
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                    return true;
                }

                if (url.contains("mailto:")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });



        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {

                    String fileName1 = url.substring( url.lastIndexOf('/')+1, url.length() );
                    String fileNameWithoutExtn = fileName1.substring(0, fileName1.lastIndexOf('.'));
                    String s[] = url.split("/");

                    for(String homework: s) {
                        if(homework.equals("HomeworkAttacheFile"))
                        {
                            if(url.endsWith(".xlsx"))
                            {
                                //downloadExcel(url,"HomeWork");
                                downloadFile(url,"HomeWork",".xlsx","DayCare Document");
                            }

                            if(url.endsWith(".pdf"))
                            {
                                downloadPdf(url,"HomeWork",fileNameWithoutExtn);
                                //downloadFile(url,"HomeWork",".pdf","DayCare Document");
                            }

                            if(url.endsWith(".jpg"))
                            {
                                //downloadImage(url,"HomeWork");
                                downloadFile(url,"HomeWork",".jpg","DayCare Document");
                            }
                        }

                        if(homework.equals("MyDocAttachFile"))
                        {
                            if(url.endsWith(".xlsx"))
                            {
                                //downloadExcel(url,"Document");
                                downloadFile(url,"Document",".xlsx","DayCare Document");
                            }

                            if(url.endsWith(".pdf"))
                            {
                                downloadPdf(url,"Document",fileNameWithoutExtn);
                                //downloadFile(url,"Document",".pdf","DayCare Document");
                            }

                            if(url.endsWith(".jpg"))
                            {
                                //downloadImage(url,"Document");
                                downloadFile(url,"Document",".jpg","DayCare Image");
                            }

                            if(url.endsWith(".jpeg"))
                            {
                                //downloadImage(url,"Document");
                                downloadFile(url,"Document",".jpeg","DayCare Image");
                            }
                            if(url.endsWith(".docx"))
                            {
                                //downloadDoc(url,"Document");
                                downloadFile(url,"Document",".docx","DayCare Document");
                            }
                        }


                        if(homework.equals("ePhotoAlbumImages")){
                            //downloadImage(url,"Photos");
                            downloadFile(url,"Photos",".jpg","DayCare Image");
                        }

                        if(homework.equals("EMagazineAttacheFile"))
                        {
                            if(url.endsWith(".xlsx"))
                            {
                                //downloadExcel(url,"Circular");
                                downloadFile(url,"Circular",".xlsx","DayCare Document");
                            }

                            if(url.endsWith(".pdf"))
                            {
                                downloadPdf(url,"Circular",fileNameWithoutExtn);
                                //downloadFile(url,"Circular",".pdf","DayCare Document");
                            }

                            if(url.endsWith(".jpg"))
                            {
                                //downloadImage(url,"Circular");
                                downloadFile(url,"Circular",".jpg","DayCare Document");
                            }
                        }

                        if(homework.equals("TimeTable"))
                        {
                            if(url.endsWith(".xlsx"))
                            {
                                //downloadExcel(url,"TimeTable");
                                downloadFile(url,"TimeTable",".xlsx","DayCare Document");
                            }

                            if(url.endsWith(".pdf"))
                            {
                                downloadPdf(url,"TimeTable",fileNameWithoutExtn);
                                //downloadFile(url,"TimeTable",".pdf","DayCare Document");
                            }

                            if(url.endsWith(".jpg"))
                            {
                                //downloadImage(url,"TimeTable");
                                downloadFile(url,"TimeTable",".jpg","DayCare Document");
                            }

                            if(url.endsWith(".xlsx"))
                            {
                                //downloadExcel(url,"TimeTable");
                                downloadFile(url,"TimeTable",".xlsx","DayCare Document");
                            }
                        }
                    }
                }catch (Exception e){
                }

            }
        });
    }





    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName,  /* prefix */".jpg",         /* suffix */storageDir      /* directory */
        );
        return imageFile;
    }

    public class PQChromeClient extends WebChromeClient {
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            Log.e("FileCooserParams => ", filePath.toString());
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            String[] mimeTypes =
                    {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                            "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                            "text/plain",
                            "application/pdf",
                            "application/zip"};

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                contentSelectionIntent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                contentSelectionIntent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
            }


            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[2];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);
            return true;

        }
    }


    public class PQClient extends WebViewClient {
        ProgressDialog progressDialog;
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String webUrl = webView.getUrl();
            if(url != null && url.startsWith("whatsapp://"))
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Moon TV");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);return true;
            }


            if (url.endsWith(".pdf")) {
                Toast.makeText(SecondActivity.this, "Type", Toast.LENGTH_SHORT).show();
                Uri source = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(source);
                request.setDescription("Description for the DownloadManager Bar");
                request.setTitle("YourApp.pdf");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SmartPigs.pdf");
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }


            if (url.contains("mailto:")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else {
                view.loadUrl(url);
                return true;
            }
        }

        //Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(SecondActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.hide();
            }
        }



        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {

            if (url.startsWith(BASE_URL))
            {
                webView.loadUrl("http://demo.sparxholidays.com/Admin/Home");
            }


            webView.loadUrl("javascript:(function(){ " +
                    "document.getElementById('android-app').style.display='none';})()");
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,Manifest.permission.READ_CALL_LOG
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        try {
            String file_path = mCameraPhotoPath.replace("file:","");
            File file = new File(file_path);
            size = file.length();

        }catch (Exception e){
            Log.e("Error!", "Error while opening image file" + e.getLocalizedMessage());
        }

        if (data != null || mCameraPhotoPath != null) {
            Integer count = 0; //fix fby https://github.com/nnian
            ClipData images = null;
            try {
                images = data.getClipData();
            }catch (Exception e) {
                Log.e("Error!", e.getLocalizedMessage());
            }

            if (images == null && data != null && data.getDataString() != null) {
                count = data.getDataString().length();
            } else if (images != null) {
                count = images.getItemCount();
            }
            Uri[] results = new Uri[count];
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else if (data.getClipData() == null) {
                    results = new Uri[]{Uri.parse(data.getDataString())};
                } else {

                    for (int i = 0; i < images.getItemCount(); i++) {
                        results[i] = images.getItemAt(i).getUri();
                    }
                }
            }


            if(resultCode==RESULT_OK){
                //String PathHolder = data.getData().getPath();
                //Toast.makeText(SecondActivity.this, PathHolder , Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(results);
            mUploadMessage = null;
        }
    }



    public static void verifyStoragePermissions(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int calllog = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG);
        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED || calllog != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void downloadImage(String uRl,String folderName) {
        Toast.makeText(this, "Download Start", Toast.LENGTH_SHORT).show();
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+folderName);
        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("DayCare Image").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Download Progress...")
                .setDestinationInExternalPublicDir("/"+folderName, "test.jpg");
        mgr.enqueue(request);
    }



    public void downloadExcel(String uRl,String folderName) {
        Toast.makeText(this, "Download Start", Toast.LENGTH_SHORT).show();
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+folderName);
        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("DayCare Document").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Download Progress...")
                .setDestinationInExternalPublicDir("/"+folderName, "homeworkexcel.xlsx");
        mgr.enqueue(request);
    }


    public void downloadPdf(String uRl,String folderName,String fileName) {
        Toast.makeText(this, "Download Start", Toast.LENGTH_SHORT).show();
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+folderName);
        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("DayCare Document").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Download Progress...")
                .setDestinationInExternalPublicDir("/"+folderName, fileName+".pdf");
        mgr.enqueue(request);
    }

    public void downloadDoc(String uRl,String folderName) {
        Toast.makeText(this, "Download Start", Toast.LENGTH_SHORT).show();
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+folderName);
        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("DayCare Document").setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Download Progress...")
                .setDestinationInExternalPublicDir("/"+folderName, "test.docx");
        mgr.enqueue(request);
    }



    public void downloadFile(String url,String folderName,String extension,String downloadTitle) {
        Toast.makeText(this, "Download Start", Toast.LENGTH_SHORT).show();
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+folderName);
        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(downloadTitle).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Download Progress...")
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir("/"+folderName, "daycare"+extension);
        mgr.enqueue(request);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_home:
                Intent intent=new Intent(SecondActivity.this,SecondActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent,bndlanimation);
                break;

            case R.id.nav_photo:
                Intent photo=new Intent(SecondActivity.this,DownloadActivity.class);
                Bundle photoanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                photo.putExtra("folderName","Photos");
                startActivity(photo, photoanimation);
                break;

            case R.id.nav_homework:
                Intent homework=new Intent(SecondActivity.this, DocumentActivity.class);
                Bundle homeworkanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                homework.putExtra("folderName","HomeWork");
                startActivity(homework, homeworkanimation);
                break;

            case R.id.nav_circular:
                Intent circular=new Intent(SecondActivity.this, DocumentActivity.class);
                Bundle circularanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                circular.putExtra("folderName","Circular");
                startActivity(circular, circularanimation);
                break;

            case R.id.nav_document:
                Intent document=new Intent(SecondActivity.this, DocumentActivity.class);
                Bundle documentnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                document.putExtra("folderName","Document");
                startActivity(document, documentnimation);
                break;

            case R.id.nav_timetable:
                Intent timetable=new Intent(SecondActivity.this, DocumentActivity.class);
                Bundle timetablenimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
                timetable.putExtra("folderName","TimeTable");
                startActivity(timetable, timetablenimation);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public static String getCookieFromAppCookieManager(String url) throws MalformedURLException {
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager == null)
            return null;
        String rawCookieHeader = null;
        URL parsedURL = new URL(url);
        rawCookieHeader = cookieManager.getCookie(parsedURL.getHost());
        if (rawCookieHeader == null)
            return null;
        return rawCookieHeader;
    }

    public void senAllsubscribers()
    {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String jsonResponse;
            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic NjlkZDFmMWYtYTIzYi00ODU1LWI4OWQtZjU5ZjIyYzgyNzk1");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"8ca17a71-ae10-4121-9baf-a5d731faffa0\","
                    +   "\"included_segments\": [\"All\"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \"English Message\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().stopSync();
    }
    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().sync();
    }


    public void sendData(String deviceId,String manufacter,String modelName,String androidVersion)
    {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://api.sparxholidays.com/KTStandaloneAPI/Common/UserDeviceDetails";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Device_Id", deviceId);
            jsonBody.put("Identifier", "111");
            jsonBody.put("Device_OS", androidVersion);
            jsonBody.put("Device_Type", "Android");
            jsonBody.put("Device_Model", manufacter+modelName);
            jsonBody.put("Invalid_Identifier", "AS");
            jsonBody.put("UserName", "asa");
            jsonBody.put("BranchId", "asaS");
            jsonBody.put("SchoolId", "asa");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY1", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class JSInterface {

        Context mContext;
        JSInterface(Context c,WebView webView) {
            mContext = c;
        }
        @JavascriptInterface
        public List<String> GetPhoneCallStatus()
        {
            List<String> listA = new ArrayList();
            listA.add("Ram");
            listA.add("Ram");
            listA.add("Ram");
            listA.add("Ram");
            listA.add("Ram");
            listA.add("Ram");
            listA.add("Ram");
            return listA;
        }


        @JavascriptInterface
        public void login(String UserName,String Password)
        {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("USERNAME", UserName);
            editor.putString("PASS", Password);
            editor.commit();
        }

        @JavascriptInterface
        public void logOut()
        {
            Toast.makeText(mContext, "Log", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences=getSharedPreferences("LOGIN",0);
            sharedPreferences.edit().remove("USERNAME").commit();
            sharedPreferences.edit().remove("PASS").commit();
        }


        @JavascriptInterface
        public String sendUserId()
        {
            SharedPreferences sp = getSharedPreferences("LOGIN", MODE_PRIVATE);
            String name = sp.getString("USERNAME", null);
            String pass = sp.getString("PASS", null);
            System.out.println("===username==="+name+"==="+pass);
            if(name.equals(null))
            {
                System.out.println("===if===");
                return "1";
            }else{
                System.out.println("===else===");
                return name+","+pass;
            }

        }



        @JavascriptInterface
        public void opencctv(String url)
        {
            Intent homework=new Intent(SecondActivity.this, CCTVActivity.class);
            Bundle homeworkanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim_right_to_left,R.anim.anim_left_to_right).toBundle();
            homework.putExtra("url",url);
            startActivity(homework, homeworkanimation);
        }
    }
}
