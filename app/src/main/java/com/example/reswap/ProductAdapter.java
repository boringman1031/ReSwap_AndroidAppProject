package com.example.reswap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private List<Product> productList;
    private List<Product> fullList; // ➕ 完整商品資料，用來篩選
    private OnFavoriteRemovedListener listener;

    public static class Product {
        public String name;
        public String category;
        public String description;
        String imagePath;

        public Product(String name, String category, String description, String imagePath) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.imagePath = imagePath;
        }
    }

    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(String name);
    }

    public void setOnFavoriteRemovedListener(OnFavoriteRemovedListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = new ArrayList<>(productList);
        this.fullList = new ArrayList<>(productList); // ➕ 備份原始資料
    }

    // ➕ 新增分類篩選功能
    public void filterByCategory(String category) {
        if (category.equals("全部")) {
            productList = new ArrayList<>(fullList);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : fullList) {
                if (p.category.equals(category)) {
                    filtered.add(p);
                }
            }
            productList = filtered;
        }
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvDesc;
        ImageButton btnFavorite;
        ImageView ivProductImage;
        DatabaseHelper dbHelper;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvDesc = itemView.findViewById(R.id.tvProductDesc);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            dbHelper = new DatabaseHelper(itemView.getContext());
        }

        public void bind(Product p) {
            tvName.setText(p.name);
            tvCategory.setText(p.category);
            tvDesc.setText(p.description);

            // 收藏狀態
            boolean isFavorite = dbHelper.isFavorite(p.name);
            btnFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            btnFavorite.setOnClickListener(v -> {
                if (isFavorite) {
                    dbHelper.removeFavorite(p.name);
                    btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(itemView.getContext(), "已移除收藏", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onFavoriteRemoved(p.name);
                } else {
                    dbHelper.addFavorite(p.name);
                    btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                    Toast.makeText(itemView.getContext(), "已加入收藏", Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("name", p.name);
                intent.putExtra("category", p.category);
                intent.putExtra("desc", p.description);
                intent.putExtra("image", p.imagePath);
                context.startActivity(intent);
            });

            Glide.with(itemView.getContext())
                    .load(p.imagePath != null ? Uri.parse(p.imagePath) : null)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .centerCrop()
                    .into(ivProductImage);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeByName(String name) {
        Iterator<Product> iterator = productList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().name.equals(name)) {
                iterator.remove();
                notifyDataSetChanged();
                break;
            }
        }

        // 更新完整清單也要刪掉
        Iterator<Product> fullIterator = fullList.iterator();
        while (fullIterator.hasNext()) {
            if (fullIterator.next().name.equals(name)) {
                fullIterator.remove();
                break;
            }
        }
    }

    public void updateData(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }
}
