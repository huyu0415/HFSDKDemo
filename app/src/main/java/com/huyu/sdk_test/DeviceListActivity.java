package com.huyu.sdk_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.huyu.lanconfig_hf.control.DeviceDiscover;
import com.huyu.lanconfig_hf.model.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * ___                      _
 * / __|   ___   ___   ___   | |  ___
 * | (_--- / _ \ / _ \ / _ \  | | /___)
 * \____| \___/ \___/ \___/| |_| \___
 * /
 * \___/
 * Description:
 * Date: 2018/1/22
 * Author: Huyu
 * Success is getting what you want; happiness is wanting what you get.
 */

public class DeviceListActivity extends AppCompatActivity implements DeviceDiscover.DiscoverListener {

	private DeviceDiscover mDiscover;
	private ArrayList<String> list;
	private ArrayAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);

		Toolbar toolbar = findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		toolbar.findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(DeviceListActivity.this, DeviceConfigActivity.class));
			}
		});

		swipeRefreshLayout = findViewById(R.id.srl);
		final ListView listView = findViewById(R.id.lv);

		swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_light),
				getResources().getColor(android.R.color.holo_green_light),
				getResources().getColor(android.R.color.holo_blue_light));

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {
				int topRowVerticalPosition =
						(listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
				swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {
			}

		});

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				mDiscover.startDiscover(3000);
			}
		});

		mDiscover = new DeviceDiscover();
		mDiscover.setDiscoverListener(this);
		list = new ArrayList<>();

		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String info = list.get(i);
				Intent intent = new Intent(DeviceListActivity.this, DeviceControlActivity.class);
				intent.putExtra("ip", info.substring(info.indexOf("\n") + 1));
				startActivity(intent);
			}
		});
	}

	@Override
	public void onreceived(List<Module> mModules) {
		list.clear();
		for (int i = 0; i < mModules.size(); i++) {
			Module module = mModules.get(i);
			list.add(module.getMac() + "\n" + module.getIp());
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(false);
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDiscover.stopDiscover();
	}
}

