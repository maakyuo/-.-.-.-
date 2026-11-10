package com.maakyuo.thorwallpaper;
import android.app.*;
import android.app.WallpaperManager;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity {
 static final String[] LABELS={"상단 화면","하단 화면"};
 static final Class<?>[] SERVICES={GifWallpaperService.TopHome.class,GifWallpaperService.BottomHome.class};
 int selected=0; TextView status; LinearLayout root;
 @Override public void onCreate(Bundle b){super.onCreate(b);
  ScrollView scroll=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(1);root.setPadding(26,30,26,30);scroll.addView(root);
  TextView title=new TextView(this);title.setText("AYN Thor 배경화면");title.setTextSize(23);root.addView(title);
  status=new TextView(this);status.setPadding(0,12,0,12);status.setText("상단과 하단에 GIF, 이미지 또는 MP4 동영상을 각각 지정할 수 있습니다.");root.addView(status);
  for(int i=0;i<2;i++){final int slot=i;TextView heading=new TextView(this);heading.setText("\n"+LABELS[i]);heading.setTextSize(19);root.addView(heading);
   Button choose=new Button(this);choose.setText("GIF / 이미지 / 동영상 선택");root.addView(choose);choose.setOnClickListener(v->{selected=slot;Intent pick=new Intent(Intent.ACTION_OPEN_DOCUMENT);pick.setType("*/*");pick.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/*","video/mp4","video/webm","video/3gpp"});pick.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(pick,100);});
   TextView zoomText=new TextView(this);root.addView(zoomText);
   SeekBar zoom=new SeekBar(this);zoom.setMax(300);zoom.setProgress(getPreferences(0).getInt("zoom"+i,100)-50);root.addView(zoom);
   zoomText.setText("이미지 크기: "+(zoom.getProgress()+50)+"%");
   zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int progress,boolean user){zoomText.setText("이미지 크기: "+(progress+50)+"%");if(user)getPreferences(0).edit().putInt("zoom"+slot,progress+50).apply();}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
   Spinner mode=new Spinner(this);String[] modes={"화면 채우기 (자르기 가능)","전체 표시 (여백 가능)","화면에 맞게 늘이기"};ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,modes);mode.setAdapter(adapter);root.addView(mode);mode.setSelection(getPreferences(0).getInt("mode"+i,0));mode.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?> p,View v,int position,long id){getPreferences(0).edit().putInt("mode"+slot,position).apply();}public void onNothingSelected(android.widget.AdapterView<?> p){}});
   Button apply=new Button(this);apply.setText(LABELS[i]+" 배경화면 적용");root.addView(apply);apply.setOnClickListener(v->{Intent intent=new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new ComponentName(this,SERVICES[slot]));try{startActivity(intent);}catch(Exception e){status.setText("배경화면 설정 화면 오류: "+e.getMessage());}});
  }setContentView(scroll);
 }
 @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(req!=100||result!=RESULT_OK||data==null||data.getData()==null)return;
  try(InputStream in=getContentResolver().openInputStream(data.getData());OutputStream out=openFileOutput("wallpaper"+selected+(data.getType()!=null&&data.getType().startsWith("video/")?".video":".img"),MODE_PRIVATE)){byte[] buf=new byte[16384];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);getPreferences(0).edit().putBoolean("video"+selected,data.getType()!=null&&data.getType().startsWith("video/")).apply();status.setText(LABELS[selected]+" 파일 저장 완료.");}catch(Exception e){status.setText("파일 저장 실패: "+e.getMessage());}
 }
}