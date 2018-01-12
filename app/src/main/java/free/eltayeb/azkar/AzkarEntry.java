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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AzkarEntry extends Activity implements OnClickListener {
	
	private SharedPreferences prefs;
	private Button morningBtn;
	private Button eveningBtn;

	@SuppressWarnings({ "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.azkar_entry);
		prefs = getSharedPreferences("setting", Context.MODE_PRIVATE);
		
		morningBtn = (Button) findViewById(R.id.morningBtn);
		morningBtn.setOnClickListener(this);
		eveningBtn = (Button) findViewById(R.id.eveningBtn);
		eveningBtn.setOnClickListener(this);
		
		if (prefs.getBoolean("firstTime", true)) {
			int w = getWindowManager().getDefaultDisplay().getWidth();
			// showMessage("w: " + w);
			if (w >= 1024) { // Dealing with Tablets
				Editor ed = prefs.edit();
				ed.putInt("fontSize", 45);
				ed.putBoolean("isPortrait", false);
				ed.commit();
			}
			showDialog(0);	
		}
		
		adjustView(getIntent().getIntExtra("fontSize", prefs.getInt("fontSize", 27)));
		
		setRequestedOrientation(prefs.getBoolean("isPortrait", true)
				 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
	}
	
	@Override
	protected void onPause() {
		Editor ed = prefs.edit();
		ed.putBoolean("firstTime", false);
		ed.commit();
		super.onPause();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("لضبط إعدادات التطبيق كتغيير حجم الخط أو تثبيت الشاشة أفقيا أو رأسيا أضعط على زر القائمة");
		builder.setCancelable(false);
		builder.setNeutralButton("موافق", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		return builder.create();
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(AzkarEntry.this, Azkar.class);
	    switch (v.getId()) {
	    case R.id.eveningBtn:
	    	startActivity(intent.putExtra("isMorning", false));
	    	break;
		case R.id.morningBtn:
		default:
			startActivity(intent.putExtra("isMorning", true));
			break;
		}
	    // finish();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu_setting, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Editor ed = prefs.edit();
		switch (item.getItemId()) {
		case R.id.landscape:
			ed.putBoolean("isPortrait", false);
			ed.commit();
			showMessage("تم تثبيت شاشة الأذكار أفقياً");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case R.id.portrait:
			ed.putBoolean("isPortrait", true);
			ed.commit();
			showMessage("تم تثبيت شاشة الأذكار رأسياً");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case R.id.fontSize:
		default:
			startActivity(new Intent(this, Settings.class));
			finish();
			break;
		}
		return true;
	}
	
	private void showMessage(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	private void adjustView(int size) {
		morningBtn.setTextSize(size);
		eveningBtn.setTextSize(size);
	}

}
