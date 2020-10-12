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
import com.cygnus.model.FeeHistory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FeeHistoryAdapter extends RecyclerView.Adapter<FeeHistoryAdapter.ViewHolder> {

    Context context;
    List<FeeHistory> feelist;

    public FeeHistoryAdapter(Context context, List<FeeHistory> feelist) {
        this.context = context;
        this.feelist = feelist;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_feehistory, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


//        holder.tv_totalfee.setText("Total Fee: \u20B9 "+feelist.get(position).getFeeCharges());
        holder.tv_paidon.setText("Paid on " + feelist.get(position).getFeeDate());
        holder.tv_feemonth.setText(feelist.get(position).getFeeMonth());
        holder.tv_feeeamount.setText("\u20B9 " + feelist.get(position).getFeeAmount());
        //  fees = fees + Integer.parseInt(feelist.get(position).getFeeCharges());
        // Toast.makeText(context, "Fees::" + fees, Toast.LENGTH_SHORT).show();
        //  totalFees.studentTotalFees(position, fees);


    }


    @Override
    public int getItemCount() {
        return feelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_feeeamount, tv_feemonth, tv_paidon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_feemonth = itemView.findViewById(R.id.tv_feemonth);
            tv_feeeamount = itemView.findViewById(R.id.tv_feeeamount);
            tv_paidon = itemView.findViewById(R.id.tv_paidon);

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