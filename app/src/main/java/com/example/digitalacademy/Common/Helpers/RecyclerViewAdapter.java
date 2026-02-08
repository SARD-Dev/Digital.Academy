package com.example.digitalacademy.Common.Helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalacademy.R;

import java.util.List;
import java.util.function.Function;

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder<T>> {

    private final List<T> recycleViewItems;
    private final Function<T, String> titleMapper;
    private final Function<T, String> contentMapper;
    private final OnClickListener<T> listener;

    /// Interface for click event callback
    public interface OnClickListener<T> {
        void onClick(T item, int position);
    }

    public RecyclerViewAdapter(List<T> list,
                               Function<T, String> titleMapper,
                               Function<T, String> contentMapper,
                               OnClickListener<T> listener) {
        this.recycleViewItems = list;
        this.titleMapper = titleMapper;
        this.contentMapper = contentMapper;
        this.listener = listener;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return ViewHolder that holds a view of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_card, parent, false);
        return new ViewHolder<>(view);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder<T> holder, int position) {
        T item = recycleViewItems.get(position);
        holder.bind(
                item,
                listener,
                position,
                titleMapper != null
                        ? titleMapper.apply(item)
                        : item instanceof String
                        ? (String) item : "",
                contentMapper != null
                        ? contentMapper.apply(item)
                        : ""
        );
    }

    /**
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return recycleViewItems.size();
    }

    /// ViewHolder class for the RecyclerView
    public static class ViewHolder<T> extends RecyclerView.ViewHolder {
        private final TextView tvRvcTitle;
        private final TextView tvRvcContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRvcTitle = itemView.findViewById(R.id.tvRvcTitle);
            tvRvcContent = itemView.findViewById(R.id.tvRvcContent);
        }

        void bind(T item, OnClickListener<T> listener, int position, String title, String content) {
            tvRvcTitle.setText(title);
            tvRvcContent.setText(content);
            itemView.setOnClickListener(v -> listener.onClick(item, position));
        }
    }

}
