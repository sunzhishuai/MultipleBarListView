package com.reliance.multiplebarlistview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private MultipleBarListView mMulipleListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMulipleListview = (MultipleBarListView) findViewById(R.id.mlv_content);

        mMulipleListview.setAdapter(new ListviewAdapter());
        mMulipleListview.setOnPositionChangeListener(new MultipleBarListView.OnPositionChangeListener() {
            @Override
            public void onPositionChange(AbsListView parent, int position, View barLayout) {
                TextView barLayout1 = (TextView) barLayout;
                barLayout1 .setText(""+position);

            }
        });
    }
    public class ListviewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 150;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LViewHolder holder = null;
            if(convertView==null){
                holder = new LViewHolder();
                convertView  = View.inflate(getApplicationContext(),R.layout.item_layout,null);
                holder.tvContent  = (TextView) convertView.findViewById(R.id.tv_item_content);
                convertView.setTag(holder);
            }

            holder = (LViewHolder) convertView.getTag();
            holder.tvContent.setText("这是"+position);
            return convertView;
        }


        private class LViewHolder{
            public TextView tvContent;
        }
    }
}
