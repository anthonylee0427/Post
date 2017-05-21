package com.example.anthonylee.post;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class Post extends AppCompatActivity {

    private String uid = "0";
    private String image_url;
    private String voice_url;

    private MediaPlayer mediaPlayer;

    private boolean waitDouble = true;
    private static final int DOUBLE_CLICK_TIME = 350; //兩次單擊的時間間隔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

//        //B.class(接收資料)
//        Bundle bundle = getIntent().getExtras();
//        final int position = bundle.getInt("position");
        final int position = 0;

        final ImageView post = (ImageView) findViewById(R.id.imageButton);
        post.setAdjustViewBounds(true);
        post.setMaxHeight(4000);
        post.setMaxWidth(3000);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("post");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> po = (Map<String,String>)dataSnapshot.child(uid).child(String.valueOf(position)).getValue();
                image_url = po.get("image");
                voice_url = po.get("voice");
                Uri image = Uri.parse(image_url);
                Uri voice = Uri.parse(voice_url);

                Picasso.with(getApplicationContext())
                        .load(image) // 圖片路徑
                        .placeholder(R.mipmap.ic_launcher)  // 圖片讀取完成之前先顯示的佔位圖
                        .error(R.mipmap.ic_launcher)        // 圖片讀取失敗時要顯示的錯誤圖
//                        .resize(300, 400)   // 將圖片寬高轉為200*200 pixel
//                        .centerInside()     // 與resize搭配使用，將調整過的圖片完整塞進ImageView中
//                        .fit()              // 與resize只能擇一使用，將圖片寬高轉為ImageView的大小
                        //.rotate(90)         // 將圖片旋轉90度
                        .into(post);  // 要顯示圖的View

                mediaPlayer = MediaPlayer.create(Post.this,voice);// 建立網路資源音樂檔案Uri物件
                if (mediaPlayer != null){
                    mediaPlayer.start();// 開始播放
                    mediaPlayer.setLooping(true);
                }


                post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( waitDouble == true )
                        {
                            waitDouble = false;
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(DOUBLE_CLICK_TIME);
                                        if ( waitDouble == false ) {
                                            waitDouble = true;
                                            singleClick();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();
                        }
                        else {
                            waitDouble = true;
                            doubleClick();
                        }

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 單擊響應事件
    private void singleClick(){
        // 暫停播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        Log.i("DoubleClickTest", "singleClick");
    }

    // 雙擊響應事件
    private void doubleClick(){
        // 開始播放
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        Log.i("DoubleClickTest", "doubleClick");
    }

}
