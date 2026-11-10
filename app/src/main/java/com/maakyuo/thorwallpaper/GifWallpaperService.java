package com.maakyuo.thorwallpaper;
import android.service.wallpaper.WallpaperService;
import android.graphics.*;
import android.os.*;
import android.view.SurfaceHolder;
import java.io.*;
import android.media.MediaPlayer;
public class GifWallpaperService extends WallpaperService {
 protected int slot(){return 0;}
 public static class TopHome extends GifWallpaperService {protected int slot(){return 0;}}
 public static class TopLock extends GifWallpaperService {protected int slot(){return 1;}}
 public static class BottomHome extends GifWallpaperService {protected int slot(){return 2;}}
 public static class BottomLock extends GifWallpaperService {protected int slot(){return 3;}}
 @Override public Engine onCreateEngine(){return new GifEngine(slot());}
 class GifEngine extends Engine {
  final Handler handler=new Handler(Looper.getMainLooper());final int slot;Movie movie;Bitmap bitmap;MediaPlayer player;boolean videoMode;boolean visible;boolean surfaceReady;long start;long lastReload;
  final Runnable draw=new Runnable(){public void run(){render();}};
  GifEngine(int s){slot=s;}
  int actualSlot(){try{android.view.Display display=getDisplayContext().getDisplay();if(display!=null)return display.getDisplayId()==android.view.Display.DEFAULT_DISPLAY?0:1;}catch(Exception ignored){}return slot==2?1:0;}
  void load(){stopVideo();bitmap=null;movie=null;videoMode=getSharedPreferences("MainActivity",0).getBoolean("video"+actualSlot(),false);if(videoMode){startVideo();lastReload=SystemClock.uptimeMillis();return;}try(FileInputStream in=openFileInput("wallpaper"+actualSlot()+".img")){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[16384];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);byte[] bytes=out.toByteArray();movie=Movie.decodeByteArray(bytes,0,bytes.length);if(movie==null)bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);}catch(Exception e){movie=null;}start=0;lastReload=SystemClock.uptimeMillis();}
  void render(){handler.removeCallbacks(draw);if(!visible)return;if(videoMode){if(SystemClock.uptimeMillis()-lastReload>3000)loadIfChanged();return;}
   if(SystemClock.uptimeMillis()-lastReload>3000)loadIfChanged();
   if(movie==null&&bitmap==null){handler.postDelayed(draw,500);return;}
   Canvas canvas=null;SurfaceHolder holder=getSurfaceHolder();
   try{canvas=holder.lockCanvas();if(canvas!=null){canvas.drawColor(Color.BLACK);int w=canvas.getWidth(),h=canvas.getHeight();int mw=Math.max(1,movie!=null?movie.width():bitmap.getWidth()),mh=Math.max(1,movie!=null?movie.height():bitmap.getHeight());int mode=getSharedPreferences("MainActivity",0).getInt("mode"+actualSlot(),0);float zoom=getSharedPreferences("MainActivity",0).getInt("zoom"+actualSlot(),100)/100f;
    float sx,sy;if(mode==2){sx=w/(float)mw*zoom;sy=h/(float)mh*zoom;}else{float factor=mode==1?Math.min(w/(float)mw,h/(float)mh):Math.max(w/(float)mw,h/(float)mh);sx=sy=factor*zoom;}
    canvas.save();canvas.translate((w-mw*sx)/2,(h-mh*sy)/2);canvas.scale(sx,sy);if(movie!=null){if(start==0)start=SystemClock.uptimeMillis();int duration=movie.duration();if(duration<=0)duration=1000;movie.setTime((int)((SystemClock.uptimeMillis()-start)%duration));movie.draw(canvas,0,0);}else canvas.drawBitmap(bitmap,0,0,null);canvas.restore();
   }}catch(Exception ignored){}finally{if(canvas!=null)holder.unlockCanvasAndPost(canvas);}handler.postDelayed(draw,movie!=null?33:500);
  }
  long lastModified=-1;
  void loadIfChanged(){File file=getFileStreamPath("wallpaper"+actualSlot()+(getSharedPreferences("MainActivity",0).getBoolean("video"+actualSlot(),false)?".video":".img"));long modified=file.exists()?file.lastModified():0;if(modified!=lastModified||videoMode!=getSharedPreferences("MainActivity",0).getBoolean("video"+actualSlot(),false)){lastModified=modified;load();}else lastReload=SystemClock.uptimeMillis();}
  void stopVideo(){if(player!=null){try{player.reset();player.release();}catch(Exception ignored){}player=null;}}
  void startVideo(){if(!visible||!surfaceReady)return;try{File file=getFileStreamPath("wallpaper"+actualSlot()+".video");if(!file.exists())return;MediaPlayer p=new MediaPlayer();player=p;p.setDataSource(file.getAbsolutePath());p.setSurface(getSurfaceHolder().getSurface());p.setVolume(0f,0f);p.setLooping(true);p.setOnErrorListener((mp,what,extra)->{if(player==mp)stopVideo();return true;});p.setOnPreparedListener(mp->{if(visible&&player==mp){try{mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);mp.start();}catch(Exception ignored){}}});p.prepareAsync();}catch(Exception e){stopVideo();}}
  @Override public void onVisibilityChanged(boolean v){visible=v;if(v){if(surfaceReady){load();render();}}else{handler.removeCallbacks(draw);stopVideo();}}
  @Override public void onSurfaceCreated(SurfaceHolder h){super.onSurfaceCreated(h);surfaceReady=true;if(visible){load();render();}}
  @Override public void onSurfaceDestroyed(SurfaceHolder h){surfaceReady=false;handler.removeCallbacks(draw);stopVideo();super.onSurfaceDestroyed(h);}

  @Override public void onSurfaceChanged(SurfaceHolder h,int f,int w,int ht){super.onSurfaceChanged(h,f,w,ht);if(visible&&surfaceReady){if(videoMode&&player==null)startVideo();else render();}}
  @Override public void onDestroy(){visible=false;handler.removeCallbacks(draw);stopVideo();super.onDestroy();}
 }
}