package com.patel.memorybookproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.patel.memorybookproject.common.Util;
import com.patel.memorybookproject.model.AddMemoriesModel;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecycleAdapter adapter;
    ArrayList<AddMemoriesModel> toAddMemoriesList;
    RecyclerView rv;
    private Query sortByDateQuery;
    private DatabaseReference dbRef;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth auth;
    static String pass;
    static Integer size = 18;

    long dateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(MainActivity.this, AddMemoriesActivity.class);
                MainActivity.this.startActivity(newIntent);
            }
        });


        toAddMemoriesList = new ArrayList<>();

        //sorting on basis of memory title
        auth = FirebaseAuth.getInstance();
        mCurrentUser = auth.getCurrentUser();
        dbRef= FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid()).child("addMemories").getRef();
        dbRef.keepSynced(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.myrecycleView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        adapter = new RecycleAdapter();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
            pass = customFontFileNameInAssets;
            Log.e(pass, pass);
        } catch (Exception e) {
            Log.e("Not Implemented", "Font can't be changed");
        }
    }


    public static void overrideFontSize(Context context, String defaultFontSizeToOverride, String customFontSizeToSet) {
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String textSize = settings.getString("customSize", customFontSizeToSet);
            size = Integer.parseInt(textSize);
        } catch (Exception e) {
            Log.e("Not Implemented", "Font size can't be changed");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(Util.getPathForEntries(Util.getAuthUser().getUid())).orderByChild("date").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        toAddMemoriesList.clear();

                        Log.w("AddMemoriesApp", "getUser:onCancelled " + dataSnapshot.toString());
                        Log.w("AddMemoriesApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            AddMemoriesModel toAddMemories = data.getValue(AddMemoriesModel.class);
                            toAddMemoriesList.add(toAddMemories);
                        }
                        Collections.reverse(toAddMemoriesList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("AddMemoriesApp", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private class RecycleAdapter extends RecyclerView.Adapter {


        @Override
        public int getItemCount() {
            return toAddMemoriesList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addmemories_item, parent, false);
            SimpleItemViewHolder pvh = new SimpleItemViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) holder;
            AddMemoriesModel toAddMemories = toAddMemoriesList.get(position);

            //get date which is in long and convert into string date format and store it as a string
            SimpleDateFormat dateFormat=new SimpleDateFormat("MMMM dd, YYYY");
            dateValue = (toAddMemories.getDate());
            Date date = new Date(dateValue);

            System.out.println("Date Created: " + date);

            ((SimpleItemViewHolder) holder).title.setText(toAddMemories.getName());
            ((SimpleItemViewHolder) holder).description.setText(toAddMemories.getMessage());
            ((SimpleItemViewHolder)holder).dateCreated.setText(String.valueOf(dateFormat.format(date)));


            if (!TextUtils.isEmpty(toAddMemories.getImageUrl())) {
                Glide.with(MainActivity.this)
                        .load(toAddMemories.getImageUrl())
                        .placeholder(R.drawable.androidlogo)//place your placeholder here
                        .into(viewHolder.ivMemoryImage);
            }
        }

        public final class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            ImageView ivMemoryImage;
            TextView description;
            TextView dateCreated;

//            Typeface tf1 = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSansCondensed-Bold.ttf");
//            Typeface tf2 = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/JosefinSans-Regular.ttf");

            public SimpleItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                title = (TextView) itemView.findViewById(R.id.myTextView);
                description = (TextView) itemView.findViewById(R.id.myTextViewDescrip);
                dateCreated = (TextView) itemView.findViewById(R.id.memoryDateDisplay);
                ivMemoryImage = (ImageView) itemView.findViewById(R.id.ivMemoryImage);

//                title.setTypeface(tf1);
//                description.setTypeface(tf2);

            }

            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(MainActivity.this, AddMemoriesActivity.class);
                newIntent.putExtra("toAddMemories", toAddMemoriesList.get(getAdapterPosition()));
                MainActivity.this.startActivity(newIntent);

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu, menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "You have selected settings", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
       else if (id == R.id.action_logOut) {
            Toast.makeText(MainActivity.this, "You have selected Sign out", Toast.LENGTH_SHORT).show();
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}

