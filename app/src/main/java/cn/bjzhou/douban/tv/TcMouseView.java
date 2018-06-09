package cn.bjzhou.douban.tv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import cn.bjzhou.douban.R;

/**
 * @author liuyongkui
 */
public class TcMouseView extends FrameLayout {

    private ImageView mMouseView;

    private Bitmap mMouseBitmap;

    private TcMouseManager mMouseManager;

    private int mMouseX = 0;
    private int mMouseY = 0;

    private int mMoveDis = TcMouseManager.MOUSE_MOVE_STEP;

    private OnMouseListener mOnMouseListener;

    public TcMouseView(Context context) {
        this(context, null);
    }

    public TcMouseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TcMouseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnMouseListener(OnMouseListener mOnMouseListener) {
        this.mOnMouseListener = mOnMouseListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMouseView != null && mMouseBitmap != null) {
            mMouseView.measure(MeasureSpec.makeMeasureSpec(mMouseBitmap.getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mMouseBitmap.getHeight(), MeasureSpec.EXACTLY));
        }
    }

    public void init(WebView parent) {
        mMouseManager = new TcMouseManager(parent, this);
        Drawable drawable = getResources().getDrawable(R.drawable.shubiao);
        mMouseBitmap = drawableToBitmap(drawable);
        mMouseView = new ImageView(getContext());
        mMouseView.setImageBitmap(mMouseBitmap);
        addView(mMouseView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (mMouseView != null) {
            mMouseView.layout(mMouseX, mMouseY, mMouseX + mMouseView.getMeasuredWidth(), mMouseY + mMouseView.getMeasuredHeight());
        }
    }

    public void onCenterButtonClicked(KeyEvent event) {
        mMouseManager.sendCenterClickEvent(mMouseX + TcMouseManager.MOUSE_MOVE_STEP, mMouseY + TcMouseManager.MOUSE_MOVE_STEP, event.getAction());
    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 50, 50, true);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("dispatchKeyEvent",
                "dispatchKeyEvent(), action=" + event.getAction() + " keycode="
                        + event.getKeyCode());
        switch (event.getKeyCode()) {
            case TcMouseManager.KEYCODE_UP:
            case TcMouseManager.KEYCODE_DOWN:
            case TcMouseManager.KEYCODE_LEFT:
            case TcMouseManager.KEYCODE_RIGHT:
            case TcMouseManager.KEYCODE_CENTER:
            case TcMouseManager.KEYCODE_ENTER:
                if (mOnMouseListener != null) {
                    return mOnMouseListener.onclick(TcMouseView.this, event);
                }
            default:
                break;
        }
        return super.dispatchKeyEvent(event);

    }

    public void moveMouse(KeyEvent event, int times) {
        Log.d("BdMainView", "wrapper moveMouse() ENTER");
        mMoveDis = times * TcMouseManager.MOUSE_MOVE_STEP;
        switch (event.getKeyCode()) {
            case TcMouseManager.KEYCODE_UP:
                mMouseY = (mMouseY - mMoveDis > 0) ? mMouseY - mMoveDis : 0;
                break;
            case TcMouseManager.KEYCODE_LEFT:
                mMouseX = (mMouseX - mMoveDis > 0) ? mMouseX - mMoveDis : 0;
                break;
            case TcMouseManager.KEYCODE_DOWN:
                mMouseY = (mMouseY + mMoveDis < getMeasuredHeight() - TcMouseManager.MOUSE_MOVE_STEP) ? mMouseY + mMoveDis : getMeasuredHeight() - TcMouseManager.MOUSE_MOVE_STEP;
                break;
            case TcMouseManager.KEYCODE_RIGHT:
                mMouseX = (mMouseX + mMoveDis < getMeasuredWidth() - TcMouseManager.MOUSE_MOVE_STEP) ? mMouseX + mMoveDis : getMeasuredWidth() - TcMouseManager.MOUSE_MOVE_STEP;
                break;
        }
        requestLayout();
        mMouseManager.sendMouseHoverEvent(event, times);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onDpadClicked(@NotNull KeyEvent event) {
        return mMouseManager.onDpadClicked(event);
    }

    /**
     * @author liuyongkui
     */
    public interface OnMouseListener {
        boolean onclick(View v, KeyEvent event);
    }


}