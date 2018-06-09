package cn.bjzhou.douban.tv;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;


/**
 * @author liuyongkui.
 */
public class TcMouseManager implements TcMouseView.OnMouseListener {

    public static final int KEYCODE_UP = KeyEvent.KEYCODE_DPAD_UP;

    public static final int KEYCODE_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;

    public static final int KEYCODE_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;

    public static final int KEYCODE_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;

    public static final int KEYCODE_CENTER = KeyEvent.KEYCODE_DPAD_CENTER;
    public static final int KEYCODE_ENTER = KeyEvent.KEYCODE_ENTER;

    public static final int MOUSE_MOVE_STEP = 15;

    private WebView mParentView;

    private TcMouseView mMouseView;

    private boolean isKeyEventCousumed = false;

    private int mSpeed = 1;

    private static final int defTimes = 400;

    private static final int defMaxSpeed = 7;

    private long mLastEventTime;

    private int mMouseX = 0;
    private int mMouseY = 0;

    public TcMouseManager(WebView mParentView, TcMouseView mMouseView) {
        this.mParentView = mParentView;
        this.mMouseView = mMouseView;
        mMouseView.setOnMouseListener(this);
    }

    public boolean onDpadClicked(KeyEvent event) {
        if (event.getKeyCode() == KEYCODE_CENTER || event.getKeyCode() == KEYCODE_ENTER) {
            dispatchKeyEventToMouse(event);
        } else {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (!isKeyEventCousumed) {

                    if (event.getDownTime() - mLastEventTime < defTimes) {

                        if (mSpeed < defMaxSpeed) {
                            mSpeed++;
                        }
                    } else {
                        mSpeed = 1;
                    }
                }
                mLastEventTime = event.getDownTime();
                dispatchKeyEventToMouse(event);
                isKeyEventCousumed = true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                if (!isKeyEventCousumed) {
                    dispatchKeyEventToMouse(event);
                }
                isKeyEventCousumed = false;
            }
        }
        return true;
    }

    private void dispatchKeyEventToMouse(KeyEvent event) {
        if (event.getKeyCode() == KEYCODE_CENTER || event.getKeyCode() == KEYCODE_ENTER) {
            mMouseView.onCenterButtonClicked(event);
        } else {
            mMouseView.moveMouse(event, mSpeed);
        }
    }

    public void sendCenterClickEvent(int x, int y, int action) {
        sendMotionEvent(x, y, action);
    }

    @SuppressLint("InlinedApi")
    public void sendMouseHoverEvent(KeyEvent event, int times) {
        int mMoveDis = times * MOUSE_MOVE_STEP;
        int contentHeight = (int) (mParentView.getContentHeight() * mParentView.getScale());
        int scrollY = mParentView.getScrollY();
        int scroll;
        switch (event.getKeyCode()) {
            case TcMouseManager.KEYCODE_UP:
                mMouseY = (mMouseY - mMoveDis > 0) ? mMouseY - mMoveDis : 0;
                if (scrollY >= mMouseY) {
                    scroll = scrollY - mMoveDis < 0 ? 0 : scrollY - mMoveDis;
                    mParentView.scrollTo(0, scroll);
                }
                break;
            case TcMouseManager.KEYCODE_LEFT:
                mMouseX = (mMouseX - mMoveDis > 0) ? mMouseX - mMoveDis : 0;
                break;
            case TcMouseManager.KEYCODE_DOWN:
                mMouseY = (mMouseY + mMoveDis < contentHeight - TcMouseManager.MOUSE_MOVE_STEP) ? mMouseY + mMoveDis : contentHeight - TcMouseManager.MOUSE_MOVE_STEP;
                if (mMouseY >= mParentView.getMeasuredHeight()) {
                    scroll = scrollY + mMoveDis > contentHeight - mParentView.getMeasuredHeight() ? contentHeight - mParentView.getMeasuredHeight() : scrollY + mMoveDis;
                    mParentView.scrollTo(0, scroll);
                }
                break;
            case TcMouseManager.KEYCODE_RIGHT:
                mMouseX = (mMouseX + mMoveDis < mParentView.getMeasuredWidth() - TcMouseManager.MOUSE_MOVE_STEP) ? mMouseX + mMoveDis : mParentView.getMeasuredWidth() - TcMouseManager.MOUSE_MOVE_STEP;
                break;
        }
        sendMotionEvent(mMouseX, mMouseY, MotionEvent.ACTION_HOVER_MOVE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @SuppressLint("NewApi")
    private void sendMotionEvent(int x, int y, int action) {
        MotionEvent motionEvent = getMotionEvent(x, y, action);
        if (action == MotionEvent.ACTION_HOVER_MOVE) {
            motionEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
            mParentView.dispatchGenericMotionEvent(motionEvent);
        } else {
            mParentView.dispatchTouchEvent(motionEvent);
        }
    }

    private MotionEvent getMotionEvent(int x, int y, int action) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        int metaState = 0;
        return MotionEvent.obtain(
                downTime,
                eventTime,
                action,
                x,
                y,
                metaState
        );
    }

    @Override
    public boolean onclick(View v, KeyEvent et) {
        return onDpadClicked(et);
    }
}