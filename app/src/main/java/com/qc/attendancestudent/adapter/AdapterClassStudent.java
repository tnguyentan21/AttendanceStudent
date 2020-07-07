package com.qc.attendancestudent.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qc.attendancestudent.R;
import com.qc.attendancestudent.model.ClassAttendanceData;
import com.qc.attendancestudent.model.Student;

import org.jetbrains.annotations.NotNull;

public class AdapterClassStudent extends BaseQuickAdapter<Student, BaseViewHolder> {

    public AdapterClassStudent() {
        super(R.layout.item_list_student);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Student student) {
        ( (TextView) baseViewHolder.getView(R.id.studentName)).setText(student.gethoTen());
        ImageView imageView = baseViewHolder.getView(R.id.statusAttendance);

        if (student.getisHaveDiemDanh()) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}