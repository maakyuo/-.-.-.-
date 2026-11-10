package com.maakyuo.thorwallpaper;
import android.service.wallpaper.WallpaperService;
import android.graphics.*;
import android.os.*;
import android.view.SurfaceHolder;
import java.io.*;
public class GifWallpaperService extends WallpaperService {
 protected int slot(){return 0;}
 public static class TopHome extends GifWallpaperService {protected int slot(){return 0;}}
 public static class TopLock extends GifWallpaperService {protected int slot(){return 1;}}
 public static class BottomHome extends GifWallpaperService {protected int slot(){return 2;}}
 public static class BottomLock extends GifWallpaperService {protected int slot(){return 3;}}
 @Override public Engine onCreateEngine(){return new GifEngine(slot());}
 class GifEngine extends Engine {
  final Handler handler=new Handler(Looper.getMainLooper());final int slot;Movie movie;boolean visible;long start;long lastReload;
  final Runnable draw=new Runnable(){public void run(){render();}};
  GifEngine(int s){slot=s;load();}
  void load(){try(FileInputStream in=openFileInput("wallpaper"+slot+".gif")){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[16384];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);byte[] bytes=out.toByteArray();movie=Movie.decodeByteArray(bytes,0,bytes.length);}catch(Exception e){movie=null;}start=0;lastReload=SystemClock.uptimeMillis();}
  void render(){handler.removeCallbacks(draw);if(!visible)return;
   if(SystemClock.uptimeMillis()-lastReload>3000)loadIfChanged();
   if(movie==null){handler.postDelayed(draw,500);return;}
   Canvas canvas=null;SurfaceHolder holder=getSurfaceHolder();
   try{canvas=holder.lockCanvas();if(canvas!=null){canvas.drawColor(Color.BLACK);int w=canvas.getWidth(),h=canvas.getHeight();int mw=Math.max(1,movie.width()),mh=Math.max(1,movie.height());int mode=getSharedPreferences("MainActivity",0).getInt("mode"+slot,0);float zoom=getSharedPreferences("MainActivity",0).getInt("zoom"+slot,100)/100f;
    float sx,sy;if(mode==2){sx=w/(float)mw*zoom;sy=h/(float)mh*zoom;}else{float factor=mode==1?Math.min(w/(float)mw,h/(float)mh):Math.max(w/(float)mw,h/(float)mh);sx=sy=factor*zoom;}
    canvas.save();canvas.translate((w-mw*sx)/2,(h-mh*sy)/2);canvas.scale(sx,sy);if(start==0)start=SystemClock.uptimeMillis();int duration=movie.duration();if(duration<=0)duration=1000;movie.setTime((int)((SystemClock.uptimeMillis()-start)%duration));movie.draw(canvas,0,0);canvas.restore();
   }}catch(Exception ignored){}finally{if(canvas!=null)holder.unlockCanvasAndPost(canvas);}handler.postDelayed(draw,33);
  }
  long lastModified=-1;
  void loadIfChanged(){File file=getFileStreamPath("wallpaper"+slot+".gif");long modified=file.exists()?file.lastModified():0;if(modified!=lastModified){lastModified=modified;load();}else lastReload=SystemClock.uptimeMillis();}
  @Override public void onVisibilityChanged(boolean v){visible=v;if(v){load();render();}else handler.removeCallbacks(draw);}
  @Override public void onSurfaceChanged(SurfaceHolder h,int f,int w,int ht){super.onSurfaceChanged(h,f,w,ht);render();}
  @Override public void onDestroy(){visible=false;handler.removeCallbacks(draw);super.onDestroy();}
 }
}