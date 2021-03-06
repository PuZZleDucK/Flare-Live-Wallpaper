
//Sweet... modding by PuZZleDucK, I'll take some credit


package com.puzzleduck.FlareLiveWallpaper;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.graphics.*;

//This animated wallpaper draws flares and.fire works
public class FlareLiveWallpaper extends WallpaperService {
    @Override
	public void onConfigurationChanged(Configuration newConfig) {

    	super.onConfigurationChanged(newConfig);
		this.onCreate();
	}

	public static final String SHARED_PREFS_NAME="flare_lwp_settings";
    private ArrayList<FlareData> flareList;
 
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new TargetEngine();
    }
    
    class TargetEngine extends Engine 
        implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final int MAX_FLARE_COUNT = 3;

		private final Handler mHandler = new Handler();
        private final Paint mPaint = new Paint();
        private float mTouchX = -1;
        private float mTouchY = -1;

        private float mCenterX1;
        private float mCenterY1;
        
        private float mLastTouchX = 239;//indent initial display
        private float mLastTouchY = 239;
        
//        private boolean flareOn = true;
        
        private final Runnable mDrawCube = new Runnable() {
            public void run() {
                drawFrame();
            }
        };
        private boolean mVisible;
        private SharedPreferences mPrefs;

        public SharedPreferences.OnSharedPreferenceChangeListener listener;
        
        TargetEngine() {
            // Create a Paint to draw the lines 
            final Paint paint = mPaint;
            paint.setColor(0xffffffff);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);//increased stroke... better thin
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE); 

//    		Log.d(TAG, "set prefs listener" );
            mPrefs = FlareLiveWallpaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
            listener = this; //(SharedPreferences.OnSharedPreferenceChangeListener)this;
            mPrefs.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(mPrefs, null);
        }

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        
            //flare settings:
//            flareOn = prefs.getBoolean("flare_on", true);
        }
		
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            SharedPreferences prefs = mPrefs;        

            //flare settings:
//            flareOn = prefs.getBoolean("flare_on", true);
 
            //init flare list
            flareList = new ArrayList<FlareData>(1);
            
            
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawCube);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawCube);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mCenterX1 = width/2; 
            mCenterY1 = height/2;
            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawCube);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xStep, float yStep, int xPixels, int yPixels) {
//            mOffset = xOffset;
            drawFrame();
        }

        /*
         * Store the position of the touch event so we can use it for drawing later
         */
        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchX = event.getX();
                mTouchY = event.getY();
            } else {
                mTouchX = -1;
                mTouchY = -1;
            }
            super.onTouchEvent(event);
        }

        
        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                updateTouchPoint(c);
                //DEBUG
                drawConkey(c);
                drawTouchPointFlare(c);
                }
            } finally { 
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(mDrawCube);
            if (mVisible) {
                mHandler.postDelayed(mDrawCube, 1000 / 25);
            }
        }
        
        void updateTouchPoint(Canvas c) {
        	   if (mTouchX >=0 && mTouchY >= 0) {                

	        	// get relative dirs
	                float diffX = mTouchX - mLastTouchX;
	                float diffY = mTouchY - mLastTouchY;
	                mCenterY1 = mCenterY1 + diffY;
	                mCenterX1 = mCenterX1 + diffX;
	                
	                //store for next
	                mLastTouchX = mTouchX;
	                mLastTouchY = mTouchY;            
        	   }
        	   //pre draw canvas clearing.... do not remove (again).
        	   c.drawColor(0xff000000);   
        }
        
        void drawTouchPointFlare(Canvas c) {

            if (mTouchX >=0 && mTouchY >= 0) {
                //max 5 flares
            	if (flareList.size() < MAX_FLARE_COUNT) {
            		//1 second delay
                	if (flareList.size() > 0 ) {
                		if(flareList.get(flareList.size()-1).getTime() > 10)
                		{
                			flareList.add(new FlareData(mTouchX, mTouchY, 0));
                		}
                	}else
                	{
                		flareList.add(new FlareData(mTouchX, mTouchY, 0 ));
                			
                	}
                }
            }

        	//flare list:   now incorporating move old flare/virs
        		FlareData thisFlare;
				for (Iterator<FlareData> fIterator = flareList.iterator(); fIterator.hasNext();) 
				{
					thisFlare = fIterator.next();

					thisFlare.move();
					thisFlare.render(c, mPaint);
					
//					if(thisFlare.getTime() > thisFlare.getStage1Time() + thisFlare.getStage2Time() + thisFlare.getStage3Time())
//					{
//						//expiring:   ... working :)  still ;)
//						fIterator.remove(); 
//					}
					
					int a = thisFlare.getColor2() >>> 24;
					a -= 2; // fade by 2
					if (a <= 20) { // if reached transparency kill the particle
//10	            this.state = STATE_DEAD;
						fIterator.remove(); 
					} 
					
				}//for

        }//flare
        
        void drawConkey(Canvas c) {
            c.drawColor(0x00000000);

            int oldColor = mPaint.getColor();

            mPaint.setColor(Color.RED);
            //mPaint.setTypeface(null);
            
            c.drawText("Last touch point: (" + (int)mLastTouchX + "," + (int)mLastTouchX + ")", 		5, 100, mPaint);
            c.drawText("Up: " + SystemClock.uptimeMillis(), 		5, 120, mPaint);
            c.drawText("Now: " + SystemClock.elapsedRealtime(), 		5, 140, mPaint);
            c.drawText("This thread: " + SystemClock.currentThreadTimeMillis(), 		5, 160, mPaint);
//            c.drawText("This is preview: " + this.isPreview(), 		5, 180, mPaint);
//            c.drawText("This is viible: " + this.isVisible(), 		5, 200, mPaint);
//            c.drawText("This is viible: " , 		5, 220, mPaint);
            
//        	c.drawText("color = " + mPaint.getColor(), 		5, 590, mPaint);
//        	c.drawText("mTouchX = " + mTouchX, 		5, 610, mPaint);
//            c.drawText("mTouchY = " + mTouchY, 		5, 630, mPaint);
//            c.drawText("mCenterX1= " + mCenterX1, 5, 690, mPaint);
//            c.drawText("mCenterY1= " + mCenterY1, 5, 710, mPaint);

			int textSize = 22;
			mPaint.setTextSize(textSize);
            c.drawText("flareCount= " + flareList.size(), 2, 210, mPaint);
			//FlareData aFlare;
			int line = 0;
			for(FlareData aFlare : flareList)
			{
				line += textSize;
				c.drawText(" -"
						   +" a= " + aFlare.getAngle() 
						   +" 1=" + aFlare.getStage1Time()
						   +" 2=" + aFlare.getStage2Time()
						   //+" 3=" + aFlare.getStage3Time()
						  // +" t=" + aFlare.getTriggerTime()
						   +" time= " + aFlare.getTime(), 
					
					5, 210 + line, mPaint);

				mPaint.setColor(Color.GREEN);
				c.drawText(" -"
						   //+" a= " + aFlare.getAlpha() 
						   +" c1= " + aFlare.getColor1() 
						   +" c2=" + aFlare.getColor2(), 

						   5, 310 + line, mPaint);
			}
            
            mPaint.setColor(oldColor);

//          c.drawText("diffX = " + diffX, 5, 650, mPaint);
//          c.drawText("diffY = " + diffY, 5, 670, mPaint); //oops... this should be in conkey if anywhere.
        }
        
    }

	public static void changeSettings() {
		if(SHARED_PREFS_NAME != null)
		{
			
			//WallpaperService ws = (WallpaperService)FlareLiveWallpaper.this;
		}
		
	}
    
}
