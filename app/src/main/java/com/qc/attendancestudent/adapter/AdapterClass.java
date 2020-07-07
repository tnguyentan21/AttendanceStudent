package com.qc.attendancestudent.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qc.attendancestudent.R;
import com.qc.attendancestudent.model.ClassAttendanceData;

import org.jetbrains.annotations.NotNull;

public class AdapterClass extends BaseQuickAdapter<ClassAttendanceData, BaseViewHolder> {

    public AdapterClass() {
        super(R.layout.item_class);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ClassAttendanceData contactModel) {
        ( (TextView) baseViewHolder.getView(R.id.classTV)).setText(contactModel.gettenLop());
    }
}