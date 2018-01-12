/*
 * Copyright (C) 2018 Yahia H. El-Tayeb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package free.eltayeb.azkar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Settings extends Activity implements OnClickListener {
	private SharedPreferences prefs;
	private int fontSize;
	private TextView zoomIn;
	private TextView zoomOut;
	private TextView azkarView;
	private boolean isLstVisible;
	private LinearLayout lst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		prefs = getSharedPreferences("setting", Context.MODE_PRIVATE);
		setRequestedOrientation(prefs.getBoolean("isPortrait", true)
				 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// fontSize = prefs.getInt("fontSize", R.dimen.text_size);
		
		azkarView = (TextView) findViewById(R.id.azkarView);
		azkarView.setMovementMethod(new ScrollingMovementMethod());
		zoomIn = (TextView) findViewById(R.id.zoomIn);
		zoomOut = (TextView) findViewById(R.id.zoomOut);
		lst     = (LinearLayout) findViewById(R.id.lst);
		zoomIn.setOnClickListener(this);
		zoomOut.setOnClickListener(this);
		azkarView.setOnClickListener(this);
		
		isLstVisible = true;
		// getResources().getVal
		// showMessage(String.valueOf(azkarView.getTextSize()));
		fontSize = prefs.getInt("fontSize", 27);
		adjustView(fontSize);
		
	}
	
    @Override
    protected void onResume() {
    	// should be called at first to avoid application crash risk
    	super.onResume();
    	showMessage("لإظهار أو إخفاء القائمة أضعط على الشاشة");
		showMessage("لحفظ التفيرات أضغط على زر الرجوع");
    }
    
	@Override
	protected void onDestroy() {
		// should be called at first to avoid application crash risk
		super.onDestroy();
		Editor ed = prefs.edit();
		ed.putInt("fontSize", fontSize);
		ed.commit();
	}

	@Override
	public void onBackPressed() {
		if (isLstVisible) {
			lst.setVisibility(View.INVISIBLE); 
			isLstVisible = false; 
		} else { 
			startActivity(new Intent(this, AzkarEntry.class).putExtra("fontSize", fontSize));
			finish();
		    // super.onBackPressed(); 
	    }
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.zoomOut:
			adjustView(--fontSize);
			// showMessage(String.valueOf(fontSize));
			break;

		case R.id.zoomIn:
			adjustView(++fontSize);
			// showMessage(String.valueOf(fontSize));
			break;
		default:
		case R.id.azkarView:
			if (isLstVisible) { lst.setVisibility(View.INVISIBLE); isLstVisible = false; }
			else              { lst.setVisibility(View.VISIBLE);      isLstVisible = true; }
			break;
			
		}
		
	}
	
	private void adjustView(float size) {
		zoomIn.setTextSize(size);
		zoomOut.setTextSize(size);
		azkarView.setTextSize(size);
		
	}
	
	/*
	public static float pixelsToSp(Context context, float px) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return px/scaledDensity;
	}
	
	public static float spToPixels(Context context, float sp) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return sp * scaledDensity;
	}
	*/
	
	private void showMessage(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	

}
