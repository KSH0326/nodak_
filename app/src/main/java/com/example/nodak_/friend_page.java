package com.example.nodak_;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class friend_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView f_img;
    Uri imageUri;
    DrawerLayout drawerLayout;

    public ImageView profile_img;
    public TextView nav_header_name;
    String fr_name;

    //디비연결
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();
    String userName = user.getDisplayName();
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("users");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference(userId);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_drawer);

        settingSideNavBar();


        Intent intent = getIntent(); //전달할 데이터를 받을 Intent
        //String을 받아야 하므로 getStringExtra()를 사용함
        fr_name = intent.getStringExtra("name");
        Log.i("정보",fr_name);
        TextView text_tv = findViewById(R.id.friend1_name_textview);
        text_tv.setText(fr_name);

        //저장된 친구 사진이 있는지 확인
        f_img = (ImageView) findViewById(R.id.friend1ImgView);
        root.child(userId).child("friends").child(fr_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot dataSnapshot) {
                //이미지 저장한게 있으면 불러오기
                if (dataSnapshot.getValue() != null) {
                    String user_img = dataSnapshot.getValue(String.class);
                    Glide.with(getApplicationContext()).load(user_img).into(f_img);
                }
                else {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(friend_page.this, "오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
        f_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                activityResult_img.launch(galleryIntent);

            }
        });

    }

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
        navigationView.setNavigationItemSelectedListener(friend_page.this);


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
                Toast.makeText(friend_page.this, "오류 발생", Toast.LENGTH_SHORT).show();
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
                friend_page.this,
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

    //파이어베이스 프로필사진 업로드
    private void uploadToFirebase_profile(Uri uri){
        //이미지 이름 선언 후 업로드
        StorageReference fileRef = reference.child(userName + "." + getFileExtension(uri));
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

                        Toast.makeText(friend_page.this, "성공", Toast.LENGTH_SHORT).show();
                        profile_img.setImageResource(R.drawable.ic_add_photo);
                    }
                });
            }
            });
    }

    //파일 타입 가져오기
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();

        return mine.getExtensionFromMimeType(cr.getType(uri));
    }
    // 친구 사진 모델에 담고 저장
    ActivityResultLauncher<Intent> activityResult_img = registerForActivityResult (new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        f_img.setImageURI(imageUri);

                        uploadToFirebase_friend(imageUri);
                    }
                }
            });
    //파이어베이스 프로필사진 업로드
    private void uploadToFirebase_friend(Uri uri){
        //이미지 이름 선언 후 업로드
        StorageReference fileRef = reference.child(fr_name + "." + getFileExtension(uri));
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
                        root.child(userId).child("friends").child(fr_name).setValue(uri.toString());

                        Toast.makeText(friend_page.this, "성공", Toast.LENGTH_SHORT).show();
                        profile_img.setImageResource(R.drawable.ic_add_photo);
                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
