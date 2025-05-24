package com.example.tp7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.AdviceViewHolder> {
    private List<Advice> adviceList;
    private String currentUserId;
    private OnAdviceActionListener listener;
    //pour gerer edit and delete
    public interface OnAdviceActionListener {
        void onEdit(Advice advice);
        void onDelete(Advice advice);
    }

    public AdviceAdapter(List<Advice> adviceList, String currentUserId, OnAdviceActionListener listener) {
        this.adviceList = adviceList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advice_item, parent, false);
        return new AdviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdviceViewHolder holder, int position) {
        Advice advice = adviceList.get(position);
        holder.bind(advice, currentUserId, listener);
    }

    @Override
    public int getItemCount() {
        return adviceList.size();
    }

    public static class AdviceViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription;
        Button btnEdit, btnDelete;
//advice item
        public AdviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtAdviceTitle);
            txtDescription = itemView.findViewById(R.id.txtAdviceDetails);
            btnEdit = itemView.findViewById(R.id.btnEditAdvice);
            btnDelete = itemView.findViewById(R.id.btnDeleteAdvice);
        }
//recupere les données à afficher
        public void bind(Advice advice, String currentUserId, OnAdviceActionListener listener) {
            txtTitle.setText(advice.getTitle() != null ? advice.getTitle() : "Titre indisponible");
            txtDescription.setText(advice.getDescription() != null ? advice.getDescription() : "Description indisponible");
//ken nafsou eli cree ladvice nafsou eli connecter naffichilou edit et supp
            if (advice.getCreatedBy() != null && advice.getCreatedBy().equals(currentUserId)) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);

                btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(advice);
                });
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(advice);
                });
            } else { //sinon on cache
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnEdit.setOnClickListener(null);
                btnDelete.setOnClickListener(null);
            }
        }
    }
}
