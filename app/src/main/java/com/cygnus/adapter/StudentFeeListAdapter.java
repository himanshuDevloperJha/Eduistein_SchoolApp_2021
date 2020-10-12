package com.cygnus.adapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cygnus.R;
import com.cygnus.feesmanage.TotalFees;
import com.cygnus.model.ClassFees;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;


public class StudentFeeListAdapter extends RecyclerView.Adapter<StudentFeeListAdapter.ViewHolder> {

    Context context;
    List<ClassFees> feelist;
    List<String> feekeylist;
    TotalFees totalFees;
    int fees=0;
    public StudentFeeListAdapter(Context context, List<ClassFees> feelist,List<String> feekeylist,
                                 TotalFees totalFees) {
        this.context = context;
        this.feelist = feelist;
        this.totalFees = totalFees;
        this.feekeylist = feekeylist;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_studentfee, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.tv_classname.setText(feelist.get(position).getFeeType());
        holder.tv_feesss.setText("\u20B9 "+feelist.get(position).getFeeCharges());
          fees = fees + Integer.parseInt(feelist.get(position).getFeeCharges());
        // Toast.makeText(context, "Fees::" + fees, Toast.LENGTH_SHORT).show();
         totalFees.studentTotalFees(position, fees);

         holder.iv_editfee.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               totalFees.editfees(position,feelist.get(position).getFeeCharges(),
                       feelist.get(position).getFeeCharges(),feekeylist.get(position));
             }
         });

         holder.tv_recfee.setText(feelist.get(position).getRecursiveFees());

    }


    @Override
    public int getItemCount() {
        return feelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_classname,tv_feesss,tv_recfee;
        AppCompatImageView iv_editfee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_feesss = itemView.findViewById(R.id.tv_feesss);
            tv_classname = itemView.findViewById(R.id.tv_classname);
            iv_editfee = itemView.findViewById(R.id.iv_editfee);
            tv_recfee = itemView.findViewById(R.id.tv_recfee);

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