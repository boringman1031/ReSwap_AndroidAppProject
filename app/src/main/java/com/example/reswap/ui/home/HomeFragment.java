package com.example.reswap.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.reswap.AddProductActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reswap.CategoryAdapter;
import com.example.reswap.DatabaseHelper;
import com.example.reswap.ExchangeRecordActivity;
import com.example.reswap.FavoriteActivity;
import com.example.reswap.ProductAdapter;
import com.example.reswap.R;
import com.example.reswap.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.viewpager2.widget.ViewPager2;
import com.example.reswap.ImageAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler = new Handler();
    private Runnable autoScrollRunnable;
    private int currentPage = 0;

    private List<ProductAdapter.Product> allProducts = new ArrayList<>();
    private List<CategoryAdapter.CategorySection> currentSections = new ArrayList<>();
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ✅ Banner 輪播設定
        int[] imageIds = {
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        };
        ViewPager2 viewPager = binding.viewPager;
        WormDotsIndicator dotsIndicator = binding.dotsIndicator;

        ImageAdapter imageAdapter = new ImageAdapter(imageIds);
        viewPager.setAdapter(imageAdapter);
        dotsIndicator.setViewPager2(viewPager);

        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getAdapter() == null) return;
                int total = viewPager.getAdapter().getItemCount();
                currentPage = (currentPage + 1) % total;
                viewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(autoScrollRunnable, 3000);

        // ➕ 我要上架按鈕
        binding.btnGoAdd.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductActivity.class));
        });

        // ❤️ 我的收藏按鈕
        binding.btnGoFavorite.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FavoriteActivity.class));
        });

        // 📜 我的交換紀錄按鈕
        binding.btnGoRecords.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ExchangeRecordActivity.class));
        });

        // 🔍 商品搜尋設定
        SearchView searchView = binding.searchView;

        // 🧾 商品列表初始化
        RecyclerView categoryRecyclerView = binding.recyclerViewCategory;
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(currentSections);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // 資料來源
        DatabaseHelper db = new DatabaseHelper(getContext());
        Cursor cursor = db.getAllProducts();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("Category"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("ImagePath"));

                allProducts.add(new ProductAdapter.Product(name, category, desc, imagePath));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // 初始呈現所有分類
        updateCategorySection(allProducts);

        // 搜尋文字變動時過濾
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        return root;
    }

    private void updateCategorySection(List<ProductAdapter.Product> filteredList) {
        Map<String, List<ProductAdapter.Product>> categoryMap = new LinkedHashMap<>();

        for (ProductAdapter.Product product : filteredList) {
            categoryMap.computeIfAbsent(product.category, k -> new ArrayList<>()).add(product);
        }

        currentSections.clear();
        for (Map.Entry<String, List<ProductAdapter.Product>> entry : categoryMap.entrySet()) {
            currentSections.add(new CategoryAdapter.CategorySection(entry.getKey(), entry.getValue()));
        }

        categoryAdapter.notifyDataSetChanged();
    }

    private void filterProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            updateCategorySection(allProducts);
            return;
        }

        List<ProductAdapter.Product> filtered = new ArrayList<>();
        for (ProductAdapter.Product p : allProducts) {
            if (p.name.toLowerCase().contains(query.toLowerCase()) ||
                    p.category.toLowerCase().contains(query.toLowerCase()) ||
                    p.description.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        updateCategorySection(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoScrollRunnable);
        binding = null;
    }
}