package com.rakoon.restaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.rakoon.restaurant.datasource.DataBaseHelper;
import com.rakoon.restaurant.model.Cart;
import com.rakoon.restaurant.view.AutoFitGridLayoutManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CartActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;
    @BindView(R.id.nodatafound)
    TextView nodatafound;
    @BindView(R.id.proceed)
    TextView proceed;
    @BindView(R.id.additems)
    TextView additems;
    @BindView(R.id.subtotal)
    TextView subtotal;
    @BindView(R.id.subtotalamount)
    TextView subtotalamount;
    @BindView(R.id.extraadd)
    TextView extraadd;
    @BindView(R.id.extraaddamount)
    TextView extraaddamount;
    @BindView(R.id.subtotallay)
    LinearLayout subtotallay;

    ArrayList<Cart> cartlist;

    AlertDialog alertDialog;
    AllPath apath;
    Unbinder unbinder;
    RecyclerViewAdapter adapter;
    String mess = "", yes = "", no = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        if (item.getItemId() == R.id.clearcart) {

            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setMessage(mess);
            builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataBaseHelper dbh = new DataBaseHelper(CartActivity.this);
                    boolean cleared = dbh.clearCart(apath.getUserid(CartActivity.this));
                    Log.e("Cleared", cleared + " clr");
                    cartlist = dbh.getCartlist(apath.getUserid(CartActivity.this));
                    adapter = new RecyclerViewAdapter(CartActivity.this);
                    recyclerView.setAdapter(adapter);
                    viewHandler();
                }
            });
            builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        if (apath.getLanguage(CartActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.cartitems_en));
            proceed.setText(getResources().getString(R.string.proceedtocheckout_en));
            nodatafound.setText(getResources().getString(R.string.cartempty_en));
            subtotal.setText(getResources().getString(R.string.subtotal_en));
            extraadd.setText(getResources().getString(R.string.extra_add_en));
            additems.setText(getResources().getString(R.string.additems_en));

            mess = getResources().getString(R.string.clearitemalrt_en);
            yes = getResources().getString(R.string.yes_en);
            no = getResources().getString(R.string.no_en);
        } else if (apath.getLanguage(CartActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            getSupportActionBar().setTitle(getResources().getString(R.string.cartitems_ar));
            proceed.setText(getResources().getString(R.string.proceedtocheckout_ar));
            nodatafound.setText(getResources().getString(R.string.cartempty_ar));
            subtotal.setText(getResources().getString(R.string.subtotal_ar));
            extraadd.setText(getResources().getString(R.string.extra_add_ar));
            additems.setText(getResources().getString(R.string.additems_ar));

            mess = getResources().getString(R.string.clearitemalrt_ar);
            yes = getResources().getString(R.string.yes_ar);
            no = getResources().getString(R.string.no_ar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cartlist = new ArrayList<>();
        DataBaseHelper dbh = new DataBaseHelper(CartActivity.this);
        cartlist = dbh.getCartlist(apath.getUserid(CartActivity.this));
        boolean deleted = dbh.deleteExtrastuf();
        int width = recyclerView.getWidth();   //  for always 1 column in grid
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(CartActivity.this, width);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(CartActivity.this);
        recyclerView.setAdapter(adapter);
        viewHandler();
        priceCalculator();
        proceed.setOnClickListener((v) -> {
            Intent in = new Intent(CartActivity.this, ExtraStuffActivity.class);
            startActivity(in);
            finish();
        });
        additems.setOnClickListener((v) -> {
            finish();
        });
    }

    void viewHandler() {
        if (cartlist.size() > 0) {
            nodatafound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            proceed.setVisibility(View.VISIBLE);
            subtotallay.setVisibility(View.VISIBLE);
        } else {
            nodatafound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            proceed.setVisibility(View.GONE);
            subtotallay.setVisibility(View.GONE);
        }
    }

    void priceCalculator() {
        double tprice = 0.0;
        for (int i = 0; i < cartlist.size(); i++) {
            Cart ct = cartlist.get(i);
            double itemprice = Double.parseDouble(ct.getQuantity()) * Double.parseDouble(ct.getPrice());
            tprice = tprice + itemprice;

        }
        DecimalFormat df2 = new DecimalFormat("#.##");
        subtotalamount.setText(df2.format(tprice) + " JD");

        double extraprice = 0.0;
        for (int i = 0; i < cartlist.size(); i++) {
            Cart ct = cartlist.get(i);
            double reqp=0.0;
            if (ct.getRequired_price() != null && !ct.getRequired_price().equalsIgnoreCase("") && !ct.getRequired_price().equalsIgnoreCase("null"))
                reqp   = Double.parseDouble(ct.getRequired_price());
            double temptotal = 0.0;
            String str = ct.getOptional_price();
            if (str != null && !str.equalsIgnoreCase("") && !str.equalsIgnoreCase("null")) {
                List<String> slist = Arrays.asList(str.split(","));
                for (int j = 0; j < slist.size(); j++) {
                    temptotal = temptotal + Double.parseDouble(slist.get(j));
                }
            }
            extraprice = extraprice + ((temptotal + reqp) * Double.parseDouble(ct.getQuantity()));
            // double itemprice = Double.parseDouble(ct.getQuantity()) * Double.parseDouble(ct.getPrice());
            //tprice = tprice + itemprice;

        }
        extraaddamount.setText(df2.format(extraprice) + " JD");

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        Context mContext;

        public RecyclerViewAdapter(Context context) {

            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name, price, quantity, required, totalprice, plus, minus, count;
            public ImageView image, list, delete;
            Cart item;

            public ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.name);
                price = v.findViewById(R.id.price);
                quantity = v.findViewById(R.id.quantity);
                required = v.findViewById(R.id.required);
                totalprice = v.findViewById(R.id.totalprice);
                image = v.findViewById(R.id.image);
                list = v.findViewById(R.id.list);
                delete = v.findViewById(R.id.delete);
                plus = v.findViewById(R.id.plus);
                count = v.findViewById(R.id.count);
                minus = v.findViewById(R.id.minus);

            }

            public void setData(Cart item) {
                this.item = item;
                name.setText(item.getName());
                if (apath.getLanguage(CartActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    price.setText(getResources().getString(R.string.itemprice_en) + ": " + item.getPrice() + " JD");
                    quantity.setText(getResources().getString(R.string.quantity_en));
                    totalprice.setText(getResources().getString(R.string.totalprice_en) + "\n " + priceCalculator(Double.parseDouble(item.getPrice()), Integer.parseInt(item.getQuantity())));
                } else if (apath.getLanguage(CartActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    price.setText(getResources().getString(R.string.itemprice_ar) + ": " + item.getPrice() + " JD");
                    quantity.setText(getResources().getString(R.string.quantity_ar));
                    totalprice.setText(getResources().getString(R.string.totalprice_ar) + "\n " + priceCalculator(Double.parseDouble(item.getPrice()), Integer.parseInt(item.getQuantity())));
                }


                count.setText(item.getQuantity());
                if (item.getOptional_name().length() > 0 && !item.getRequired_name().equalsIgnoreCase("null") )
                    required.setText(item.getRequired_name() + "," + item.getOptional_name());
                else if (!item.getRequired_name().equalsIgnoreCase("null"))
                    required.setText(item.getRequired_name());
                else
                    required.setVisibility(View.GONE);

                ImageRequest request = new ImageRequest(item.getPhoto(),
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                image.setImageBitmap(bitmap);
                            }
                        }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                image.setImageResource(R.drawable.ic_launcher_foreground);
                            }
                        });
                Singleton.getIntance(CartActivity.this).addToRequestQueue(request);

                //relativeLayout.setBackgroundColor(Color.parseColor(item.color));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.cart_adap, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder Vholder, int position) {
            Cart c = cartlist.get(position);
            Vholder.setData(c);

            Vholder.delete.setOnClickListener((v) -> {
                DataBaseHelper dbh = new DataBaseHelper(CartActivity.this);
                boolean deleted = dbh.deleteCartItem(apath.getUserid(CartActivity.this), c.getAuoto_id());
                Log.e("deleted " + deleted, apath.getUserid(CartActivity.this) + " " + c.getAuoto_id());
                if (deleted) {
                    cartlist.remove(position);
                    notifyDataSetChanged();
                    viewHandler();
                    priceCalculator();
                }
            });
            Vholder.list.setOnClickListener((v) -> {
                finish();
            });

            Vholder.plus.setOnClickListener((v) -> {
                int qu = Integer.parseInt(c.getQuantity().toString());
                if (qu < 99) {
                    qu += 1;
                    c.setQuantity(qu + "");
                    DataBaseHelper dbh = new DataBaseHelper(CartActivity.this);
                    boolean updated = dbh.updateCartItem(c);
                    if (updated) {
                        cartlist.set(position, c);
                        notifyDataSetChanged();
                        priceCalculator();
                    }
                    //   setTotalprice();
                } else {

                }
            });
            Vholder.minus.setOnClickListener((v) -> {
                int qu = Integer.parseInt(c.getQuantity().toString());
                if (qu > 1) {
                    qu -= 1;
                    c.setQuantity(qu + "");
                    DataBaseHelper dbh = new DataBaseHelper(CartActivity.this);
                    boolean updated = dbh.updateCartItem(c);
                    if (updated) {
                        cartlist.set(position, c);
                        notifyDataSetChanged();
                        priceCalculator();
                    }
                    // setTotalprice();
                } else {

                }
            });

        }

        @Override
        public int getItemCount() {

            return cartlist.size();
        }
    }

    String priceCalculator(double price, int quantity) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        double pr = price * quantity;

        return df2.format(pr) + " JD";
    }
}
