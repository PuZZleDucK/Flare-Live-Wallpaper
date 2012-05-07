
//Sweet... modding by PuZZleDucK, I'll take some credit


package flarelivewallpaper.puzzleduck.com;

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

        /*
         * Draw one frame of the animation. This method gets called repeatedly
         * by posting a delayed Runnable. You can do any drawing you want in
         * here. This example draws a wireframe cube.
         */
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

			Random rng = new Random();
//            Log.d(TAG, "Start Flare");
        	//add after flare...viral...crack
//new flare 
            if (mTouchX >=0 && mTouchY >= 0) {
                //max 5 flares
            	if (flareList.size() < MAX_FLARE_COUNT) {
            		//1 second delay
                	if (flareList.size() > 0 ) {
                		if(flareList.get(flareList.size()-1).getTime() > 10)
                		{
                			flareList.add(new FlareData(mTouchX, mTouchY, rng.nextInt(10)-5, 0xDD00FF00, 0xDDFF0000, 0, 30 + rng.nextInt(20), 15 + rng.nextInt(10), 30+rng.nextInt(20)));
                		}
                	}else
                	{
                		flareList.add(new FlareData(mTouchX, mTouchY, rng.nextInt(10)-5, 0xDD0000FF, 0xDDFF0000, 0, 30 + rng.nextInt(20), 15 + rng.nextInt(10), 30+rng.nextInt(20)));
                			
                	}
                }
            }

        	//flare list:   now incorporating move old flare/virs
        		FlareData thisFlare;
				for (Iterator<FlareData> fIterator = flareList.iterator(); fIterator.hasNext();) 
				{
					thisFlare = fIterator.next();

					//move  old flare/virs
					if (thisFlare.getTime() < thisFlare.getStage1Time()) 
					{
						thisFlare.setY((float) (thisFlare.getY()
								+ Math.sin(SystemClock.elapsedRealtime()) - 0.2 * thisFlare.getTime() ));
						thisFlare.setX((float) thisFlare.getX()
								+ (float) Math.sin(SystemClock.elapsedRealtime()) + (( thisFlare.getTilt() * thisFlare.getTime())/80)  );
					} 
//					else {
//					}
//						if (thisFlare.getTime() > thisFlare.getStage1Time()*3) 
//						{
//							//
//						}
						
					thisFlare.setTime(thisFlare.getTime() + 1);

					//render
					if (thisFlare.getTime() < thisFlare.getStage1Time()) {
						//stage 1
//			            Log.d(TAG, "S1");
//						mPaint.setColor(0xFF00FF00);
						mPaint.setColor(0xCCDDDD00);
//						c.drawCircle(thisFlare.getX(), thisFlare.getY(), 3, mPaint);
						c.drawRoundRect(new RectF(thisFlare.getX(), thisFlare.getY(), thisFlare.getX()+1, thisFlare.getY()+5), 0, 0, mPaint);
					} else {
						if (thisFlare.getTime() < thisFlare.getStage1Time() + thisFlare.getStage2Time()) {
							//stage 2
//				            Log.d(TAG, "S2");
							
							if(thisFlare.getExplosionCount() == 0) //new explosion
							{
								thisFlare.setExplosionCount(6 + rng.nextInt(20) );
//								Log.d(TAG, "\n\nFlare count: " + thisFlare.getExplosionCount());

//								for(int i = thisFlare.getExplosionCount(); i > 0; i--)
//								{	
//									Log.d(TAG, "   -flare" + i +": " + thisFlare.getExplosionCount());
//									Log.d(TAG, "        i/count " + (double)i/(double)thisFlare.getExplosionCount() );
//									Log.d(TAG, "        i/count*360 " + (double)((double)i/(double)thisFlare.getExplosionCount())*360.0 );
//									Log.d(TAG, "        rad sin i/count*360 " + (float)((double)thisFlare.getExplosionRadius()+20) * Math.sin((double)((double)i/(double)thisFlare.getExplosionCount())*359.0));
//									Log.d(TAG, "        rad cos i/count*360 " + (float)((double)thisFlare.getExplosionRadius()+20) * Math.cos((double)((double)i/(double)thisFlare.getExplosionCount())*359.0));
//								}
							}
							
							mPaint.setColor(thisFlare.getColor1());
							thisFlare.incrementExplosionRadius();
							thisFlare.incrementExplosionRadius();
							//fade colors
							if(rng.nextInt(4) == 1)
							{
								thisFlare.setColor1(thisFlare.getColor1()-0x01000000);
							}
							//drift down
							if(rng.nextInt(40) == 1)
							{
								thisFlare.setY(thisFlare.getY()+1);
							}
							
							for(int i = thisFlare.getExplosionCount(); i > 0; i--)
							{
								c.drawCircle(
										(float)((double)thisFlare.getExplosionRadius() * (Math.sin((double)((double)i/(double)thisFlare.getExplosionCount())*0.017453293*360)) + thisFlare.getX() + rng.nextInt(2)-4),
										(float)((double)thisFlare.getExplosionRadius() * (Math.cos((double)((double)i/(double)thisFlare.getExplosionCount())*0.017453293*360)) + thisFlare.getY() + rng.nextInt(2)-4),
										1,
										mPaint);
//								c.drawCircle(
//										(float) (thisFlare.getX() + (Math.cos( (i/thisFlare.getExplosionCount())*360 ) * thisFlare.getExplosionRadius())),
//										(float) (thisFlare.getY() + (Math.sin((i/thisFlare.getExplosionCount())*360) * thisFlare.getExplosionRadius())),
//										2,
//										mPaint);
							}
						}else
						{
							if(thisFlare.getTime() < thisFlare.getStage1Time() + thisFlare.getStage2Time() + thisFlare.getStage3Time())
							{
								//stage 3
								mPaint.setColor(thisFlare.getColor2());
								thisFlare.incrementExplosion2Radius();
								
								if(thisFlare.getExplosion2Count() == 0) //new explosion
								{
									thisFlare.setExplosion2Count(4 + rng.nextInt(8) );
	//								Log.d(TAG, "\n\nFlare2 count: " + thisFlare.getExplosionCount());
	
								}

								
								
								for(int i = thisFlare.getExplosionCount(); i > 0; i--)
								{
									for(int j = thisFlare.getExplosion2Count(); j > 0; j--)
									{

										switch(rng.nextInt(5))
										{
											case(0):
												mPaint.setColor(thisFlare.getColor2()-0x2200000);
											
											case(2):

												mPaint.setColor(thisFlare.getColor2()+0x22000000);
											break;
											default:
												mPaint.setColor(thisFlare.getColor2());
												break;
										}
										c.drawPoint(
												(float)( (double)thisFlare.getExplosion2Radius() * (Math.sin((double)((double)j/(double)thisFlare.getExplosion2Count())*0.017453293*360)) +     (double)thisFlare.getExplosionRadius() * (Math.sin((double)((double)i/(double)thisFlare.getExplosionCount())*0.017453293*360)) + thisFlare.getX() + rng.nextInt(2)-4),
												(float)( (double)thisFlare.getExplosion2Radius() * (Math.cos((double)((double)j/(double)thisFlare.getExplosion2Count())*0.017453293*360)) +     (double)thisFlare.getExplosionRadius() * (Math.cos((double)((double)i/(double)thisFlare.getExplosionCount())*0.017453293*360)) + thisFlare.getY() + rng.nextInt(2)-4),
												mPaint);
										
									}
									
									//fade colors
									if(rng.nextInt(5) == 1)
									{
										thisFlare.setColor2(thisFlare.getColor2()-0x01000000);
									}
									
									//drift down
									if(rng.nextInt(40) == 1)
									{
										thisFlare.setY(thisFlare.getY()+1);
									}
								}

								
								
								
							}else
							{
								//expiring:   ... working :)
								fIterator.remove();
								
							}
						}

					}//else
				}//for

        }//flare
        
        void drawConkey(Canvas c) {
            c.drawColor(0x00000000);

            int oldColor = mPaint.getColor();

            mPaint.setColor(0xffff0000);
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
            c.drawText("flareCount= " + flareList.size(), 5, 210, mPaint);
            
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
