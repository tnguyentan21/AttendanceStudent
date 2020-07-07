package com.qc.attendancestudent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.qc.attendancestudent.core.BaseActivity;
import com.qc.attendancestudent.login.LoginFragment;

import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoginActivity extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFragment(R.id.main, new LoginFragment(), "LG");

        toolbar = findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.logout).setOnClickListener(v -> {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có muốn đăng xuất?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build();
                            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                            mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Đăng xuất thành công!!",Toast.LENGTH_SHORT).show();
                                            Intent i=new Intent(getApplicationContext(),SplashscreenActivity.class);
                                            i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        });

        setSupportActionBar(toolbar);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setTitToolbar(String titToolbar) {
        toolbar.setTitle(titToolbar);
    }

    public void setTitToolbarWithIconBack(String titToolbar) {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(titToolbar);
    }
}