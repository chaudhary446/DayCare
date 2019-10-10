package com.daycare.download;

import com.crashlytics.android.Crashlytics;

public class CrashUtils
{
    public static void report(Throwable t)
    {
        Crashlytics.logException(t);
    }
}