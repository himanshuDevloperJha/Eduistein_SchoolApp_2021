package com.cygnus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cygnus.R;
import com.cygnus.feesmanage.AddFeeStructure;
import com.cygnus.feesmanage.TotalFees;
import com.cygnus.model.ClassFees;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ClasswiseTotalfeeAdapter extends RecyclerView.Adapter<ClasswiseTotalfeeAdapter.ViewHolder> {

    Context context;
    List<String> feelist;
    TotalFees totalFees;
    int fees = 0;

    public ClasswiseTotalfeeAdapter(Context context, List<String> feelist) {
        this.context = context;
        this.feelist = feelist;



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_classtotalfee, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


//        holder.tv_totalfee.setText("Total Fee: \u20B9 "+feelist.get(position).getFeeCharges());
        holder.tv_classtotalfee.setText(feelist.get(position));
      //  fees = fees + Integer.parseInt(feelist.get(position).getFeeCharges());
        // Toast.makeText(context, "Fees::" + fees, Toast.LENGTH_SHORT).show();
      //  totalFees.studentTotalFees(position, fees);

        holder.ll_classFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AddFeeStructure.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("FeeClassID",feelist.get(position));
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return feelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
LinearLayout ll_classFee;
        TextView  tv_totalfee,tv_classtotalfee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_classtotalfee = itemView.findViewById(R.id.tv_classtotalfee);
            tv_totalfee = itemView.findViewById(R.id.tv_totalfee);
            ll_classFee = itemView.findViewById(R.id.ll_classFee);

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