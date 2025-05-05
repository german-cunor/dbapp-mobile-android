package code;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.app1.R;

import java.util.List;

import db.Product;
import db.ProductBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {

    private MainActivity context;
    private List<ProductBase> products;

    public ProductsAdapter(MainActivity context, List<ProductBase> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, viewGroup, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int i) {
        holder.itemText.setText(products.get(i).getName());
        holder.itemView.setOnClickListener(e -> {
            Intent intent = new Intent(context, ProductActivity.class);
            intent.putExtra("ProductName", products.get(i).getName());
            intent.putExtra("Type", "Edit");
            intent.putExtra("Id", products.get(i).getId());
            context.startActivity(intent);
        });
        holder.delete_item.setOnClickListener(e -> {
            deleteProductById(products.get(i).getId(), i);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        TextView itemText;
        ImageButton delete_item;

        public ProductsViewHolder(View view) {
            super(view);

            itemText = view.findViewById(R.id.item_text);
            delete_item = view.findViewById(R.id.delete_item);
        }
    }

    private void deleteProductById(int id, int position) {
        Call<Void> call = MainActivity.productApi.deleteProduct(id);

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    products.remove(position);
                    notifyDataSetChanged();
                    context.checkItems();
                } else {
                    Toast.makeText(context, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }
}
