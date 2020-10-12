package com.cygnus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cygnus.R;
import com.cygnus.feesmanage.TotalFees;
import com.cygnus.model.ClassFees;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class StudentFeeDetailAdapter extends RecyclerView.Adapter<StudentFeeDetailAdapter.ViewHolder> {

    Context context;
    List<ClassFees> feelist;
    TotalFees totalFees;
    int fees = 0;

    public StudentFeeDetailAdapter(Context context, List<ClassFees> feelist,
                                   TotalFees totalFees) {
        this.context = context;
        this.feelist = feelist;
        this.totalFees = totalFees;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_student_feedetail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.tv_feetype.setText(feelist.get(position).getFeeType());
        holder.tv_feeamt.setText("\u20B9 " + feelist.get(position).getFeeCharges());
        fees = fees + Integer.parseInt(feelist.get(position).getFeeCharges());
        // Toast.makeText(context, "Fees::" + fees, Toast.LENGTH_SHORT).show();
        totalFees.studentTotalFees(position, fees);

        holder.tv_recfee1.setText(feelist.get(position).getRecursiveFees());

    }


    @Override
    public int getItemCount() {
        return feelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_feeamt, tv_feetype,tv_recfee1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_feetype = itemView.findViewById(R.id.tv_feetype);
            tv_feeamt = itemView.findViewById(R.id.tv_feeamt);
            tv_recfee1 = itemView.findViewById(R.id.tv_recfee1);

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}