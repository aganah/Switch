package umn.ac.id.aswitch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<TransHandler> histori;

    public HistoryAdapter(ArrayList<TransHandler> lists) {
        histori = lists;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.historylist,parent,false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.jenis.setText(histori.get(position).getJenis());
        holder.nominal.setText(histori.get(position).getNominal().toString());
        holder.tanggal.setText(histori.get(position).getTanggal());
        if(histori.get(position).getPenerima().isEmpty()){
            holder.user.setText(histori.get(position).getrId());
        }else{
            holder.user.setText(histori.get(position).getrId()+" -> "+histori.get(position).getPenerima());
        }
    }

    @Override
    public int getItemCount() {
        return histori.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tanggal, nominal, jenis, user;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tanggal = itemView.findViewById(R.id.tanggal_history);
            nominal = itemView.findViewById(R.id.nominal_history);
            jenis = itemView.findViewById(R.id.jenis_history);
            user = itemView.findViewById(R.id.namauser_history);
        }
    }
}
