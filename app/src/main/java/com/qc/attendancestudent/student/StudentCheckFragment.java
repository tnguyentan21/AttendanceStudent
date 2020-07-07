package com.qc.attendancestudent.student;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qc.attendancestudent.R;
import com.qc.attendancestudent.core.BaseFragment;
import com.qc.attendancestudent.model.ClassAttendanceData;
import com.qc.attendancestudent.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.nlopez.smartlocation.SmartLocation;


public class StudentCheckFragment extends BaseFragment {

    private FirebaseDatabase database;
    private Button buttonScan, buttonChangeIF;
    private TextView textViewName, textViewMssv, textViewDiemdanh, lopTV;

    private Student sVien;
    private String statusDiemDanh = "Chưa điểm danh";
    private String maLop;
    private String keyQR;
    private Location locationSV;
    private ClassAttendanceData dsLop;
    private Boolean initData = false;
    private String email;

    public StudentCheckFragment() {
    }

    public static StudentCheckFragment newInstance(String email) {
        StudentCheckFragment fragment = new StudentCheckFragment();
        Bundle args = new Bundle();
        args.putString("Email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString("Email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_check, container, false);
        buttonScan = (Button) view.findViewById(R.id.buttonScan);
        textViewName = (TextView) view.findViewById(R.id.hoten);
        textViewDiemdanh = view.findViewById(R.id.diemdanh);
        textViewMssv = (TextView) view.findViewById(R.id.mssv);
        buttonChangeIF = view.findViewById(R.id.changeInfo);
        lopTV = view.findViewById(R.id.lopTV);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        getSinhVien();
        buttonScan.setOnClickListener(v -> IntentIntegrator.forSupportFragment(StudentCheckFragment.this).initiateScan());
        buttonChangeIF.setOnClickListener(v -> {
            initData = false;
            getSinhVien();
        });
    }

    @SuppressLint("SetTextI18n")
    private void getSinhVien() {
        if (!initData) {
            int index;
            switch (email) {
                case "nttdemo001@gmail.com": index = 0; break;
                case "nttdemo002@gmail.com": index = 1; break;
                case "nttdemo003@gmail.com": index = 2; break;
                default: index = new Random().nextInt(9);
            }
            sVien = getStudent(index);
            textViewMssv.setText(
                    "" + sVien.getmSSV());
            textViewName.setText(sVien.gethoTen());
            setStatusDiemDanh(statusDiemDanh);
            lopTV.setText(sVien.getmaLop());
            initData = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setStatusDiemDanh(String str) {
        textViewDiemdanh.setText(str);
    }

    private void listenerDataChange(String maLop) {
        showLoading();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dsLop = new Gson().fromJson(new Gson().toJson(snapshot.getValue()), new TypeToken<ClassAttendanceData>() {
                }.getType());
                hideLoading();
                diemDanh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.getReference(DB_NAME).child(maLop).addListenerForSingleValueEvent(valueEventListener);
    }

    private void diemDanh() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showDialog("Vui lòng kiểm tra cấp quyền truy cập vị trí!");
        } else {
            if (!SmartLocation.with(getContext()).location().state().isGpsAvailable()) {
                showDialog("Vui lòng bật GPS!");
            } else {
                if (Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ALLOW_MOCK_LOCATION).equals("1")) {
                    showDialog("Vui lòng vào tuỳ chọn nhà phát triển tắt giả lập vị trí");
                } else {
                    if (dsLop.getisHaveDiemDanh()) {
                        for (Student std : dsLop.getStudentList()) {
                            if (std.getmSSV() == sVien.getmSSV() && maLop.equals(std.getmaLop())) {
                                if (dsLop.getKeyClass().equals(keyQR)) {
                                    SmartLocation.with(getContext()).location()
                                            .oneFix()
                                            .start(location -> {
                                                float a = locationSV.distanceTo(location);
                                                if (locationSV.distanceTo(location) <= 250) {
                                                    std.setStatus(true);

                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put(dsLop.getMaLop(), dsLop);

                                                    database.getReference(DB_NAME).updateChildren(map);

                                                    showDialog("Điểm danh thành công!!!");
                                                    setStatusDiemDanh("Đã điểm danh");
                                                } else {
                                                    showDialog("Không thể điểm danh! Bạn ở quá xa vi trí của giảng viên!");
                                                }
                                            } );
                                } else {
                                    showDialog("Bạn không thể điểm danh bằng mã QR cũ!");
                                }
                                break;
                            }
                        }
                    } else {
                        hideLoading();
                        showDialog("Hiện tại lớp không thể điểm danh");
                    }
                }
            }
        }

    }

    private Student getStudent(int index) {
        List<Student> students1 = new ArrayList<>();
        students1.add(new Student("CNTT01", 12345611, "Tuan Minh", false));
        students1.add(new Student("CNTT01", 12345671, "Minh Tuan", false));
        students1.add(new Student("CNTT01", 12345678, "Hoang Minh", false));

        students1.add(new Student("CNTT02", 45678911, "Nguyen Tuấn Trịnh", false));
        students1.add(new Student("CNTT02", 46578911, "Hoàng Minh Tâm", false));
        students1.add(new Student("CNTT02", 45678912, "Trần Sang Sang", false));

        students1.add(new Student("CNTT03", 78945632, "Le Thi Thu Hang", false));
        students1.add(new Student("CNTT03", 78945612, "Nguyen Huynh Tu", false));
        students1.add(new Student("CNTT03", 78945612, "Tran Sieng Nang", false));

        return students1.get(index);
    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData = true;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                HashMap<String, String> map = new Gson().fromJson(result.getContents(), new TypeToken<HashMap<String, String>>() {
                }.getType());
                if (Objects.equals(map.get("Lop"), sVien.getmaLop())) {
                    maLop = map.get("Lop");
                    keyQR = map.get("MaQR");

                    locationSV = new Location("SV");

                    locationSV.setLatitude(Float.parseFloat(Objects.requireNonNull(map.get("Lat"))));
                    locationSV.setLongitude(Float.parseFloat(Objects.requireNonNull(map.get("Lng"))));

                    listenerDataChange(maLop);
                } else {
                    showDialog("Bạn không phải sinh viên lớp này!!");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}