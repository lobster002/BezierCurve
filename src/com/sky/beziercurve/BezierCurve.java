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
		// ��ȡͼƬ��Դ
		sources = new Drawable[2];
		sources[0] = getResources().getDrawable(R.drawable.blue_);
		sources[1] = getResources().getDrawable(R.drawable.red_);

		// ��ȡͼƬ��ʵ�ʿ��
		sWidth = sources[0].getIntrinsicWidth();
		sHeight = sources[0].getIntrinsicHeight();

		mRandom = new Random();

		params = new RelativeLayout.LayoutParams(sWidth, sHeight);
		params.addRule(CENTER_HORIZONTAL, TRUE);// ����ڸ�����ˮƽ����
		params.addRule(ALIGN_PARENT_BOTTOM, TRUE);// ����ڸ����ֵײ�
	
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

		// ���Զ�����������
		AnimatorSet set = getAnimator(iv);
		set.start();
	}

	private AnimatorSet getAnimator(ImageView iv) {
		// �����������Զ���
		// 1. alpha

		// ͸���ȴ�0.1f ��1.0f ����
		ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0, 1f, 1.0f);

		// 2.Scale����
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 0, 2f,
				1.0f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 0, 2f,
				1.0f);

		AnimatorSet enter = new AnimatorSet();// ��������
		enter.setDuration(500);// ����ʱ��
		enter.playTogether(alpha, scaleX, scaleY);// ͬʱִ��
		enter.setTarget(iv);

		// 3.����������(���ģ������޸�Iv����--PointF��x��y��)
		ValueAnimator bezierValueAnimator = agetBezier(iv);
		AnimatorSet bezierSet = new AnimatorSet();
		bezierSet.playSequentially(enter, bezierValueAnimator);// ˳��ִ��
		bezierSet.setInterpolator(interpolators[mRandom.nextInt(4)]);//��ֵ��
		bezierSet.setTarget(iv);
		return bezierSet;
	}

	private ValueAnimator agetBezier(final ImageView iv) {
		// �������߹켣
		PointF pointf1 = getPointF(true);
		PointF pointf2 = getPointF(false);
		PointF pointf0 = new PointF((mWidth -sWidth)/ 2, mHeight - sHeight);
		PointF pointf3 = new PointF(mRandom.nextInt(mWidth), 0);// �յ�λ��

		// ��ֵ�� ����View·��
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
		// ��ȡ�ÿؼ����
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}

}
