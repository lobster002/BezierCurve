package com.sky.beziercurve;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class Evaluator implements TypeEvaluator<PointF> {
	PointF pointf1 = null;
	PointF pointf2 = null;

	public Evaluator(PointF pointf1, PointF pointf2) {
		this.pointf1 = pointf1;
		this.pointf2 = pointf2;
	}

	@Override
	public PointF evaluate(float t, PointF point0, PointF point3) {
		PointF point = new PointF();
		point.x = point0.x * (1 - t) * (1 - t) * (1 - t) + 3 * pointf1.x * t
				* (1 - t) * (1 - t) + 3 * pointf2.x * t * t * (1 - t)
				+ point3.x * t * t * t;
		point.y = point0.y * (1 - t) * (1 - t) * (1 - t) + 3 * pointf1.y * t
				* (1 - t) * (1 - t) + 3 * pointf2.y * t * t * (1 - t)
				+ point3.y * t * t * t;
		return point;
	}
}
