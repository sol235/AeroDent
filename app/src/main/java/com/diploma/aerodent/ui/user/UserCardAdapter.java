package com.diploma.aerodent.ui.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.User;
import com.diploma.aerodent.util.NameUtils;

import java.util.ArrayList;
import java.util.List;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserCardAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textInitials;
        private final TextView textUserName;
        private final TextView textUserRole;
        private final View cardStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitials = itemView.findViewById(R.id.text_avatar_initials);
            textUserName = itemView.findViewById(R.id.text_patient_name);
            textUserRole = itemView.findViewById(R.id.text_patient_phone);
            cardStatus = itemView.findViewById(R.id.card_status);

            if (cardStatus != null) {
                cardStatus.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onUserClick(users.get(position));
                }
            });
        }

        public void bind(User user) {
            textUserName.setText(user.getFullName());
            
            String roleStr = itemView.getContext().getString(user.getRole().getDisplayName());
            if (!user.isActive()) {
                roleStr += " (Неактивен)";
            }
            textUserRole.setText(roleStr);
            
            textInitials.setText(NameUtils.getInitials(user.getFullName()));
        }
    }

}
