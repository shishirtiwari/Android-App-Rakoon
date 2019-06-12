package com.rakoon.restaurant.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.rakoon.restaurant.model.Area;
import com.rakoon.restaurant.model.Branch;
import com.rakoon.restaurant.model.Cart;
import com.rakoon.restaurant.model.ExtraStuff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";
    private final Context myContext;
    private static final String DATABASE_NAME = "pos.db";
    private static final int DATABASE_VERSION = 1;
    private String pathToSaveDBFile;
    SQLiteDatabase myDataBase;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        pathToSaveDBFile = new StringBuffer(context.getFilesDir().getAbsolutePath()).append("/").append(DATABASE_NAME).toString();
        Log.e("PATH", pathToSaveDBFile);
        try {
            prepareDatabase();
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (IOException e) {

        }
    }

    public void prepareDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            Log.d(TAG, "Database exists.");
//                  int currentDBVersion = getVersionId();?
//                  if (DATABASE_VERSION > currentDBVersion) {
            Log.d(TAG, "Database version is higher than old.");
           /* deleteDb();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
//    				  }
            }*/
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open("sqlite/" + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }

    public void deleteDb() {
        File file = new File(pathToSaveDBFile);
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "Database deleted.");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean insertArea(String id, String name, String delivery, String branch_id, String userid) {
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Log.e(userid + " uid ", id + " id");
            myDataBase.execSQL("delete from area_table");
            myDataBase.execSQL("delete from branch");
            // myDataBase.rawQuery("delete from area_table where userid='" + userid + "'", null);
            Cursor cr = myDataBase.rawQuery("insert into area_table values ('" + id + "','" + name + "','" + delivery + "','" + branch_id + "','" + userid + "')", null);
            // Cursor res1 = myDataBase.rawQuery("select * from ReportMasterColumn_Hierarchy where ReportId = '" + reportid + "'", null);
            Log.e("curser size", cr.getCount() + "");
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Area getArea(String userid) {
        Area area = null;
        try {
            Log.e("user id", userid + "");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cr;
//            if (!userid.equalsIgnoreCase(""))
//                cr = myDataBase.rawQuery("select * from area_table where userid='" + userid + "'", null);
//            else
                cr = myDataBase.rawQuery("select * from area_table", null);

            // Cursor res1 = myDataBase.rawQuery("select * from ReportMasterColumn_Hierarchy where ReportId = '" + reportid + "'", null);
            Log.e("curser size", cr.getCount() + "");
            if (cr.getCount() > 0) {
                cr.moveToFirst();
                area = new Area(cr.getString(cr.getColumnIndex("id")), cr.getString(cr.getColumnIndex("name")),
                        cr.getString(cr.getColumnIndex("delivery")), cr.getString(cr.getColumnIndex("branch_id")));
            }
            myDataBase.close();
            return area;
        } catch (Exception e) {
            Log.e("Exception catch ar", e.toString());
            return area;
        }
    }

    public boolean insertBranch(Branch br, String userid) {
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Log.e(userid + " uid ", br.getId() + " id");
            // myDataBase.rawQuery("delete from branch where userid_='" + userid + "'", null);
            myDataBase.execSQL("delete from branch");
            myDataBase.execSQL("delete from area_table");
            Cursor cr = myDataBase.rawQuery("insert into branch values ('" + br.getId() + "','" + br.getName() + "','" + br.getOpen() + "','" + br.getClose() + "','" + br.getLogo() + "','" + br.getAddress() + "','" + TextUtils.join(",", br.getArr()) + "','" + br.getPhone() + "','" + userid + "')", null);
            // Cursor res1 = myDataBase.rawQuery("select * from ReportMasterColumn_Hierarchy where ReportId = '" + reportid + "'", null);
            Log.e("curser size", cr.getCount() + "");
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Branch getBranch(String userid) {
        Branch branch = null;
        try {
            Log.e("user id", userid + "");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cr0 = myDataBase.rawQuery("select *from branch", null);
            Log.e("curser 0 size", cr0.getCount() + "");
            while (cr0.moveToNext()) {
                Log.e("ID", cr0.getString(cr0.getColumnIndex("id_")));
            }
            Cursor cr;
//            if (!userid.equalsIgnoreCase(""))
//                cr = myDataBase.rawQuery("select *from branch where userid_='" + userid + "'", null);
//            else
                cr = myDataBase.rawQuery("select *from branch", null);
            Log.e("curser size", cr.getCount() + "");
            if (cr.getCount() > 0) {
                cr.moveToFirst();
                // Cursor res1 = myDataBase.rawQuery("select * from ReportMasterColumn_Hierarchy where ReportId = '" + reportid + "'", null);
                branch = new Branch(cr.getString(cr.getColumnIndex("id_")), cr.getString(cr.getColumnIndex("name_")),
                        cr.getString(cr.getColumnIndex("open_")), cr.getString(cr.getColumnIndex("close_")),
                        cr.getString(cr.getColumnIndex("logo_")), cr.getString(cr.getColumnIndex("address_")),
                        cr.getString(cr.getColumnIndex("payment_type_")).split(","), cr.getString(cr.getColumnIndex("phone_")),"","");
            }
            myDataBase.close();
            return branch;
        } catch (Exception e) {
            Log.e("Exception catch br", e.toString());
            return branch;
        }
    }

    public boolean insertItem(Cart cart) {
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cr = myDataBase.rawQuery("insert into cart (id, cat_id, cat_name, name, price, tax, photo, modification, description, userid, quantity,\n" +
                    "            required_id, required_name, required_price, optional_id, optional_name, optional_price) values ('" + cart.getId() + "','" + cart.getCat_id() + "','" + cart.getCat_name() + "','" + cart.getName() + "','" + cart.getPrice() + "','" + cart.getTax() + "','" + cart.getPhoto() + "','" + cart.getModification() + "','" + cart.getDescription() + "','" + cart.getUserid() + "','" + cart.getQuantity()
                    + "','" + cart.getRequired_id() + "','" + cart.getRequired_name() + "','" + cart.getRequired_price() + "','" + cart.getOptional_id() + "','" + cart.getOptional_name() + "','" + cart.getOptional_price() + "')", null);
            Log.e("curser size", cr.getCount() + " cat_id"+cart.getCat_id()+" iiii");
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Cart> getCartlist(String userid) {
        ArrayList<Cart> cartlist = new ArrayList<>();
        try {
            Log.e("user id", userid + "");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cr = myDataBase.rawQuery("select * from cart where userid='" + userid + "'", null);
            while (cr.moveToNext()) {
                cartlist.add(new Cart(cr.getInt(cr.getColumnIndex("auto_id")), cr.getString(cr.getColumnIndex("id")), cr.getString(cr.getColumnIndex("cat_id"))
                        , cr.getString(cr.getColumnIndex("cat_name")), cr.getString(cr.getColumnIndex("name"))
                        , cr.getString(cr.getColumnIndex("price")), cr.getString(cr.getColumnIndex("tax"))
                        , cr.getString(cr.getColumnIndex("photo")), cr.getString(cr.getColumnIndex("modification"))
                        , cr.getString(cr.getColumnIndex("description")), cr.getString(cr.getColumnIndex("userid"))
                        , cr.getString(cr.getColumnIndex("quantity")), cr.getString(cr.getColumnIndex("required_id"))
                        , cr.getString(cr.getColumnIndex("required_name")), cr.getString(cr.getColumnIndex("required_price"))
                        , cr.getString(cr.getColumnIndex("optional_id")), cr.getString(cr.getColumnIndex("optional_name"))
                        , cr.getString(cr.getColumnIndex("optional_price"))));
            }
            Log.e("Cart list size id", userid + "");
            myDataBase.close();
            return cartlist;
        } catch (Exception e) {
            Log.e("Exception catch ar", e.toString());
            return cartlist;
        }
    }

    public String getcategoryid(String userid) {
        String cate_id = "0";
        try {
            Log.e("user id", userid + "");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cr = myDataBase.rawQuery("select * from cart where userid='" + userid + "'", null);
            cr.moveToFirst();
            cate_id=cr.getString(cr.getColumnIndex("cat_id"));
            Log.e("cate id ", cate_id + "  blank"+cr.getString(2));
            Log.e("cursor size ", cr.getCount() + "  cursor size");
            myDataBase.close();
            return cate_id;
        } catch (Exception e) {
            Log.e("Exception catch ar", e.toString());
            return cate_id;
        }
    }

    public boolean clearCart(String userid) {
        try {
            Log.e("user id", userid + "");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            myDataBase.execSQL("delete from cart where userid='" + userid + "'");//rawQuery("delete from cart where userid='" + userid + "'", null);
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clearDatabase() {
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            myDataBase.execSQL("delete from area_table");
            myDataBase.execSQL("delete from branch");
            myDataBase.execSQL("delete from extra_stuff");
            myDataBase.execSQL("delete from cart");//rawQuery("delete from cart where userid='" + userid + "'", null);
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateCartItem(Cart c) {
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            ContentValues cv = new ContentValues();
            cv.put("quantity", c.getQuantity()); //These Fields should be your String values of actual column names
            myDataBase.update("cart", cv, "auto_id=" + c.getAuoto_id(), null);
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean deleteCartItem(String userid, int auto_id) {
        try {
            Log.e("user id", userid + "");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            myDataBase.execSQL("delete from cart where userid='" + userid + "' and auto_id=" + auto_id);//rawQuery("delete from cart where userid='" + userid + "'", null);
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insertExtrastuf(ArrayList<ExtraStuff> cartarray) {
        try {
            Log.e("Extra arr size", cartarray.size() + "  size ");
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            for (int i = 0; i < cartarray.size(); i++) {
                ExtraStuff cart = cartarray.get(i);
                Cursor cr = myDataBase.rawQuery("insert into extra_stuff (id, name, price, photo, modification, quantity, status) values ('" + cart.getId() + "','" + cart.getName() + "','" + cart.getPrice() + "','" + cart.getPhoto() + "','" + cart.getModification() + "','" + cart.getQuantity() + "','" + cart.isStatus() + "')", null);
                Log.e("curser size", cr.getCount() + "");
            }
            Cursor cr = myDataBase.rawQuery("select * from extra_stuff", null);
            Log.e("cr size", cr.getCount() + " size");
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteExtrastuf() {
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            myDataBase.execSQL("delete from extra_stuff");//rawQuery("delete from cart where userid='" + userid + "'", null);
            Cursor cr = myDataBase.rawQuery("select * from extra_stuff", null);
            Log.e("cr size", cr.getCount() + " size");
            myDataBase.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<ExtraStuff> getextraStuff() {
        ArrayList<ExtraStuff> list = new ArrayList<>();
        try {
            myDataBase = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cr = myDataBase.rawQuery("select * from extra_stuff", null);
            while (cr.moveToNext()) {
                list.add(new ExtraStuff( cr.getString(cr.getColumnIndex("id")), cr.getString(cr.getColumnIndex("name"))
                        , cr.getString(cr.getColumnIndex("price")), cr.getString(cr.getColumnIndex("photo"))
                        , cr.getString(cr.getColumnIndex("modification")), cr.getString(cr.getColumnIndex("quantity"))
                        , true));
            }
            Log.e("extra list size id", list.size() + "");
            myDataBase.close();
            return list;
        } catch (Exception e) {
            Log.e("Exception catch ar", e.toString());
            return list;
        }
    }


    public boolean exists(String table) {
        try {
        } catch (Exception e) {

        }
        try {
            myDataBase.rawQuery("SELECT * FROM " + table, null);
            //myDataBase.close();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }
}