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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Azkar extends Activity implements OnLongClickListener, OnClickListener{

	private int bgID;
	private TextView azkarTxtView;
	private ScrollView azkarScrollView;
	private SharedPreferences prefs;
	private LinearLayout lst;
	private boolean isLstVisible;
	private Intent intent;
	private TextView alkorsyVwItem;
	private TextView lastPageVwItem;
	private boolean isMorning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.azkar);

		// stop screen from locking off automatically
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		// lstVw.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view_item, new String[] {"hello"} ));
		lst = (LinearLayout) findViewById(R.id.lst);
		alkorsyVwItem = (TextView) findViewById(R.id.alkorsyVwItem);
		lastPageVwItem = (TextView) findViewById(R.id.lastPageVwItem);
		azkarTxtView = (TextView) findViewById(R.id.azkarView);
		azkarScrollView = (ScrollView)findViewById(R.id.azkarScrollView);
		//azkarTxtView.setMovementMethod(new ScrollingMovementMethod());
		azkarTxtView.setOnLongClickListener(this);
		azkarTxtView.setOnClickListener(this);

		// delete the sound effect when click on azkar text view
		azkarTxtView.setSoundEffectsEnabled(false);

		alkorsyVwItem.setOnClickListener(this);
		lastPageVwItem.setOnClickListener(this);
		prefs = getSharedPreferences("setting", Context.MODE_PRIVATE);
		setRequestedOrientation(prefs.getBoolean("isPortrait", true)
				? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		if (getIntent().getExtras().getBoolean("isMorning")) {
			azkarTxtView.setText(R.string.morning_azkar);
			azkarTxtView.setTextColor(0xFF000000); // alpha R G B
			isMorning = true;
			bgID = R.drawable.morning;
		} else {
			azkarTxtView.setText(R.string.evening_azkar);
			azkarTxtView.setTextColor(0xFFFFFFFF); // alpha R G B
			isMorning = false;
			bgID = R.drawable.evening_2;
		}

		azkarTxtView.setBackgroundResource(bgID);
		intent = new Intent(getApplicationContext(), Reiterating.class);
		intent.putExtra("bgID", bgID).putExtra("isMorning", isMorning);

		adjustView(prefs.getInt("fontSize", 27));

		// we use this variable to check today with the last day the user access this application
		String today = new SimpleDateFormat("dd").format(new Date());
		// my new code (logic)
		// check if we start in new day we doesn't scroll to the saved position 
		// (start scrolling from the beginning)
		if (prefs.getString("day", today).equals(today)) {
			// NOTE: if you don't use post method the scroll view doesn't scroll to the desired position
			// this method is called after the android rendering the UI so you can then scroll
			azkarScrollView.post(
					new Runnable() {
						@Override
						public void run() {
							int offset;
							if (isMorning) offset = prefs.getInt("yMorning", 0);
							else           offset = prefs.getInt("yEvening", 0);
							azkarScrollView.scrollBy(0, offset);
							
						}
					});
		} else { // in a new day ... we reset scrolling positions' values to zero
			SharedPreferences.Editor ed = prefs.edit();
			ed.putInt("yMorning", 0);
			ed.putInt("yEvening", 0);
			ed.commit();
		}

		// run the following code (logic) only at the first time we run this application version 2.4
		//		if (prefs.getBoolean("isFirst2_4", true)) 
		//			if (prefs.getBoolean("isMorning", isMorning) == isMorning &&
		//			prefs.getString("day", today).equals(today))
		//				azkarTxtView.scrollBy(0, prefs.getInt("y", 0));

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * it is very important to save preferences in onPause state not onDestroy because the application
	 * can be terminated after pause state for version 2.3.3 but it can be terminated on stop state for 
	 * the versions above 2.3.3
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		savePreferences();
	}

	private void savePreferences() {
		Editor ed = prefs.edit();
		ed.putInt("bgID", bgID);
		//ed.putInt("y", azkarView.getScrollY());

		// set isFirst2_4 to false to stop running the old code (old logic)
		ed.putBoolean("isFirst2_4", false);

		// save the y position of the morning view and evening view each individually
		if (isMorning) ed.putInt("yMorning", azkarScrollView.getScrollY());
		else           ed.putInt("yEvening", azkarScrollView.getScrollY());
		
		// the next code is used for testing and debugging
		//showMessage("scroll y: " + azkarScrollView.getScrollY());

		//ed.putBoolean("isMorning", isMorning);
		String day = new SimpleDateFormat("dd").format(new Date());
		ed.putString("day", day);
		ed.commit();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isLstVisible) { lst.setVisibility(View.INVISIBLE); isLstVisible = false; }
		else finish();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isLstVisible) { lst.setVisibility(View.INVISIBLE); isLstVisible = false; }
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu_reiterating, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemID = item.getItemId();
		switch (itemID) {
		case R.id.lastPage:
			intent.putExtra("itemID", itemID );
			break;
		case R.id.alkorsyVerse:
		default:
			intent.putExtra("itemID", itemID);
			break;
		}
		startActivity(intent);
		return true;
	}


	@Override
	public boolean onLongClick(View v) {
		// showMessage("long click");
		if (!isLstVisible) { lst.setVisibility(View.VISIBLE);   isLstVisible = true; }
		else               { lst.setVisibility(View.INVISIBLE); isLstVisible = false; }
		return true;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alkorsyVwItem:
			intent.putExtra("itemID", R.id.alkorsyVerse);
			startActivity(intent);
			lst.setVisibility(View.INVISIBLE);   
			isLstVisible = false;
			break;
		case R.id.lastPageVwItem:
			intent.putExtra("itemID", R.id.lastPage);
			startActivity(intent);
			lst.setVisibility(View.INVISIBLE);   
			isLstVisible = false;
			break;
		case R.id.azkarView:
		default:
			if (isLstVisible) { lst.setVisibility(View.INVISIBLE);   isLstVisible = false; }
			break;
		}

	}

	private void adjustView(int size) {
		alkorsyVwItem.setTextSize(size);
		lastPageVwItem.setTextSize(size);
		azkarTxtView.setTextSize(size);
	}

	private void showMessage(String text) {
		Toast.makeText(Azkar.this, text, Toast.LENGTH_SHORT).show();
	}

}
