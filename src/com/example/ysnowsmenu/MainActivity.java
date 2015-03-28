package com.example.ysnowsmenu;

import java.util.ArrayList;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ArrayList<String> mDatas;
	private ListView mListView;
	private StarMenu mArcMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initData();
		initView();
		mListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mDatas));
		
	}
	
	
	
	
	private void initData()
	{
		mDatas = new ArrayList<String>();

		for (int i = 'A'; i < 'Z'; i++)
		{
			mDatas.add((char) i + "");
		}

	}

	private void initView()
	{
		mListView = (ListView) findViewById(R.id.id_listview);
		mArcMenu = (StarMenu) findViewById(R.id.id_menu);
	}
}
