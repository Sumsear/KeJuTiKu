package com.example.hp.keju.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.keju.R;
import com.example.hp.keju.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private List<QuestionEntity> data = new ArrayList<>();

    public AnswerAdapter() {
    }

    @NonNull
    @Override
    public AnswerAdapter.AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.AnswerViewHolder holder, int position) {

        QuestionEntity entity = getItem(position);

        holder.setQuestion(String.format("问题: %s", entity.getQ()));
        holder.setAnswer(String.format("答案: %s", entity.getA()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public QuestionEntity getItem(int position) {
        return this.data.get(position);
    }

    public void setData(List<QuestionEntity> data) {
        if (data != null) {
            this.data.clear();
            this.data.addAll(data);
            this.notifyDataSetChanged();
        }
    }

    public void addData(List<QuestionEntity> data) {
        if (data != null) {
            this.data.addAll(data);
            this.notifyDataSetChanged();
        }
    }


    class AnswerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvQ;
        private TextView tvA;

        AnswerViewHolder(View itemView) {
            super(itemView);

            tvQ = itemView.findViewById(R.id.item_tv_q);
            tvA = itemView.findViewById(R.id.item_tv_a);
        }

        void setQuestion(String q) {
            if (q != null) tvQ.setText(q);
        }

        void setAnswer(String a) {
            if (a != null) tvA.setText(a);
        }
    }


}
