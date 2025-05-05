package code;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.testapp.app1.R;

import db.Product;
import db.ProductBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {

    private String currentType;
    private int currentId;
    private TextView title;
    private EditText name;
    private EditText price;
    private EditText stock;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.title);
        String t = getIntent().getStringExtra("ProductName");

        if (t == null) {
            title.setText("New Product");
        } else {
            title.setText(t);
        }

        Button cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener( e -> finish());

        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        stock = findViewById(R.id.stock);
        description = findViewById(R.id.description);

        Button save = findViewById(R.id.save_button);
        save.setOnClickListener(e -> {
            if (name.getText().toString().isEmpty()) {
                Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (price.getText().toString().isEmpty()) {
                Toast.makeText(this, "Price is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (stock.getText().toString().isEmpty()) {
                Toast.makeText(this, "Stock is null", Toast.LENGTH_SHORT).show();
                return;
            }
            Product product = new Product();
            product.setName(name.getText().toString());
            product.setPrice(Double.parseDouble(price.getText().toString()));
            product.setStock(Integer.parseInt(stock.getText().toString()));
            product.setDescription(description.getText().toString());

            switch (currentType) {
                case "Edit":
                    product.setId(currentId);
                    updateProductById(currentId, product);
                    break;

                case "New":
                    createNewProduct(product);
                    break;
                default:
            }
//            MySQL.executeQuery("INSERT INTO Products(Name, Price, Stock, Description) VALUES ('" + name +"', " + price + ", " + stock + ", '" + description + "')");
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentType = getIntent().getStringExtra("Type");

        if (currentType.equals("Edit")) {
            currentId = getIntent().getIntExtra("Id", -1);
            fetchProductById(currentId);
        }
    }

    private void fetchProductById(int id) {
        Call<Product> call = MainActivity.productApi.getProductById(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!response.isSuccessful()) {
                    Log.e("API", "Error code: " + response.code());
                    return;
                }

                Product product = response.body();
                if (product != null) {
                    name.setText(product.getName());
                    price.setText("" + product.getPrice());
                    stock.setText("" + product.getStock());
                    description.setText(product.getDescription());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void updateProductById(int id, Product updatedProduct) {
        Call<Void> call = MainActivity.productApi.updateProduct(id, updatedProduct);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Product updated successfully", Toast.LENGTH_SHORT).show();
                    title.setText(updatedProduct.getName());
                } else {
                    Toast.makeText(getContext(), "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void createNewProduct(Product newProduct) {
        Call<ProductBase> call = MainActivity.productApi.createProduct(newProduct);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ProductBase> call, Response<ProductBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductBase createdProduct = response.body();
                    currentId = createdProduct.getId();
                    title.setText(newProduct.getName());
                    currentType = "Edit";

                    Toast.makeText(getContext(), "New product created!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductBase> call, Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }


    private ProductActivity getContext() {
        return this;
    }
}