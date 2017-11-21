package com.example.dotoan.musicrecommendation.RecycleView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dotoan.musicrecommendation.Contruct.MusicC;
import com.example.dotoan.musicrecommendation.R;

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
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btn.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorAccent));
                if (onItemClickedListener != null) {

                    MusicC musicC = new MusicC();

                    musicC.set_id(Integer.parseInt(holder.txtv_id.getText().toString()));
                    musicC.setMid(holder.txtv_mid.getText().toString());

                    onItemClickedListener.onItemClick(musicC);
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
        Button btn;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            txtv_id = (TextView) itemView.findViewById(R.id.id_music);
            txtv_mid = (TextView) itemView.findViewById(R.id.name_music);
            btn = (Button)itemView.findViewById(R.id.music_button);
        }
    }

    public interface OnItemClickedListener {
        void onItemClick(MusicC musicC);
    }

    private OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}