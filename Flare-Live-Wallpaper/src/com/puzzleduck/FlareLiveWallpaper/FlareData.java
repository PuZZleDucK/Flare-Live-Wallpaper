package com.puzzleduck.FlareLiveWallpaper;
import android.graphics.*;
import java.util.*;
import android.os.*;
import java.util.Iterator;

public class FlareData {
	private float x;
	private float y;
	private float angle;
	private float time;
	//private float triggerTime;
	private float stage1Time;
	private float stage2Time;
	private float stage3Time;
	private int color1;
	private int color2;
	private int type;
	private int flareAlpha;
	private double explosionRadius;
	private int explosionCount;
	private int explosion2Count;
	private double explosion2Radius;
	private static Random rng;
	
	public FlareData(float inx, float iny, int intype)
	{
		int firstExplosionMin = 5;
		int firstExplosionMax = 30;
		int secondExplosionMin = 2;
		int secondExplosionMax = 12;
		flareAlpha = 255;
		rng = new Random();
		x = inx;
		y = iny;
		angle = 20-rng.nextInt(40);
		//find out distance to edge of screen 
		// and use this as a maximum for trigger times
		// actually, just detonating within 5px 
		// of the edge of screen would do the job.
		time = 0;
		//triggerTime = 50;
		setStage1Time(30 + rng.nextInt(20)); //, 
		setStage2Time(15 + rng.nextInt(10));
		setStage3Time(360+rng.nextInt(20));//40
		color1 = Color.argb(255,rng.nextInt(155)+100,rng.nextInt(155)+100,rng.nextInt(155)+100);//incolor1;
		color2 = Color.argb(255,rng.nextInt(155)+100,rng.nextInt(155)+100,rng.nextInt(155)+100); //incolor2;
		type = intype;
		explosionCount = firstExplosionMin + rng.nextInt(firstExplosionMax);
		explosionRadius = 0;
		explosion2Count = secondExplosionMin + rng.nextInt(secondExplosionMax) ;
		explosion2Radius = 0;
	}

	public void render(Canvas c, Paint mPaint)
	{
		if (this.getTime() < this.getStage1Time()) {
			//stage 1
		    int flareColor = Color.argb(170,210,0,0);
			mPaint.setColor(flareColor);
			int flareWidth = 4;
			int flareHeight = 18;
			// use angle to draw angular rocket later
			c.drawRoundRect(new RectF(this.getX()-(flareWidth/2), this.getY()-(flareHeight/2), this.getX()+(flareWidth/2), this.getY()+(flareHeight/2)), 0, 0, mPaint);
		} else {
			if (this.getTime() < this.getStage1Time() + this.getStage2Time()) {
				//stage 2
				mPaint.setColor(this.getColor1());
				this.incrementExplosionRadius(2);
				
				switch(rng.nextInt(5))
				{
						//shimmer
					case(0):
						//	mPaint.setColor(this.getColor2()-0x2200000);
						this.setColor1( this.getColor1()-10 );
						break;
					case(2):

						//	mPaint.setColor(this.getColor2()+0x22000000);
						this.setColor1( this.getColor1()+10 );
						break;
					default:
						break;
				}
				//drift
				if(rng.nextInt(20) == 1)
				{
					this.setY(this.getY()+1);
				}

				for(double i = this.getExplosionCount(); i > 0; i--)
				{
					double thisFraction = i/this.getExplosionCount();
					c.drawCircle(
						(float)(this.getExplosionRadius() * (Math.sin(( thisFraction )*0.017453293*360)) + this.getX() + rng.nextInt(2)-4),
						(float)(this.getExplosionRadius() * (Math.cos(( thisFraction )*0.017453293*360)) + this.getY() + rng.nextInt(2)-4),
						1,
						mPaint);
				}
			}else
			{
				if(this.getTime() < this.getStage1Time() + this.getStage2Time() + this.getStage3Time())
				{
					//stage 3
					mPaint.setColor(this.getColor2());
					this.incrementExplosion2Radius();

					for(int i = this.getExplosionCount(); i > 0; i--)
					{
						for(int j = this.getExplosion2Count(); j > 0; j--)
						{
							switch(rng.nextInt(5))
							{
								//shimmer
								case(0):
								//	mPaint.setColor(this.getColor2()-0x2200000);
								    this.setColor2( this.getColor2()-1 );
									break;
								case(2):

								//	mPaint.setColor(this.getColor2()+0x22000000);
								    this.setColor2( this.getColor2()+1 );
									break;
								default:
									break;
							}
							
							//fade colors
							mPaint.setColor(this.getColor2());
							if(rng.nextInt(25) == 1)
							{
								this.setAlpha(this.getAlpha()-1);
								mPaint.setAlpha(this.getAlpha());
								
								int a = this.color2 >>> 24;
                     	        a -= 1; // fade
                      	        this.color2 = (this.color2 & 0x00ffffff) + (a << 24);       // set the new alpha
								
                  	            mPaint.setAlpha(a);
							}
							
							c.drawPoint(
								(float)( this.getExplosion2Radius() * (Math.sin(((double)j/this.getExplosion2Count())*0.017453293*360)) + this.getExplosionRadius() * (Math.sin(((double)i/this.getExplosionCount())*0.017453293*360)) + this.getX() + rng.nextInt(2)-4),
								(float)( this.getExplosion2Radius() * (Math.cos(((double)j/this.getExplosion2Count())*0.017453293*360)) + this.getExplosionRadius() * (Math.cos(((double)i/this.getExplosionCount())*0.017453293*360)) + this.getY() + rng.nextInt(2)-4),
								mPaint);
							mPaint.setAlpha(255);

						}
						//drift down
						if(rng.nextInt(40) == 1)
						{
							this.setY(this.getY()+1);
						}
					}//for j
				}//for i
			}//if final stage

		}//else second stage
	}//render function

	private void incrementExplosionRadius(double p0)
	{
		this.explosionRadius += p0;
	}


	public void move()
	{
		//move  old flare/virs
		if (this.getTime() < this.getStage1Time()) 
		{
			this.setY( (this.getY()
						   + (float)Math.sin(SystemClock.elapsedRealtime()) - 0.2f * this.getTime() ));
			this.setX(this.getX()
					+ (float) Math.sin(SystemClock.elapsedRealtime()) + (( this.getAngle() * this.getTime())/80)  );

		} 

		this.setTime(this.getTime() + 1);
		
	}

	
	public float getAngle(){
		return angle;
	}
	
	public void setAngle(float inAngle){
		angle = inAngle;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

//	public float getTriggerTime() {
//		return triggerTime;
//	}

//	public void setTriggerTime(float triggerTime) {
//		this.triggerTime = triggerTime;
//	}

	public int getColor1() {
		return color1;
	}

	public void setColor1(int color1) {
		this.color1 = color1;
	}

	public int getColor2() {
		return color2;
	}

	public void setColor2(int color2) {
		this.color2 = color2;
	}

	public int getAlpha() {
		return this.flareAlpha;
	}

	public void setAlpha(int alpha) {
		this.flareAlpha = alpha;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setStage1Time(float stage1Time) {
		this.stage1Time = stage1Time;
	}

	public float getStage1Time() {
		return stage1Time;
	}

	public void setStage2Time(float stage2Time) {
		this.stage2Time = stage2Time;
	}

	public float getStage2Time() {
		return stage2Time;
	}

	public void setStage3Time(float stage3Time) {
		this.stage3Time = stage3Time;
	}

	public float getStage3Time() {
		return stage3Time;
	}

	public void setExplosionRadius(double explosionRadius) {
		this.explosionRadius = explosionRadius;
	}
	public void incrementExplosionRadius() {
		this.explosionRadius ++;
	}

	public double getExplosionRadius() {
		return explosionRadius;
	}

	public void setExplosionCount(int explosionCount) {
		this.explosionCount = explosionCount;
	}

	public int getExplosionCount() {
		return explosionCount;
	}

	public void setExplosion2Count(int explosion2Count) {
		this.explosion2Count = explosion2Count;
	}

	public int getExplosion2Count() {
		return explosion2Count;
	}

	public void incrementExplosion2Radius() {
		this.explosion2Radius ++;
		
	}
	public double getExplosion2Radius() {
		return explosion2Radius;
	}
}
