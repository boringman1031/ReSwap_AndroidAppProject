package com.example.reswap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public static class CategorySection {
        String categoryName;
        List<ProductAdapter.Product> productList;

        public CategorySection(String categoryName, List<ProductAdapter.Product> productList) {
            this.categoryName = categoryName;
            this.productList = productList;
        }
    }

    private List<CategorySection> categorySections;

    public CategoryAdapter(List<CategorySection> categorySections) {
        this.categorySections = categorySections;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        RecyclerView recyclerViewInner;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryTitle);
            recyclerViewInner = itemView.findViewById(R.id.recyclerViewInner);
        }

        public void bind(CategorySection section) {
            tvCategoryName.setText(section.categoryName);
            ProductAdapter adapter = new ProductAdapter(section.productList);
            recyclerViewInner.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewInner.setAdapter(adapter);
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_section, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bind(categorySections.get(position));
    }

    @Override
    public int getItemCount() {
        return categorySections.size();
    }
}
