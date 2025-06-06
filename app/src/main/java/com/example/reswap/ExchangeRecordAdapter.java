package com.example.reswap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExchangeRecordAdapter extends RecyclerView.Adapter<ExchangeRecordAdapter.ViewHolder> {

    public static class ExchangeRecord {
        public String productName, receiver, address, contact, time;

        public ExchangeRecord(String productName, String receiver, String address, String contact, String time) {
            this.productName = productName;
            this.receiver = receiver;
            this.address = address;
            this.contact = contact;
            this.time = time;
        }
    }

    private Context context;
    private List<ExchangeRecord> recordList;

    public ExchangeRecordAdapter(Context context, List<ExchangeRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProduct, tvReceiver, tvAddress, tvContact, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvReceiver = itemView.findViewById(R.id.tvReceiver);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(ExchangeRecord record) {
            tvProduct.setText("📦 商品：" + record.productName);
            tvReceiver.setText("👤 收件人：" + record.receiver);
            tvAddress.setText("🏠 地址：" + record.address);
            tvContact.setText("📞 電話：" + record.contact);
            tvTime.setText("🕒 時間：" + record.time);
        }
    }

    @Override
    public ExchangeRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exchange_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExchangeRecordAdapter.ViewHolder holder, int position) {
        holder.bind(recordList.get(position));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
