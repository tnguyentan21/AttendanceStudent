package com.qc.attendancestudent.techer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qc.attendancestudent.R;
import com.qc.attendancestudent.adapter.AdapterClass;
import com.qc.attendancestudent.core.BaseFragment;
import com.qc.attendancestudent.model.ClassAttendance;
import com.qc.attendancestudent.model.ClassAttendanceData;
import com.qc.attendancestudent.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiaoVienFragment extends BaseFragment {

    FirebaseDatabase database;
    AdapterClass adapterClass;
    List<ClassAttendanceData> list;
    RecyclerView recyclerView;
    Button refresh;

    public GiaoVienFragment() {
        // Required empty public constructor
    }

    public static GiaoVienFragment newInstance() {
        return new GiaoVienFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_giao_vien, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();

        database = FirebaseDatabase.getInstance();
        refresh = view.findViewById(R.id.buttonRefresh);
        adapterClass = new AdapterClass();
        adapterClass.setEmptyView(R.layout.empty_view);

        adapterClass.setOnItemClickListener((adapter, view1, position) -> replaceFragment(R.id.main, GVCLassFragment.newInstance(new Gson().toJson(list.get(position).getStudentList()), list.get(position).getMaLop(), list.get(position).getisHaveDiemDanh()), "StudentList", "GV"));

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManagerES = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManagerES);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(adapterClass);

        refresh.setOnClickListener(v -> initData());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    try {
                        ClassAttendanceData classStudent = new Gson().fromJson(new Gson().toJson(postSnapshot.getValue()), new TypeToken<ClassAttendanceData>() {}.getType());
                        list.add(classStudent);
                    } catch (Exception ignored){}
                }
                adapterClass.setNewInstance(list);
                adapterClass.notifyDataSetChanged();
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DiemDanh", error.getMessage()); //Don't ignore errors!
            }
        };
        database.getReference(DB_NAME).addValueEventListener(valueEventListener);
    }

    private List<ClassAttendance> classData() {
        List<ClassAttendance> attendances = new ArrayList<>();
        attendances.add(new ClassAttendance("CNTT01", "Lớp CNTT 01", false, "", "", "0","0"));
        attendances.add(new ClassAttendance("CNTT02", "Lớp CNTT 02", false, "", "", "0","0"));
        attendances.add(new ClassAttendance("CNTT03", "Lớp CNTT 03", false, "", "", "0","0"));

        return attendances;
    }

    private void initData() {
        for (ClassAttendance attendance : classData()) {
            database.getReference(DB_NAME).child(attendance.getMaLop()).setValue(attendance);
        }
        initStudent();
    }

    private void initStudent() {

        List<Student> students1 = new ArrayList<>();
        students1.add(new Student("CNTT01", 12345611, "Tuan Minh", false));
        students1.add(new Student("CNTT01", 12345671, "Minh Tuan", false));
        students1.add(new Student("CNTT01", 12345678, "Hoang Minh", false));

        List<Student> students2 = new ArrayList<>();
        students2.add(new Student("CNTT02", 45678911, "Nguyen Tuấn Trịnh", false));
        students2.add(new Student("CNTT02", 46578931, "Hoàng Minh Tâm", false));
        students2.add(new Student("CNTT02", 45678912, "Trần Sang Sang", false));


        List<Student> students3 = new ArrayList<>();
        students3.add(new Student("CNTT03", 78945632, "Le Thi Thu Hang", false));
        students3.add(new Student("CNTT03", 78945642, "Nguyen Huynh Tu", false));
        students3.add(new Student("CNTT03", 78945612, "Tran Sieng Nang", false));

        Map<String, Object> map = new HashMap<>();
        map.put("studentList", students1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("studentList", students2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("studentList", students3);

        database.getReference(DB_NAME).child("CNTT02").updateChildren(map2);
        database.getReference(DB_NAME).child("CNTT01").updateChildren(map);
        database.getReference(DB_NAME).child("CNTT03").updateChildren(map3);

    }
}