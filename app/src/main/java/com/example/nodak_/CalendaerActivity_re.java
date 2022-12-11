package com.example.nodak_;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;


public class CalendaerActivity_re extends AppCompatActivity {
    public CalendarView calendarView;
    public Button del_Btn,save_Btn, out;
    public TextView diaryTextView,textView2,fEditText;
    public EditText contextEditText ;//fEditText;

    public ImageView imgv;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount gsa;



    //2022-12-06 다이얼로그
    AlertDialog.Builder builder;
    //String[] friends;
    public String[] fr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_calendar);

        getSupportActionBar().setTitle("Nodac");
        out = (Button) findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();

        save_Btn = (Button)findViewById(R.id.save_Btn) ;
        del_Btn = (Button)findViewById(R.id.del_Btn) ;
        diaryTextView = (TextView)findViewById(R.id.diaryTextView);
        contextEditText = (EditText) findViewById(R.id.contextEditText);
        //textView2 = (TextView)findViewById(R.id.textView2);
        imgv = (ImageView)findViewById(R.id.imgv);
        fEditText = (TextView) findViewById(R.id.fEditText);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //2022-12-06 다이얼로그
        fEditText=findViewById(R.id.fEditText);
        fEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        calendarView=findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                diaryTextView.setVisibility(View.VISIBLE);
                imgv.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                fEditText.setVisibility(View.VISIBLE);
            }
        });
    }


    //2022-12-06 다이얼로그
    public void showDialog(){
        fr=getResources().getStringArray(R.array.fr);
        builder=new AlertDialog.Builder(CalendaerActivity_re.this);
        builder.setTitle("친구");
        builder.setItems(fr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fEditText.append(fr [which] + " ");
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.signOut();
                    Toast.makeText(CalendaerActivity_re.this, R.string.success_logout, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    // ...
                });
        gsa = null;
    }



}
