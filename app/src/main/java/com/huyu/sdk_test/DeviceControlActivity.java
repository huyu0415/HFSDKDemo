package com.huyu.sdk_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huyu.lanconfig_hf.control.DeviceController;
import com.huyu.lanconfig_hf.entity.AutoAlarmEntity;
import com.huyu.lanconfig_hf.entity.ZoneEntity;
import com.huyu.lanconfig_hf.listener.ControlListener;
import com.huyu.lanconfig_hf.utils.CMDUtils.CmdName;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ___                      _
 * / __|   ___   ___   ___   | |  ___
 * | (_--- / _ \ / _ \ / _ \  | | /___)
 * \____| \___/ \___/ \___/| |_| \___
 * /
 * \___/
 * Description:
 * Date: 2018/1/20
 * Author: Huyu
 * Success is getting what you want; happiness is wanting what you get.
 */

public class DeviceControlActivity extends AppCompatActivity implements View.OnClickListener {

	DeviceController mDeviceController;

	ControlListener mControlListener = new ControlListener() {

		@Override
		public void openTTSResponse(boolean success) {
			if (success) {
				// get data
				mDeviceController.sendCMD(CmdName.GETSTATU, 0, 0, null);
			} else {
				// toast failed
				Toast.makeText(DeviceControlActivity.this, "Open TTS Failed", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		public void didGetStatus(ConcurrentHashMap<CmdName, Object> map) {
			// Zone 1...8
			ZoneEntity zoneEntity1 = (ZoneEntity) map.get(CmdName.ZONE1ATTR);
			//		.
			//		2 - 7
			//		.
			ZoneEntity zoneEntity8 = (ZoneEntity) map.get(CmdName.ZONE8ATTR);
			et_zone.setText(String.format("1-%s-%s", zoneEntity1.getAttr(), zoneEntity1.getCode()));

			// Alarm Number
			String alarmNumber1 = (String) map.get(CmdName.ALARMNUM1);
			String alarmNumber2 = (String) map.get(CmdName.ALARMNUM2);
			String alarmNumber3 = (String) map.get(CmdName.ALARMNUM3);
			et_num.setText(String.format("2-%s", alarmNumber2));

			// Timing Arm/Disarm 1
			AutoAlarmEntity autoAlarmEntity1 = (AutoAlarmEntity) map.get(CmdName.AUTOARM1);
			AutoAlarmEntity autoAlarmEntity2 = (AutoAlarmEntity) map.get(CmdName.AUTOARM2);
			String armTime1 = autoAlarmEntity1.getHour() + ":" + autoAlarmEntity1.getMinute();
			String disarmTime1 = autoAlarmEntity2.getHour() + ":" + autoAlarmEntity2.getMinute();
			// TODO: 2018/1/22 week is binary string like: 0101010 means Tue, Thu, Sat; from the last bit to first bit is Mon, Tue, Wed... 1 is work, 0 is not work
			String week1 = autoAlarmEntity1.getWeekRepeat();
			boolean isEnable1 = autoAlarmEntity1.getIsOn();
			et_timing.setText(String.format("1-%s-%s-%s-%s", week1, armTime1, disarmTime1, isEnable1 ? "1" : "0"));

			// Timing Arm/Disarm 2
			AutoAlarmEntity autoAlarmEntity3 = (AutoAlarmEntity) map.get(CmdName.AUTOARM3);
			AutoAlarmEntity autoAlarmEntity4 = (AutoAlarmEntity) map.get(CmdName.AUTOARM4);
			String armTime2 = autoAlarmEntity3.getHour() + ":" + autoAlarmEntity3.getMinute();
			String disarmTime2 = autoAlarmEntity4.getHour() + ":" + autoAlarmEntity4.getMinute();
			// TODO: 2018/1/22 week is binary string like: 0101010 means Tue, Thu, Sat; from the last bit to first bit is Mon, Tue, Wed... 1 is work, 0 is not work
			String week2 = autoAlarmEntity3.getWeekRepeat();
			boolean isEnable2 = autoAlarmEntity3.getIsOn();

			// Alarm tone  0 -- siren; 1 -- bark; 2 -- devil1; 3 -- devil2
			int tone = (Integer) map.get(CmdName.ALARMTONE);
			et_tone.setText(String.valueOf(tone));

			// Alarm Volume  [0, 10]
			int alarmVol = (Integer) map.get(CmdName.ALARMVOL);
			et_alarm_vol.setText(String.valueOf(alarmVol));

			// Ring Times  [0, 20]
			int ringTimes = (Integer) map.get(CmdName.RINGTIMES);
			et_ring_times.setText(String.valueOf(ringTimes));

			// Device status  0 -- Arm;  1 -- Disarm;  2 -- Stay
			int status = (Integer) map.get(CmdName.ARM_DISARM);
			et_status.setText(String.valueOf(status));

			// Panel Volume	 [0, 10]
			int voiceVol = (Integer) map.get(CmdName.VOICEVOL);
			et_panel_vol.setText(String.valueOf(voiceVol));

			// Panel Password must be 6 bit
			String pwd = (String) map.get(CmdName.PASSWORD);
			et_panel_pwd.setText(pwd);

			// Panel Time
			String rtc = (String) map.get(CmdName.RTC);
//			String panelTime = String.format("20%s-%s-%s %s:%s:%s", rtc.substring(0, 2), rtc.substring(2, 4), rtc.substring(4, 6), rtc.substring(6, 8), rtc.substring(8, 10), rtc.substring(10));
			et_panel_time.setText(rtc);

			// Server Params  cid must be 6 bit
			String ip = (String) map.get(CmdName.IP);
			String port = (String) map.get(CmdName.PORT);
			String cid = (String) map.get(CmdName.CID);
			et_server_ip.setText(ip);
			et_server_port.setText(port);
			et_server_cid.setText(cid);

			// Panel sofeware version
			String version = (String) map.get(CmdName.VERSION);
			tv_hostVersion.setText(String.format("V%s", version));
		}
	};
	private EditText et_zone,et_num,et_timing,et_tone,et_alarm_vol,et_ring_times,
		et_status,et_panel_vol,et_panel_pwd,et_panel_time,et_server_ip,et_server_port,et_server_cid;
	private TextView tv_hostVersion;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_device_control);

		String ip = getIntent().getStringExtra("ip");

		mDeviceController = new DeviceController();
		mDeviceController.init(ip);

		mDeviceController.setControlListener(mControlListener);

		initViews();
	}

	private void initViews() {

		Toolbar toolbar = findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);

		et_zone = findViewById(R.id.et_zone);
		Button btn_zone = findViewById(R.id.btn_zone);
		et_num = findViewById(R.id.et_num);
		Button btn_num = findViewById(R.id.btn_num);
		et_timing = findViewById(R.id.et_timing);
		Button btn_timing = findViewById(R.id.btn_timing);
		et_tone = findViewById(R.id.et_tone);
		Button btn_tone = findViewById(R.id.btn_tone);
		et_alarm_vol = findViewById(R.id.et_alarm_vol);
		Button btn_alarm_vol = findViewById(R.id.btn_alarm_vol);
		et_ring_times = findViewById(R.id.et_ring_times);
		Button btn_ring_times = findViewById(R.id.btn_ring_times);
		et_status = findViewById(R.id.et_status);
		Button btn_status = findViewById(R.id.btn_status);
		et_panel_vol = findViewById(R.id.et_panel_vol);
		Button btn_panel_vol = findViewById(R.id.btn_panel_vol);
		et_panel_pwd = findViewById(R.id.et_panel_pwd);
		Button btn_panel_pwd = findViewById(R.id.btn_panel_pwd);
		et_panel_time = findViewById(R.id.et_panel_time);
		Button btn_panel_time = findViewById(R.id.btn_panel_time);
		et_server_ip = findViewById(R.id.et_server_ip);
		et_server_port = findViewById(R.id.et_server_port);
		et_server_cid = findViewById(R.id.et_server_cid);
		Button btn_server = findViewById(R.id.btn_server);
		tv_hostVersion = findViewById(R.id.tv_hostVersion);


		btn_zone.setOnClickListener(this);
		btn_num.setOnClickListener(this);
		btn_timing.setOnClickListener(this);
		btn_tone.setOnClickListener(this);
		btn_alarm_vol.setOnClickListener(this);
		btn_ring_times.setOnClickListener(this);
		btn_status.setOnClickListener(this);
		btn_panel_vol.setOnClickListener(this);
		btn_panel_pwd.setOnClickListener(this);
		btn_panel_time.setOnClickListener(this);
		btn_server.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDeviceController.exitControl();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
		    case R.id.btn_zone:
				String text[] = et_zone.getText().toString().split("-");
				mDeviceController.setZoneAttr(Integer.parseInt(text[0]), text[1]);
//				mDeviceController.setZoneCode(Integer.parseInt(text[0]), text[2]);
		    	break;

			case R.id.btn_num:
				String text1[] = et_num.getText().toString().split("-");
				mDeviceController.setAlarmNum(Integer.parseInt(text1[0]), text1[1]);
				break;

			case R.id.btn_timing:
				String text2[] = et_timing.getText().toString().split("-");
				mDeviceController.setAutoTiming(Integer.parseInt(text2[0]), text2[1], text2[2], text2[3], text2[4].equals("1"));
				break;

		    case R.id.btn_tone:
				String text3 = et_tone.getText().toString();
				mDeviceController.setAlarmTone(text3);
		    	break;

			case R.id.btn_alarm_vol:
				String text4 = et_alarm_vol.getText().toString();
				mDeviceController.setAlarmVol(Integer.parseInt(text4));
				break;

			case R.id.btn_ring_times:
				String text5 = et_ring_times.getText().toString();
				mDeviceController.setRingTimes(Integer.parseInt(text5));
				break;

		    case R.id.btn_status:
				String text6 = et_status.getText().toString();
				mDeviceController.setArmDisarmStatus(Integer.parseInt(text6));
		    	break;

			case R.id.btn_panel_vol:
				String text7 = et_panel_vol.getText().toString();
				mDeviceController.setVoiceVol(Integer.parseInt(text7));
				break;

			case R.id.btn_panel_pwd:
				String text8 = et_panel_pwd.getText().toString();
				mDeviceController.setDevicePwd(text8);
				break;

		    case R.id.btn_panel_time:
				String text9 = et_panel_time.getText().toString();
				mDeviceController.setDeviceTime(text9.substring(0, 2), text9.substring(2, 4), text9.substring(4, 6),
						text9.substring(6, 8), text9.substring(8, 10), text9.substring(10, 12));
		    	break;

			case R.id.btn_server:
				String ip = et_server_ip.getText().toString();
				String port = et_server_port.getText().toString();
				String cid = et_server_cid.getText().toString();
				mDeviceController.setDeviceServerParams(ip, Integer.parseInt(port), cid);
				break;

		    default : break;
		}
	}
}
