package com.maakyuo.thorwallpaper;
import android.service.wallpaper.WallpaperService;
import android.graphics.*;
import android.os.*;
import android.view.SurfaceHolder;
import java.io.*;
public class GifWallpaperService extends WallpaperService {
 @Override public Engine onCreateEngine(){return new GifEngine();}
 class GifEngine extends Engine {
   final Handler handler=new Handler(Looper.getMainLooper());
   Movie movie;boolean visible;long start;
   final Runnable draw=new Runnable(){public void run(){render();}};
   GifEngine(){try(FileInputStream in=openFileInput("wallpaper.gif")){byte[] bytes=new byte[in.available()];int offset=0,n;while(offset<bytes.length&&(n=in.read(bytes,offset,bytes.length-offset))>0)offset+=n;movie=Movie.decodeByteArray(bytes,0,offset);}catch(Exception ignored){}}
   void render(){handler.removeCallbacks(draw);if(!visible||movie==null)return;
     SurfaceHolder holder=getSurfaceHolder();Canvas canvas=null;
     try{canvas=holder.lockCanvas();if(canvas!=null){canvas.drawColor(Color.BLACK);int w=canvas.getWidth(),h=canvas.getHeight();float scale=Math.max(w/(float)Math.max(1,movie.width()),h/(float)Math.max(1,movie.height()));canvas.save();canvas.translate((w-movie.width()*scale)/2,(h-movie.height()*scale)/2);canvas.scale(scale,scale);
       if(start==0)start=SystemClock.uptimeMillis();int duration=movie.duration();if(duration<=0)duration=1000;movie.setTime((int)((SystemClock.uptimeMillis()-start)%duration));movie.draw(canvas,0,0);canvas.restore();
     }}catch(Exception ignored){}finally{if(canvas!=null)holder.unlockCanvasAndPost(canvas);}
     handler.postDelayed(draw,33);
   }
   @Override public void onVisibilityChanged(boolean v){visible=v;if(v)render();else handler.removeCallbacks(draw);}
   @Override public void onSurfaceChanged(SurfaceHolder h,int f,int w,int ht){super.onSurfaceChanged(h,f,w,ht);render();}
   @Override public void onDestroy(){visible=false;handler.removeCallbacks(draw);super.onDestroy();}
 }
}