package code;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.testapp.app1.R;

import db.Product;
import db.ProductApi;
import db.ProductBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView noItems;
    private List<ProductBase> products;
    private ProductsAdapter adapter;

    public static ProductApi productApi;
    private static final String BASE_URL = "http://192.168.229.62:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        products = new ArrayList<>();

        RecyclerView recycler = findViewById(R.id.recycler);
        adapter = new ProductsAdapter(this, products);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        FloatingActionButton floatingButton = findViewById(R.id.floatingButton);
        floatingButton.setOnClickListener(e -> {
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra("Type", "New");
            startActivity(intent);
        });

        noItems = findViewById(R.id.no_items);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        productApi = retrofit.create(ProductApi.class);

        // Fetch products
        fetchAllProducts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchAllProducts();
    }

    public void checkItems() {
        if (products.isEmpty()) {
            noItems.setVisibility(VISIBLE);
        } else {
            noItems.setVisibility(INVISIBLE);
        }
    }

    private void fetchAllProducts() {
        Call<List<ProductBase>> call = productApi.getAllProducts();

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<ProductBase>> call, Response<List<ProductBase>> response) {
                if (!response.isSuccessful()) {
                    Log.e("API", "Error code: " + response.code());
                    return;
                }

                products.clear();
                List<ProductBase> body = response.body();

                if (body != null) {
                    products.addAll(body);
                }

                adapter.notifyDataSetChanged();
                checkItems();
            }

            @Override
            public void onFailure(Call<List<ProductBase>> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }
}