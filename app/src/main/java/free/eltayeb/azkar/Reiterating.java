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
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class Reiterating extends Activity implements OnCompletionListener {
	public static final byte REPEAT = 3;
	private static final byte SURAS = 3;
	private byte index, counter;
	// we make for each sura its own media player
	private MediaPlayer[] mPlayers;
	// the next player for elastaza only
	private MediaPlayer mPlayer;
	private MediaPlayer mMediaPlayerLollipop;
	private Drawable[] mImages;
	private ImageView mImageVw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reiterating);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		counter = 1;
		int bgID = getIntent().getIntExtra("bgID", R.drawable.morning);
		int itemID = getIntent().getIntExtra("itemID", R.id.alkorsyVerse);
		boolean isMorning = getIntent().getBooleanExtra("isMorning", true);
		
		if (itemID == R.id.lastPage) {
			mPlayers = new MediaPlayer[] {
				MediaPlayer.create(this, R.raw.s112),
				MediaPlayer.create(this, R.raw.s113),
				MediaPlayer.create(this, R.raw.s114),
			};
			if (isMorning) {
				mImages = new Drawable[] {
					getResources().getDrawable(R.drawable.s112_m),
					getResources().getDrawable(R.drawable.s113_m),
					getResources().getDrawable(R.drawable.s114_m)
				};
			} else {
				mImages = new Drawable[] {
						getResources().getDrawable(R.drawable.s112_e),
						getResources().getDrawable(R.drawable.s113_e),
						getResources().getDrawable(R.drawable.s114_e)
				};
			}
		} else {
			mPlayers = new MediaPlayer[] {
					MediaPlayer.create(this, R.raw.s2_255)
			};
			if (isMorning) 
				mImages = new Drawable[] { getResources().getDrawable(R.drawable.s2_255_m) };
			else
				mImages = new Drawable[] { getResources().getDrawable(R.drawable.s2_255_e) };
		}
		
		mImageVw = (ImageView) findViewById(R.id.reiteratingView);
		mImageVw.setBackgroundResource(bgID);
		
		for (MediaPlayer mp : mPlayers) {
			setMediaPlayerOptions(mp);
		}
		
		mImageVw.setImageDrawable(mImages[0]);
		mPlayer = MediaPlayer.create(this, R.raw.al_estaza);
		setMediaPlayerOptions(mPlayer);
		mPlayer.start();
		
	}
	
	@Override
	protected void onDestroy() {
		// must be before super.onDestroy();
		if (mPlayer != null) { mPlayer.release(); mPlayer = null; }
		
		releasePlayerArray();

		if (isLollipop() && mMediaPlayerLollipop != null) {
			mMediaPlayerLollipop.release();
			mMediaPlayerLollipop = null;
		}
		
		super.onDestroy();
		// showMessage(String.valueOf(player == null));
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mPlayer != null) { mPlayer.release(); mPlayer = null; }
		if (counter <= REPEAT) {
			// exit this activity if alkorsy verse was playing
			if (mImages.length == 1 && index > 0) { finish(); return; }
			
			index = (byte) (index % SURAS);
			if (counter > 1 && index == 0) {
				Toast toast = Toast.makeText(Reiterating.this, String.valueOf(counter), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.RIGHT | Gravity.TOP, 13, 51);
				toast.show();
			}
			if (index == SURAS - 1) counter++;
		} else {
			finish();
			return;
		}
		// note there is return statement above to stop the next lines from execution
		mImageVw.setImageDrawable(mImages[index]);
		// very IMPORTANT line it switches between suras
		if (!isLollipop()) mPlayers[index++].start();
		else handleLollipopIssue(mp);
	}
	
	private void showMessage(String text) {
		Toast.makeText(Reiterating.this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * solving the problem in Lollipop only, the playback doesn't play all suras in
	 * iteration 2 and 3 correctly
	 */
	private void handleLollipopIssue(MediaPlayer oldMediaPlayer) {
		// no point to continue if not Lollipop
		if (!isLollipop()) return;

		// stop working with mPlayers
		releasePlayerArray();

		// work with new media player after releasing the old (last) one was playing
		if (oldMediaPlayer != null) oldMediaPlayer.release();
		int[] resIds =
				(mImages.length == 1) ? new int[]{R.raw.s2_255} : new int[]{ R.raw.s112, R.raw.s113, R.raw.s114 };
		mMediaPlayerLollipop = MediaPlayer.create(this, resIds[index++]);
		setMediaPlayerOptions(mMediaPlayerLollipop);
		// the next line is important because if the user turn off the screen while listening the
		// verse(s), the sound doesn't continue to the end and wrong behavior would happen
		mMediaPlayerLollipop.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		mMediaPlayerLollipop.start();
	}

	/**
	 * check if this device platform is Lollipop or not
	 * @return true if the device platform is Lollipop
	 */
	private boolean isLollipop() {
		int apiLevel = Build.VERSION.SDK_INT;
		return apiLevel == 21 || apiLevel == 22;
	}

	private void releasePlayerArray() {
		// the next line is very important without it
		// this line of coming code "for (MediaPlayer p : mPlayers)" will give NullPointerException
		// because of attempting to get length of null array
		if (mPlayers == null) return; // No point for continue

		int i = 0;
		for (MediaPlayer p : mPlayers) {
			p.release();
			mPlayers[i++] = null;
		}
		mPlayers = null;
	}

	private void setMediaPlayerOptions(MediaPlayer mp) {
		mp.setOnCompletionListener(this);
		mp.setLooping(false);
	}
}
