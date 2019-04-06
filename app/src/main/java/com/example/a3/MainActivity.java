package com.example.a3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.camera2.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    SQLiteDatabase db;
    Button save;
    Button load;
    Button capture;
    EditText Tsearch;
    EditText Ssearch;
    ImageView iv;
    String t;
    String s;
    Bitmap bm;
    ContentValues cv;
    ContentValues cv2;
    ContentValues cv3;
    TextView tv;
    ByteArrayOutputStream stream;
    byte[] ba;
    String[] items;
    Bitmap bit;
    byte[] results;
    ArrayList<Bitmap> bt;
    int counter=0;
    int picOrder=0;
    ArrayList<String> temps;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tsearch= (EditText) findViewById(R.id.Tag);
        Ssearch= (EditText) findViewById(R.id.Size);
        save=(Button)findViewById(R.id.sb);
        load=(Button)findViewById(R.id.lb);
        capture=(Button)findViewById(R.id.cb);
        iv=(ImageView)findViewById(R.id.imageview);
        db = openOrCreateDatabase("MyDatabse", Context.MODE_PRIVATE, null);
        tv=(TextView)findViewById(R.id.textview);

        db.execSQL("DROP TABLE IF EXISTS Photos");
        db.execSQL("CREATE TABLE Photos (Tag TEXT,Photo Blob,Size int, input_order int)");
    }

    public void camera(View v)
    {
        Log.v("Capture", "Button got clicked");
        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x, 1);

    }

    @Override
    protected void onActivityResult(int rc, int resc, Intent data)
    {
        //
        bm = (Bitmap) data.getExtras().get("data");
        long s;
        s=bm.getByteCount();
        Ssearch.setText(""+s);
        Log.v("My Tag",""+bm.getByteCount());
        iv.setBackgroundResource(0);
        iv.setImageBitmap(bm);
    }

    public void onSave(View v)
    {
        t=Tsearch.getText().toString();
        stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,stream);
        ba = stream.toByteArray();

        String orderPrint= ""+picOrder+"";
        items=t.split(";");
        for (int i=0;i<items.length;i++)
        {

            String temp="INSERT INTO Photos (Tag,Photo,Size,input_order) VALUES(?,?,?,?)";
            SQLiteStatement insertS= db.compileStatement(temp);
            insertS.clearBindings();
            insertS.bindString(1,items[i]);
            insertS.bindBlob(2,ba);
            insertS.bindString(3,Ssearch.getText().toString());
            insertS.bindString(4,orderPrint);
            insertS.executeInsert();
            Log.d("Tag T", temp+"."+items[i]);
        }
        picOrder++;

    }
    public void onLoad(View v) {
        s = Ssearch.getText().toString();
        t = Tsearch.getText().toString();

        if (!s.isEmpty() && t.isEmpty()) {
            String powe = "SELECT Photos.Tag,Photos.Size from Photos where Size=" + s;
            Log.d("Tag T", powe + ".");
            Cursor c = db.rawQuery("SELECT * from Photos where Size=" + s, null);

            int flag = 0;
            int i=0;
            c.moveToFirst();
            String temp = "";
            String tempView="";
            temps=new ArrayList<String>();
            bt= new ArrayList<Bitmap>();
            while (flag == 0) {

                temp =c.getString(0) + "  " + c.getString(2)+c.getString(3);
                tempView =tempView+c.getString(0) + "  " + c.getString(2)+c.getString(3)+"\n"+" ";
                Log.v("MyTag", temp+"\n");
                temps.add(tempView);
                //tv.setText(tempView);
                results=c.getBlob(1);
                bit=BitmapFactory.decodeByteArray(results,0,results.length);
                bt.add(bit);
                i++;
                iv.setImageBitmap(bt.get(0));

                if (c.isLast()) {
                    for(int y=0;y<temps.size();y++)
                    {
                        tv.setText(temps.get(y));
                    }
                    flag = 1;
                } else {
                    c.moveToNext();
                }
            }
        }
        if(!s.isEmpty() && !t.isEmpty())
        {
            String tempQuery = "SELECT * from Photos where tag='" + t + "' and size='" + s + "'";
            Log.e("Tag Query", tempQuery);
            Cursor c3 = db.rawQuery(tempQuery, null);
            int flag = 0;
            c3.moveToFirst();
            String temp = "";
            String tempView="";
            temps=new ArrayList<String>();
            bt= new ArrayList<Bitmap>();
            while (flag == 0)
            {

                temp = c3.getString(0) + "  " + c3.getString(2)+c3.getString(3);
                Log.v("MyTag", temp+"\n");
               // tv.setText(temp);
                tempView =tempView+c3.getString(0) + "  " + c3.getString(2)+c3.getString(3)+"\n"+" ";
                temps.add(tempView);
                results=c3.getBlob(1);
                bit=BitmapFactory.decodeByteArray(results,0,results.length);
                bt.add(bit);
                iv.setImageBitmap(bt.get(0));

                temp = temp + "\n";
                if (c3.isLast()) {
                    for(int y=0;y<temps.size();y++)
                    {
                        tv.setText(temps.get(y));
                    }
                    flag = 1;
                } else {
                    c3.moveToNext();
                }
            }

        }
        if (s.isEmpty() && !t.isEmpty()) {
            String tempQuery = "SELECT * from Photos where Tag=" + t;
            Log.e("Tag Query", tempQuery);
            Cursor c2 = db.rawQuery("SELECT * from Photos where Tag=?", new String[]{Tsearch.getText().toString()});

            int flag = 0;
            c2.moveToFirst();
            String temp = "";
            String tempView="";
            temps=new ArrayList<String>();
            bt= new ArrayList<Bitmap>();
            while (flag == 0)
            {

                temp = temp + c2.getString(0) + "  " + c2.getString(2)+c2.getString(3);
                Log.v("MyTag", temp+"\n");
              //  tv.setText(temp +"\n");
                tempView =tempView+c2.getString(0) + "  " + c2.getString(2)+c2.getString(3)+"\n"+" ";

                temps.add(tempView);
                results=c2.getBlob(1);
                bit=BitmapFactory.decodeByteArray(results,0,results.length);
                bt.add(bit);
                iv.setImageBitmap(bt.get(0));

                temp = temp + "\n";
                if (c2.isLast()) {
                    for(int y=0;y<temps.size();y++)
                    {
                        tv.setText(temps.get(y));
                    }
                    flag = 1;
                } else {
                    c2.moveToNext();
                }
            }
        }
        if (s.isEmpty() && t.isEmpty())
        {

            return;
        }

    }
    public void onNext(View view)
    {
        if (counter>=0 && counter<bt.size())
        {
            counter++;
            iv.setImageBitmap(bt.get(counter));
        }
        else{
            return;
        }
    }
    public void onPrevious(View view)
    {
        if (counter>0 && counter<bt.size())
        {
            counter--;
            iv.setImageBitmap(bt.get(counter));
        }
        else{
            return;
        }
    }
}



