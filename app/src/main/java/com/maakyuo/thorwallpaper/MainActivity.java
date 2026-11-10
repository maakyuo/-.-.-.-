package com.maakyuo.thorwallpaper;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import java.io.*;
public class MainActivity extends Activity {
  private static final int PICK=100;
  private TextView status;
  @Override public void onCreate(Bundle b){super.onCreate(b);
    LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(36,48,36,24);
    TextView title=new TextView(this);title.setText("AYN Thor 배경화면 변경");title.setTextSize(23);root.addView(title);
    status=new TextView(this);status.setText("GIF를 선택한 다음 라이브 배경화면으로 적용하세요.\n상·하단 독립 적용은 기기 지원 여부를 확인해야 합니다.");status.setPadding(0,24,0,24);root.addView(status);
    Button choose=new Button(this);choose.setText("저장소에서 GIF 선택");root.addView(choose);
    choose.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/gif");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);});
    Button apply=new Button(this);apply.setText("배경화면 적용");root.addView(apply);
    apply.setOnClickListener(v->{Intent i=new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,new android.content.ComponentName(this,GifWallpaperService.class));try{startActivity(i);}catch(Exception e){status.setText("배경화면 설정 화면을 열 수 없습니다: "+e.getMessage());}});
    setContentView(root);
  }
  @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(req!=PICK||result!=RESULT_OK||data==null)return;
    Uri uri=data.getData();if(uri==null)return;
    try(InputStream in=getContentResolver().openInputStream(uri);OutputStream out=openFileOutput("wallpaper.gif",MODE_PRIVATE)){
      byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);
      status.setText("GIF 저장 완료. 이제 '배경화면 적용'을 누르세요.");
    }catch(Exception e){status.setText("GIF를 읽지 못했습니다: "+e.getMessage());}
  }
}