package com.huyu.sdk_test;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;
import com.huyu.lanconfig_hf.control.DeviceConfiger;

/**
 * ___                      _
 * / __|   ___   ___   ___   | |  ___
 * | (_--- / _ \ / _ \ / _ \  | | /___)
 * \____| \___/ \___/ \___/| |_| \___
 * /
 * \___/
 * Description:
 * Date: 2018/1/23
 * Author: Huyu
 * Success is getting what you want; happiness is wanting what you get.
 */

public class DeviceConfigActivity extends AppCompatActivity {

	private DeviceConfiger mDeviceConfiger;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_device_config);

		Toolbar toolbar = findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);

		final Button btn_ssid = findViewById(R.id.btn_ssid);
		final EditText et_pwd = findViewById(R.id.pwd);
		Button btn_config = findViewById(R.id.btn_config);

		dialog = new ProgressDialog(this);
		dialog.setMessage("Waiting for config...");
		dialog.setCanceledOnTouchOutside(false);

		btn_config.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String ssid = btn_ssid.getText().toString();
				String pwd = et_pwd.getText().toString();
				if(ssid.length() > 0) {
					mDeviceConfiger.startConfigWifi(DeviceConfigActivity.this, ssid, pwd);
					dialog.show();
				}
			}
		});


		mDeviceConfiger = new DeviceConfiger();
		mDeviceConfiger.setListener(new OnSmartLinkListener() {
			@Override
			public void onLinked(final SmartLinkedModule smartLinkedModule) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceConfigActivity.this,
								"Config success\nIP = " + smartLinkedModule.getIp() + ";\nMAC = " + smartLinkedModule.getMac(), Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onCompleted() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceConfigActivity.this,
								"Config finish", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						finish();
					}
				});
			}

			@Override
			public void onTimeOut() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceConfigActivity.this,
								"Config timeout", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						finish();
					}
				});
			}
		});


		btn_ssid.setText(mDeviceConfiger.getCurrentSSid(this));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDeviceConfiger.stopConfigWifi();
	}
}
