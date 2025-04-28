package com.example.welcomscreen;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class testtt extends AppCompatActivity {

    private List<DataModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize dataList
        dataList = new ArrayList<>();

        // Add sample data
        dataList.add(new DataModel("Item 1", "Description 1"));
        dataList.add(new DataModel("Item 2", "Description 2"));
        dataList.add(new DataModel("Item 3", "Description 3"));
        // Add more data as needed

        // Add TextViews dynamically
        addTextViews();
    }

    // DataModel class
    public static class DataModel {
        private String item1;
        private String item2;

        // Constructor
        public DataModel(String item1, String item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        // Getters and Setters
        public String getItem1() {
            return item1;
        }

        public void setItem1(String item1) {
            this.item1 = item1;
        }

        public String getItem2() {
            return item2;
        }

        public void setItem2(String item2) {
            this.item2 = item2;
        }
    }

    // Method to add TextViews dynamically
    private void addTextViews() {
        // Get the LinearLayout container
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        // Loop through the data and create TextViews dynamically
        for (DataModel data : dataList) {
            TextView textView1 = new TextView(this);
            textView1.setText(data.getItem1());
            linearLayout.addView(textView1);

            TextView textView2 = new TextView(this);
            textView2.setText(data.getItem2());
            linearLayout.addView(textView2);
        }
    }
}