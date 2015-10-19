package com.sky.beziercurve;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, Runnable {

	private Button start = null;
	private BezierCurve container = null;
	private Handler mHandler = null;
	private volatile boolean isRunning = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);
		container = (BezierCurve) findViewById(R.id.container);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				container.add();
			}
		};

		new Thread(this).start();
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(this, "手动添加10个", Toast.LENGTH_SHORT).show();
		for(int i = 0 ;i < 10;i++){
			container.add();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				Thread.sleep(1000);
				mHandler.sendEmptyMessage(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
