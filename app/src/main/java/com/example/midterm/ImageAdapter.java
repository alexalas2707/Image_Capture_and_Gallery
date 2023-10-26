package com.example.midterm;

//importing necessary libraries
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;

    // List of images in byte array form to display
    private List<byte[]> images;

    // Interface for long click listener on RecyclerView items
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    // Instance of the long click listener
    private OnItemLongClickListener listener;


    // Setter for the long click listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }


    // Constructor for the ImageAdapter
    public ImageAdapter(Context context, List<byte[]> images) {
        this.context = context;
        this.images = images;
    }

    //This method inflates the custom layout for each item and returns an ImageViewHolder
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view,listener);
    }

    // This method binds the data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        byte[] image = images.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.imageItem.setImageBitmap(bitmap);
    }

    // Returns the number of items to display
    @Override
    public int getItemCount() {
        return images.size();
    }

    // ViewHolder class to hold the views for each item
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;

        public ImageViewHolder(@NonNull View itemView, OnItemLongClickListener listener) {
            super(itemView);

            // Initialize the ImageView for each item
            imageItem = itemView.findViewById(R.id.imageItem);

            // Setting a long click listener for each item
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(position);
                    return true;
                }
                return false;
            });
        }
    }
}
