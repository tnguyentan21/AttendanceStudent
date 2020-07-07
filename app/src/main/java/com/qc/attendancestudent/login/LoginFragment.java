package com.qc.attendancestudent.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.qc.attendancestudent.LoginActivity;
import com.qc.attendancestudent.R;
import com.qc.attendancestudent.core.BaseFragment;
import com.qc.attendancestudent.student.StudentCheckFragment;
import com.qc.attendancestudent.techer.GiaoVienFragment;

import java.util.Objects;

public class LoginFragment extends BaseFragment implements
        View.OnClickListener {

    private GoogleSignInClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private SignInButton signInButton;
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signInButton = view.findViewById(R.id.btnSignIn);

        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);


        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LoginActivity) Objects.requireNonNull(getActivity()))
                .setTitToolbar("Đăng Nhập");

    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getContext()));
        if (account != null) {
            if (Objects.equals(account.getEmail(), "nttdemo20@gmail.com")) {
                replaceFragment(R.id.main , GiaoVienFragment.newInstance(), "GV", "/");
            } else {
                replaceFragment(R.id.main, StudentCheckFragment.newInstance(account.getEmail()), "SV", "/");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;
            if (Objects.equals(acct.getEmail(), "nttdemo20@gmail.com")) {
                replaceFragment(R.id.main , GiaoVienFragment.newInstance(), "GV", "/");
            } else {
                replaceFragment(R.id.main, StudentCheckFragment.newInstance(acct.getEmail()), "SV", "/");
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleApiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {
            signIn();
        }
    }
}