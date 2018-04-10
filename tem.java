package com.pitaya.daokoudai.common.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

public class DkdToast {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    static final String TAG = "BooheeToast";
    private static final Runnable mActivite = new Runnable() {
        public void run() {
            DkdToast.activeQueue();
        }
    };
    protected static AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private static Handler mHanlder = new Handler();
    private static BlockingQueue<DkdToast> mQueue = new LinkedBlockingQueue();
    final Context mContext;
    long mDuration;
    private final Runnable mHide = new Runnable() {
        public void run() {
            DkdToast.this.handleHide();
        }
    };
    private WindowManager.LayoutParams mParams;
    private final Runnable mShow = new Runnable() {
        public void run() {
            DkdToast.this.handleShow();
        }
    };
    View mView;
    private WindowManager mWM;
    private static DkdToast mToast;

    @SuppressWarnings("ResourceType")
    public DkdToast(Context context) {
        this.mContext = context;
        this.mParams = new WindowManager.LayoutParams();
        this.mParams.height = -2;
        this.mParams.width = -2;
        this.mParams.format = -3;
        this.mParams.windowAnimations = 16973828;
        this.mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        this.mParams.setTitle("Toast");
        this.mParams.flags = 152;
        this.mWM = (WindowManager) context.getSystemService("window");
        this.mParams.packageName = context.getPackageName();
    }

    public static DkdToast makeText(Context context, CharSequence text, int duration) {
        return new DkdToast(context).setText(text).setDuration(duration).setGravity(80, 0,
                ViewUiUtils.dp2px(context, 64.0f));
    }

    public static DkdToast makeText(Context context, int resId, int duration) throws
            Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public DkdToast setView(View view) {
        this.mView = view;
        return this;
    }

    public View getView() {
        return this.mView;
    }

    @TargetApi(17)
    public DkdToast setGravity(int gravity, int xOffset, int yOffset) {
        int finalGravity = gravity;
        this.mParams.gravity = finalGravity;
        if ((finalGravity & 7) == 7) {
            this.mParams.horizontalWeight = 1.0f;
        }
        if ((finalGravity & 112) == 112) {
            this.mParams.verticalWeight = 1.0f;
        }
        this.mParams.y = yOffset;
        this.mParams.x = xOffset;
        return this;
    }

    public DkdToast setDuration(int duration) {
        if (duration == 0) {
            this.mDuration = 2000;
        } else if (duration == 1) {
            this.mDuration = 3500;
        }
        return this;
    }

    public DkdToast setText(int resId) {
        setText(this.mContext.getText(resId));
        return this;
    }

    @SuppressWarnings("ResourceType")
    public DkdToast setText(CharSequence s) {
        View view = Toast.makeText(this.mContext, s, 0).getView();
        if (view != null) {
            ((TextView) view.findViewById(android.R.id.message)).setText(s);
            setView(view);
        }
        return this;
    }

    public void show() {
//        mQueue.offer(this);
        mToast = this;
        LogUtils.d("-----toast",mToast.toString());
        mHanlder.post(mActivite);
//        if (mAtomicInteger.get() == 0) {c
//            mAtomicInteger.incrementAndGet();
//            mHanlder.post(mActivite);
//        }
    }

    public void cancel() {

        if (!(mAtomicInteger.get() == 0 && mQueue.isEmpty()) && equals(mQueue.peek())) {
            mHanlder.removeCallbacks(mActivite);
            mHanlder.post(this.mHide);
            mHanlder.post(mActivite);
        }
    }

    private void handleShow() {
        if (this.mView != null) {
//            if (this.mView.getParent() != null) {
//                this.mWM.removeView(this.mView);
//                LogUtils.d("-----view",mView.getParent().toString()+"remove view");
//            }
            this.mWM.addView(this.mView, this.mParams);
            LogUtils.d("-----view show",mWM.toString()+"add view"+mView.toString());
        }
    }

    private void handleHide() {
        if (this.mView != null) {
            if (this.mView.getParent() != null) {
                this.mWM.removeView(this.mView);
//                mQueue.poll();
            }
            this.mView = null;
            LogUtils.d("-----view hide","hide");
        }
    }

    private static void activeQueue() {
//        DkdToast toast = mQueue.peek();
        if (mToast == null) {
//            mAtomicInteger.decrementAndGet();
            return;
        }
//        mHanlder.post(mToast.mHide);
        mHanlder.post(mToast.mShow);
        mHanlder.postDelayed(mToast.mHide, mToast.mDuration);
//        mHanlder.postDelayed(mActivite, mToast.mDuration);
    }
}
