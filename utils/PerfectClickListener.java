package com.fta.myapplication.databingpak.utils;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.Calendar;

/**
 * 文件描述： 避免重复点击
 * 作者： Created by fta on 2017/4/22
 * 来源：
 */

public abstract class PerfectClickListener implements OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    private int id = -1;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int mId = v.getId();
        if (id != mId) {
            id = mId;
            lastClickTime = currentTime;
            onNoDoubleClick(v);
            return;
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);
}
