package com.example.nodak_;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class CalendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //캘린더 메인
    public CalendarView calendarView;
    public Button del_Btn,save_Btn;
    public TextView diaryTextView,textView2;
    public EditText contentText;
    public TextView tag;
    public EditText titleText;
    public ImageView imgv;
    private Uri imageUri;

    //헤더
    public ImageView profile_img;
    public TextView nav_header_name;

    public String year,month,day,date;

    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount gsa;

    //2022-12-06 다이얼로그
    AlertDialog.Builder builder;
    //String[] friends;
    public String[] fr;

    //툴바
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //알림
    //2022-12-10 알림
    public static final String NOTIFICATION_CHANNEL_ID = "1001";
    private CharSequence channelName = "노티피케이션 채널";
    private String description = "해당 채널에 대한 설명";

    private int importance = NotificationManager.IMPORTANCE_HIGH;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    String userName = user.getDisplayName();
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("users");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference(userId);



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_act);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //헤더


        save_Btn = (Button) findViewById(R.id.save_Btn);
        del_Btn = (Button) findViewById(R.id.del_Btn);
        titleText = (EditText) findViewById(R.id.title);
        contentText = (EditText) findViewById(R.id.content);
        textView2 = (TextView) findViewById(R.id.textView2);
        imgv = (ImageView) findViewById(R.id.imgv);
        tag = (TextView) findViewById(R.id.tag);

        progressBar = findViewById(R.id.progress_View);
        progressBar.setVisibility(View.INVISIBLE);

        //툴바
        this.settingSideNavBar();


        /*
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        */


        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                titleText.setVisibility(View.VISIBLE);
                imgv.setVisibility(View.VISIBLE);
                contentText.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                tag.setVisibility(View.VISIBLE);
                String trans = "0";

                year = String.valueOf(i);
                // 1~9월 앞에 0 붙이기 (그대로 저장 시 혼동옴. ex: 125가 1월 25일인지 12월 5일인지 모름)
                if (i1 < 9) {
                    String trans1 = String.valueOf(i1+1);
                    trans = trans + trans1;
                    month = trans;
                }
                else{
                    month = String.valueOf(i1+1);
                }

                day = String.valueOf(i2);


                date = year + month + day;

                // 해당 날짜 데이터 읽기
                //if (root.child(userId).orderByChild("schedule").equals(date) == false)
                root.child(userId).child("schedule").child(date).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@Nullable DataSnapshot dataSnapshot) {
                        //해당하는 날짜에 저장된 일정이 있을 경우에 모델에 불러옴
                        if (dataSnapshot.getValue() != null) {
                            ScheduleModel group = dataSnapshot.getValue(ScheduleModel.class);
                            //각각의 값 받아오기 get어쩌구 함수들은 Together_group_list.class에서 지정한것
                            String title = group.getTitle();
                            String content = group.getContent();
                            String tags = group.getTag();
                            String imageurl = group.getImageurl();


                            //텍스트뷰에 받아온 문자열 대입하기
                            titleText.setText(title);
                            contentText.setText(content);
                            tag.setText(tags);
                            //이미지 로드
                            Glide.with(getApplicationContext()).load(imageurl).into(imgv);


                        } else {
                            titleText.setText("");
                            contentText.setText("");
                            tag.setText("태그: ");
                            imgv.setImageResource(R.drawable.ic_add_photo);
                        }
                    }

                    @Override
                    public void onCancelled(@Nullable DatabaseError error) {
                        Toast.makeText(CalendarActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        //태그 다이얼로그
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        //사진 클릭 시 사진 선택
        imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                activityResult_imgv.launch(galleryIntent);
            }
        });

        //업로드버튼 클릭 이벤트
        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //선택한 이미지가 있다면

                if (TextUtils.isEmpty(titleText.getText().toString())) {
                    Toast.makeText(CalendarActivity.this, "제목을 작성해주세요. ", Toast.LENGTH_SHORT).show();

                } else {
                    uploadToFirebase(imageUri);
                }
            }
        });

        // 일정 삭제
        del_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                root.child(userId).child("schedule").child(date).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        reference.child(date + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalendarActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                System.out.println("error: "+exception.getMessage());
                                Toast.makeText(CalendarActivity.this, "이미지 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("error: "+e.getMessage());
                        Toast.makeText(CalendarActivity.this, "저장된 일정이 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
    /***
     *  -> 사이드 네브바 세팅
     *   - 클릭 아이콘 설정
     *   - 아이템 클릭 이벤트 설정
     */
    public void settingSideNavBar()
    {
        // 툴바 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 사이드 메뉴를 오픈하기위한 아이콘 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("nodac");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

        // 사이드 네브바 구현
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(CalendarActivity.this);


        View nav_header_view = navigationView.getHeaderView(0);

        nav_header_name = (TextView) nav_header_view.findViewById(R.id.tv_name);
        //유저 이름
        nav_header_name.setText(userName);

        //저장된 프로필 사진 불러오기
        profile_img = (ImageView) nav_header_view.findViewById(R.id.iv_image);
        root.child(userId).child("user_imageurl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot dataSnapshot) {
                //이미지 저장한게 있으면 불러오기
                if (dataSnapshot.getValue() != null) {
                    String user_img = dataSnapshot.getValue(String.class);
                    Glide.with(getApplicationContext()).load(user_img).into(profile_img);
                }
                else {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CalendarActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
            }
        });

        //프로필 사진 클릭 시 선택
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                activityResult.launch(galleryIntent);

            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                CalendarActivity.this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.closed
        );


        // 사이드 네브바 클릭 리스너
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // -> 사이드 네브바 아이템 클릭 이벤트 설정
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                //프로필 사진만 클릭 인식이 불가능해서 그냥 메뉴 선택 시 적용
                if (id == R.id.nav_friend1){

                    Intent intent = new Intent(getApplicationContext(),friend_page.class);
                    String i = menuItem.getTitle().toString();
                    intent.putExtra("name",i);
                    startActivity(intent);

                }else if(id == R.id.nav_friend2){
                    //drawable로 변경은 가능하나 uri로 불가
                    menuItem.setIcon(R.drawable.ic_launcher_foreground);
                    Intent intent = new Intent(getApplicationContext(),friend_page.class);
                    String i = menuItem.getTitle().toString();
                    intent.putExtra("name",i);
                    startActivity(intent);

                }else if(id == R.id.nav_friend3){

                    Intent intent = new Intent(getApplicationContext(),friend_page.class);
                    String i = menuItem.getTitle().toString();
                    intent.putExtra("name",i);
                    startActivity(intent);
                }
                else if(id == R.id.nav_friend4){

                    Intent intent = new Intent(getApplicationContext(),friend_page.class);
                    String i = menuItem.getTitle().toString();
                    intent.putExtra("name",i);
                    startActivity(intent);
                }
                else if(id == R.id.nav_mycalendar){
                    Intent intent = new Intent(getApplicationContext(),CalendarActivity.class);
                    startActivity(intent);
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                //사이드 메뉴 닫음
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }
    //선택한 프로필 사진 모델에 담고 저장
    ActivityResultLauncher<Intent> activityResult_fr = registerForActivityResult (new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profile_img.setImageURI(imageUri);

                        uploadToFirebase_profile(imageUri);

                    }
                }
            });

    //파이어베이스 프로필사진 업로드
    private void uploadToFirebase_profile(Uri uri){

        //이미지 이름 선언 후 업로드
        StorageReference fileRef = reference.child(userId + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //이미지 모델에 담기
                        ImgModel model = new ImgModel(uri.toString());
                        //키로 아이디 생성
                        String modelId = root.push().getKey();
                        //데이터넣기
                        root.child(userId).child("user_imageurl").setValue(uri.toString());
                        //프로그래스바 숨김
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CalendarActivity.this,"성공", Toast.LENGTH_SHORT).show();
                        profile_img.setImageResource(R.drawable.ic_add_photo);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //프로그래스바 보여주기
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //프로그래스바 숨김
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CalendarActivity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //옵션메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.i2:{ // 알림 클릭
                Intent notificationIntent = new Intent(this, Kakaomap.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );
                NotificationCompat.Builder builder=new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Nodac")
                        .setContentText(titleText.getText())
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis());

                NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    CharSequence channelName = "노티피케이션 채널";
                    String description = "해당 채널에 대한 설명";
                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationChannel channel = new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            channelName,
                            importance
                    );
                    channel.setDescription(description);

                    assert notificationManager != null;
                    notificationManager.createNotificationChannel(channel);
                }


                notificationManager.notify(1234,builder.build());

                return true;
            }
            case R.id.i1:{
                signOut();
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);

    }

    /*//프로필 사진 클릭 시 선택
                profile_img.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            activityResult.launch(galleryIntent);

            if (TextUtils.isEmpty(titleText.getText().toString())) {
                Toast.makeText(CalendarActivity.this, "제목을 작성해주세요. ", Toast.LENGTH_SHORT).show();

            } else {
                uploadToFirebase_profile(imageUri);
            }
        }
    });*/


    //뒤로가기 클릭 시
    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // 일정에서 선택한 사진 모델에 담기
    ActivityResultLauncher<Intent> activityResult_imgv = registerForActivityResult (new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgv.setImageURI(imageUri);
                    }
                }
            });
    //선택한 프로필 사진 모델에 담고 저장
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult (new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profile_img.setImageURI(imageUri);

                        uploadToFirebase_profile(imageUri);

                    }
                }
            });



    //파일 타입 가져오기
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();

        return mine.getExtensionFromMimeType(cr.getType(uri));
    }

    //파이어베이스 일정 업로드
    private void uploadToFirebase(Uri uri){
        //제목 내용 태그 업로드
        String txt = titleText.getText().toString();
        root.child(userId).child("schedule").child(date).child("title").setValue(txt);
        root.child(userId).child("schedule").child(date).child("content").setValue(contentText.getText().toString());
        root.child(userId).child("schedule").child(date).child("tag").setValue(tag.getText().toString());
        //이미지 이름 선언 후 업로드
        if (uri != null){
            StorageReference fileRef = reference.child(date + "." + getFileExtension(uri));
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //이미지 모델에 담기
                            ImgModel model = new ImgModel(uri.toString());
                            //키로 아이디 생성
                            String modelId = root.push().getKey();
                            //데이터넣기
                            root.child(userId).child("schedule").child(date).child("imageurl").setValue(uri.toString());
                            //프로그래스바 숨김
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(CalendarActivity.this,"성공", Toast.LENGTH_SHORT).show();
                            imgv.setImageResource(R.drawable.ic_add_photo);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    //프로그래스바 보여주기
                    progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //프로그래스바 숨김
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CalendarActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(CalendarActivity.this, "이미지 외 변경사항이 저장되었습니다", Toast.LENGTH_SHORT).show();
        }

    }

    //2022-12-06 다이얼로그
    public void showDialog(){
        fr=getResources().getStringArray(R.array.fr);
        builder=new AlertDialog.Builder(CalendarActivity.this);
        builder.setTitle("친구");
        builder.setItems(fr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tag.append(fr [which] + " ");
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.signOut();
                    Toast.makeText(CalendarActivity.this, R.string.success_logout, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    // ...
                });
        gsa = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}