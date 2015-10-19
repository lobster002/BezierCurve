package com.sky.beziercurve;

import java.util.Random;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BezierCurve extends RelativeLayout {
	private Drawable sources[] = null;
	private int sWidth;
	private int sHeight;
	private int mWidth;
	private int mHeight;
	private Random mRandom = null;
	private LayoutParams params = null;
	private Interpolator[] interpolators = null;

	public BezierCurve(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 获取图片资源
		sources = new Drawable[2];
		sources[0] = getResources().getDrawable(R.drawable.blue_);
		sources[1] = getResources().getDrawable(R.drawable.red_);

		// 获取图片的实际宽高
		sWidth = sources[0].getIntrinsicWidth();
		sHeight = sources[0].getIntrinsicHeight();

		mRandom = new Random();

		params = new RelativeLayout.LayoutParams(sWidth, sHeight);
		params.addRule(CENTER_HORIZONTAL, TRUE);// 相对于父布局水平居中
		params.addRule(ALIGN_PARENT_BOTTOM, TRUE);// 相对于父布局底部
	
		interpolators = new Interpolator[4];
		interpolators[0] = new LinearInterpolator();
		interpolators[1] = new AccelerateInterpolator();
		interpolators[2] = new DecelerateInterpolator();
		interpolators[3] = new AccelerateDecelerateInterpolator();
		
	}

	public void add() {
		ImageView iv = new ImageView(getContext());
		int tmp = mRandom.nextInt(2);
		iv.setLayoutParams(params);
		iv.setImageDrawable(sources[tmp]);
		addView(iv);

		// 属性动画控制坐标
		AnimatorSet set = getAnimator(iv);
		set.start();
	}

	private AnimatorSet getAnimator(ImageView iv) {
		// 构造三个属性动画
		// 1. alpha

		// 透明度从0.1f 到1.0f 渐变
		ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0, 1f, 1.0f);

		// 2.Scale缩放
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 0, 2f,
				1.0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 0, 2f,
				1.0f);

		AnimatorSet enter = new AnimatorSet();// 动画集合
		enter.setDuration(500);// 设置时间
		enter.playTogether(alpha, scaleX, scaleY);// 同时执行
		enter.setTarget(iv);

		// 3.贝塞尔曲线(核心，不断修改Iv坐标--PointF（x，y）)
		ValueAnimator bezierValueAnimator = agetBezier(iv);
		AnimatorSet bezierSet = new AnimatorSet();
		bezierSet.playSequentially(enter, bezierValueAnimator);// 顺序执行
		bezierSet.setInterpolator(interpolators[mRandom.nextInt(4)]);//插值器
		bezierSet.setTarget(iv);
		return bezierSet;
	}

	private ValueAnimator agetBezier(final ImageView iv) {
		// 构造曲线轨迹
		PointF pointf1 = getPointF(true);
		PointF pointf2 = getPointF(false);
		PointF pointf0 = new PointF((mWidth -sWidth)/ 2, mHeight - sHeight);
		PointF pointf3 = new PointF(mRandom.nextInt(mWidth), 0);// 终点位置

		// 估值器 控制View路径
		Evaluator mEvaluator = new Evaluator(pointf1, pointf2);
		ValueAnimator animator = ValueAnimator.ofObject(mEvaluator, pointf0,
				pointf3);
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				PointF pointf = (PointF) animation.getAnimatedValue();
				iv.setX(pointf.x);
				iv.setY(pointf.y);
				iv.setAlpha(1 - animation.getAnimatedFraction());
			}
		});
		animator.setTarget(iv);
		animator.setDuration(2000);
		return animator;
	}

	private PointF getPointF(boolean mark) {
		PointF pointf = new PointF();
		pointf.x = mRandom.nextInt(mWidth);
		if (!mark) {
			pointf.y = mRandom.nextInt(mHeight / 2);
		} else {
			pointf.y = mRandom.nextInt(mHeight / 2) + mHeight / 2;
		}
		return pointf;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获取该控件宽高
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}

}
