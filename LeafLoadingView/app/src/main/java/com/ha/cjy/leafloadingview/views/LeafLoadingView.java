package com.ha.cjy.leafloadingview.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ha.cjy.leafloadingview.R;
import com.ha.cjy.leafloadingview.utils.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 叶子加载视图
 * 1、构造函数（初始化）
 * 2、onMeasure（测量大小）
 * 3、onSizeChanged（确定大小）
 * 4、onLayout(位置，当view包含子View时）
 * 5、onDraw（绘制）
 * 6、暴露接口给外部
 *
 * 叶子飘动参考：https://github.com/Ajian-studio/GALeafLoading/blob/master/app/src/main/java/com/gastudio/leafloading/LeafLoadingView.java
 * Created by cjy on 17/8/21.
 */

public class LeafLoadingView extends View {
    // 叶子飘动一个周期所花的时间
    private final long LEAF_FLOAT_TIME = 2000;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;
    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;

    private Context mContext;
    private Paint mBgPaint;
    private Paint mProgressPaint;
    private Paint mFanPaint;
    private Paint mLeafPaint;
    private int mProgressColor = Color.rgb(255,168,0);
    //控件宽，高
    private int mWidth;
    private int mHeight;
    //中心坐标
    private int mCenterX;
    private int mCenterY;
    //背景图片宽、高
    private int mPicWidth;
    private int mPicHeight;

    //进度
    private float mProgress = 0;
    //进度宽度
    private float mProgressWidth;
    //总进度宽度
    private float mTotalProgressWidth;
    //旋转角度
    private int mAngle = 60;
    /**
     * 叶子
     */
    private List<Leaf> mLeafInfos;
    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;
    private int mLeafWidth;
    private int mLeafHeight;
    private Bitmap mLeafBitmap;
    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;
    // 用于控制随机增加的时间不抱团
    private int mAddTime;

    public LeafLoadingView(Context context) {
        super(context);
        initView(context);
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;

        mBgPaint = new Paint();

        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mProgressPaint.setStrokeWidth(2);

        mFanPaint = new Paint();
        mFanPaint.setColor(Color.WHITE);
        mFanPaint.setTextSize(28);

        mLeafPaint = new Paint();

        //去掉硬件加速，去除黑色背景问题
        setLayerType(LAYER_TYPE_SOFTWARE,null);

        LeafFactory factory = new LeafFactory();
        mLeafInfos = factory.generateLeafs();

        mLeafBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.leaf);
        mLeafWidth = mLeafBitmap.getWidth();
        mLeafHeight = mLeafBitmap.getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = w/2;
        mCenterY = h/2;
        mProgressPaint.setColor(mProgressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制背景图
        drawBackground(canvas);
        //绘制进度
        drawProgress(canvas);
        //绘制风扇
        drawFan(canvas);
        //绘制叶子
        drawLeaf(canvas);
    }

    private void drawBackground(Canvas canvas){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.leaf_kuang);
        mPicWidth = bitmap.getWidth();
        mPicHeight = bitmap.getHeight();
        canvas.drawBitmap(bitmap,0,0,mBgPaint);

        mTotalProgressWidth = mPicWidth - 76;
    }

    private void drawProgress(Canvas canvas){
        mProgressWidth = mProgress/100 * mTotalProgressWidth;
        RectF rectF = new RectF();
        rectF.left = 16;
        rectF.top = 16;
        rectF.right = mProgressWidth;
        rectF.bottom = mPicHeight-16;

        float[] radius = new float[8];
        radius[0] = 40;
        radius[1] = 40;
        radius[2] = 0;
        radius[3] = 0;
        radius[4] = 0;
        radius[5] = 0;
        radius[6] = 40;
        radius[7] = 40;

        Path path = new Path();
        path.addRoundRect(rectF,radius, Path.Direction.CW);
        //SRC 上层 DST 下层
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));//设置图片混合显示效果
        canvas.drawPath(path,mProgressPaint);
    }

    private void drawFan(Canvas canvas){
        int centerX = (int) mTotalProgressWidth;
        int centerY = 8;
        if(mProgress == 100){
            String text = "100%";
            canvas.drawText(text,centerX,mPicHeight/2+getTextHeight(text)/2,mFanPaint);
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.fengshan);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            Matrix matrix = new Matrix();
            matrix.setTranslate(centerX, centerY);     //设置图片的原点坐标
            if (this.mProgress >= 95 && this.mProgress < 100){
                float scale = Math.abs(this.mProgress - 100) * 0.2f;
                //缩放 参数1：X轴缩放倍数，参数2：Y轴缩放倍数 参数3，4：缩放中心点
                matrix.preScale(scale,scale,(float)bitmapWidth/2, (float)bitmapHeight/2);
            }else{
                //旋转 参数1：角度，参数2，3：旋转中心点
                matrix.preRotate(mAngle, (float)bitmapWidth/2, (float)bitmapHeight/2);
            }
            canvas.drawBitmap(bitmap, matrix, mFanPaint);
            if (this.mProgress != 100){
                mAngle += 60;
            }
        }
    }

    /**
     * 绘制叶子
     *
     * @param canvas
     */
    private void drawLeaf(Canvas canvas) {
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = leaf.x;
                float transY = leaf.y;
                Log.e("(x,y)=","（"+transX+","+transY+"）");
                if (transX > mProgressWidth) {
                    matrix.postTranslate(transX, transY);
                    // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                    float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                            / (float) mLeafRotateTime;
                    int angle = (int) (rotateFraction * 360);
                    // 根据叶子旋转方向确定叶子旋转角度
                    int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                            + leaf.rotateAngle;
                    matrix.postRotate(rotate, transX
                            + mLeafWidth / 2, transY + mLeafHeight / 2);
                    canvas.drawBitmap(mLeafBitmap, matrix, mLeafPaint);
                    canvas.restore();
                }
            } else {
                continue;
            }
        }
    }

    /**
     * 获取叶子的x,y
     * @param leaf
     * @param currentTime
     */
    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis()
                    + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (mTotalProgressWidth - mTotalProgressWidth * fraction);
        leaf.y = getLocationY(leaf);
    }

    // 通过叶子信息获取当前叶子的Y值
    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) ((float) 2 * Math.PI / mTotalProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 大振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) (a * Math.sin(w * leaf.x)) + 40 * 2 / 3;//40是圆角半径
    }

    public enum StartType {
        LITTLE, MIDDLE, BIG
    }

    /**
     * 叶子对象，用来记录叶子主要数据
     *
     */
    private class Leaf {
        // 在绘制部分的位置
        float x, y;
        // 控制叶子飘动的幅度
        StartType type;
        // 旋转角度
        int rotateAngle;
        // 旋转方向--0代表顺时针，1代表逆时针
        int rotateDirection;
        // 起始时间(ms)
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        // 生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(3);
            // 随时类型－ 随机振幅
            StartType type = StartType.MIDDLE;
            switch (randomType) {
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }
            leaf.type = type;
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            // 随机旋转方向（顺时针或逆时针）
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        // 根据最大叶子数产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(float progress){
        mProgress = progress;
        invalidate();
    }

    /**
     * 进度框颜色
     * @param color
     */
    public void setProgressColor(int color){
        this.mProgressColor = color;
    }

    /**
     * 设置中等幅度值
     * @param amplitude
     */
    public void setAmplitude(int amplitude){
        this.mMiddleAmplitude = amplitude;
    }

    /**
     * 设置幅度差
     * @param amplitudeDisparity
     */
    public void setAmplitudeDisparity(int amplitudeDisparity){
        this.mAmplitudeDisparity = amplitudeDisparity;
    }

    /**
     * 获取文本的高度
     * @param text
     * @return
     */
    private int getTextHeight(String text){
        Rect bounds = new Rect();
        mFanPaint.getTextBounds(text,0,text.length(),bounds);
        int height = bounds.bottom + bounds.height();
        return height;
    }
}
