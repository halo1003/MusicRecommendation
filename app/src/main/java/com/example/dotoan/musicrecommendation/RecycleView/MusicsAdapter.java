package com.example.dotoan.musicrecommendation.RecycleView;

import android.app.DownloadManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dotoan.musicrecommendation.Contruct.Mdetail;
import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.Contruct.itemC;
import com.example.dotoan.musicrecommendation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/19/2017.
 */

public class MusicsAdapter extends RecyclerView.Adapter<MusicsAdapter.RecyclerViewHolder>{

    private List<MusicC> data = new ArrayList<MusicC>();
    private Context mContext;

    public MusicsAdapter(List<MusicC> data,Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_music, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        holder.txtv_id.setText(data.get(position).get_id()+"");
        holder.txtv_mid.setText(data.get(position).getMid()+"");
        holder.txtv_Aname.setText(data.get(position).getAname());
        holder.txtv_Rm.setText(data.get(position).getMname());
        holder.li_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null) {

                    itemC i = new itemC();
                    i.setId(holder.txtv_id.getText().toString());
                    i.setMid(holder.txtv_mid.getText().toString());
                    i.setArtic(holder.txtv_Aname.getText().toString());
                    i.setName(holder.txtv_Rm.getText().toString());

                    onItemClickedListener.onItemClick(i);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_id;
        TextView txtv_mid;
        TextView txtv_Rm;
        TextView txtv_Aname;
        LinearLayout li_btn;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            txtv_id = (TextView) itemView.findViewById(R.id.id_music);
            txtv_mid = (TextView) itemView.findViewById(R.id.name_music);
            txtv_Rm = (TextView)itemView.findViewById(R.id.Realname_music);
            txtv_Aname = (TextView)itemView.findViewById(R.id.A_music);
            li_btn = (LinearLayout)itemView.findViewById(R.id.Li_btn);
        }
    }

    public interface OnItemClickedListener {
        void onItemClick(itemC itemC);
    }

    private OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}