package com.qc.attendancestudent.techer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.opencsv.CSVWriter;
import com.qc.attendancestudent.LoginActivity;
import com.qc.attendancestudent.R;
import com.qc.attendancestudent.adapter.AdapterClassStudent;
import com.qc.attendancestudent.core.BaseFragment;
import com.qc.attendancestudent.model.ClassAttendanceData;
import com.qc.attendancestudent.model.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.nlopez.smartlocation.SmartLocation;

public class GVCLassFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    FirebaseDatabase database;
    AdapterClassStudent adapterClass;
    RecyclerView recyclerView;
    Button qrCode, taoQR, xuatDS;
    Switch aSwitch;
    Boolean firstShowQR = false;
    AlertDialog alertDialog;

    private List<Student> mParam1;
    private String mParam2;
    private Boolean mStatus;
    private Bitmap bitmap;
    private List<Student> studentList;
    private String maBaoMatQR;
    public GVCLassFragment() {
        // Required empty public constructor
    }

    public static GVCLassFragment newInstance(String param1, String param2, Boolean param3) {
        GVCLassFragment fragment = new GVCLassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = new Gson().fromJson(getArguments().getString(ARG_PARAM1), new TypeToken<List<Student>>() {}.getType());
            mParam2 = getArguments().getString(ARG_PARAM2);
            mStatus = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_g_v_c_lass, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LoginActivity) Objects.requireNonNull(getActivity()))
                .setTitToolbarWithIconBack(mParam2);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();

        database = FirebaseDatabase.getInstance();
        adapterClass = new AdapterClassStudent();
        adapterClass.setEmptyView(R.layout.empty_view);

        recyclerView = view.findViewById(R.id.recyclerViewStudent);
        qrCode = view.findViewById(R.id.qrCode);
        aSwitch = view.findViewById(R.id.switchView);
        taoQR = view.findViewById(R.id.taoQR);
        xuatDS = view.findViewById(R.id.xuatDs);

        aSwitch.setChecked(mStatus);

        taoQRCode();

        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRCode();
            }
        });

        taoQR.setOnClickListener(v -> {
            taoQRCode();
        });

        xuatDS.setOnClickListener(v -> {
            xuatDS();
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("isHaveDiemDanh", isChecked);
                database.getReference(DB_NAME).child(mParam2).updateChildren(map);
            }
        });

        RecyclerView.LayoutManager layoutManagerES = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManagerES);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(adapterClass);

    }

    private void taoQRCode() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            showDialog("Vui lòng kiểm tra cấp quyền truy cập vị trí!");
        } else {
            if ( !SmartLocation.with(getContext()).location().state().isGpsAvailable()) {
                showDialog("Vui lòng bật GPS!");
            } else {
                if (Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ALLOW_MOCK_LOCATION).equals("1")) {
                    showDialog("Vui lòng vào tuỳ chọn nhà phát triển tắt giả lập vị trí");
                } else {
                    SmartLocation.with(getContext()).location()
                            .oneFix()
                            .start(location -> {
                                try {
                                    maBaoMatQR = UUID.randomUUID().toString();
                                    listenerData();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("Lop", mParam2);
                                    map.put("MaQR", maBaoMatQR);
                                    map.put("Lat", location.getLatitude());
                                    map.put("Lng", location.getLongitude());

                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.encodeBitmap(new Gson().toJson(map), BarcodeFormat.QR_CODE, 600, 600);

                                    View view = View.inflate(getContext(), R.layout.qr_code_layout, null);
                                    ImageView img = view.findViewById(R.id.imageQR);
                                    img.setImageDrawable(null);
                                    img.setImageBitmap(bitmap);

                                    alertDialog = new AlertDialog.Builder(getContext())
                                            .setView(view)
                                            .setPositiveButton("Chia sẽ", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    shareImageUri(Uri.parse(MediaStore.Images.Media.insertImage(Objects.requireNonNull(getActivity()).getContentResolver(), bitmap,"QRCode_DiemDanh_"+mParam2, null)));
                                                }
                                            })
                                            .setNegativeButton("Đóng", new DatePickerDialog.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                            .setIcon(android.R.drawable.ic_dialog_alert).show();

                                    database.getReference(DB_NAME).child(mParam2).child("keyClass").setValue(maBaoMatQR);
                                    database.getReference(DB_NAME).child(mParam2).child("Lat").setValue(location.getLatitude());
                                    database.getReference(DB_NAME).child(mParam2).child("Lng").setValue(location.getLongitude());

                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }
        }
    }

    private void listenerData() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        ClassAttendanceData classStudent = new Gson().fromJson(new Gson().toJson(postSnapshot.getValue()), new TypeToken<ClassAttendanceData>() {}.getType());
                        if (classStudent.getMaLop().equals(mParam2)) {
                            checkSVDiemDanh(classStudent.getStudentList());
                            studentList = classStudent.getStudentList();
                            adapterClass.setNewInstance(classStudent.getStudentList());
                            adapterClass.notifyDataSetChanged();
                            hideLoading();
                            break;
                        }
                    }
                    if (!firstShowQR) {
                        showQRCode();
                        firstShowQR = true;
                    }
                } catch (Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DiemDanh", error.getMessage()); //Don't ignore errors!
            }
        };

        database.getReference(DB_NAME).addValueEventListener(valueEventListener);
    }

    private void showQRCode() {
        alertDialog.show();
    }

    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }

    private void checkSVDiemDanh(List<Student> students) {
        if (studentList == null) return;
        for (int i =0; i< studentList.size() - 1 ; i++) {
            if (students.get(i).getisHaveDiemDanh() != studentList.get(i).getisHaveDiemDanh() && students.get(i).getisHaveDiemDanh()) {
                try {
                    Toast.makeText(getContext(), students.get(i).gethoTen() + " điểm danh", Toast.LENGTH_SHORT).show();
                } catch (NullPointerException ex){}
            }
        }
    }

    private void xuatDS() {
        if (studentList == null || studentList.isEmpty()) {
            showDialog("Không có sinh viên!");
            return;
        }
        FileWriter mFileWriter = null;
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String baseDir;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            baseDir = Objects.requireNonNull(Objects.requireNonNull(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath());
        } else {
            baseDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        String fileName = "DiemDanh" + mParam2 + ".csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer = null;

        // File exist
        if(f.exists()&&!f.isDirectory())
        {
            try {
                mFileWriter = new FileWriter(filePath, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);
        }
        else
        {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @SuppressLint("SimpleDateFormat") String[] ngayDD = new String[]{"Danh Sach Diem Danh - " + new SimpleDateFormat("dd/MM/yyyy").format(new Date().getTime())};
        assert writer != null;
        writer.writeNext(ngayDD);
        String[] title = new String[]{"Ho Ten", "Diem Danh"};
        writer.writeNext(title);

        for (Student st: studentList) {
            String[] entries;
            String statusDD = "Vang";
            if (st.getisHaveDiemDanh()) {
                statusDD = "Co";
            }
            entries = new String[]{convert(st.gethoTen()), statusDD};
            writer.writeNext(entries);
        }

        try {
            writer.close();
            Toast.makeText(getContext(), "Xuât danh sách thành công", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convert(String str) {
        str = str.replaceAll("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ", "a");
        str = str.replaceAll("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ", "e");
        str = str.replaceAll("ì|í|ị|ỉ|ĩ", "i");
        str = str.replaceAll("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ", "o");
        str = str.replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u");
        str = str.replaceAll("ỳ|ý|ỵ|ỷ|ỹ", "y");
        str = str.replaceAll("đ", "d");

        str = str.replaceAll("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ", "A");
        str = str.replaceAll("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ", "E");
        str = str.replaceAll("Ì|Í|Ị|Ỉ|Ĩ", "I");
        str = str.replaceAll("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ", "O");
        str = str.replaceAll("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ", "U");
        str = str.replaceAll("Ỳ|Ý|Ỵ|Ỷ|Ỹ", "Y");
        str = str.replaceAll("Đ", "D");
        return str;
    }

}