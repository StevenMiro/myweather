package activity;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.example.coolweather.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.app.Activity;
import android.content.DialogInterface;


import android.content.Intent;
import android.content.SharedPreferences;

public  class weatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoL;
	private TextView cityNameT;
	private TextView publishT;
	private TextView weatherDespT;
	private TextView temp1T;
	private TextView temp2T;
	private TextView currentDateT;
	private Button switchCity;
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoL = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameT = (TextView) findViewById (R.id.city_name);
		publishT = (TextView) findViewById(R.id.publish_text);
		weatherDespT = (TextView) findViewById(R.id.weather_desp);
		temp1T = (TextView) findViewById(R.id.temp1);
		temp2T = (TextView) findViewById(R.id.temp2);
		currentDateT = (TextView) findViewById(R.id.current_date);
		switchCity = (Button)findViewById(R.id.switch_city);
		refreshWeather = (Button)findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			Log.d("333", "111");
			
			publishT.setText("ͬ���У���");
			weatherInfoL.setVisibility(View.INVISIBLE);
			cityNameT.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else {
			
			Log.d("333", "112");
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	private void queryWeatherCode(String countyCode){
		
		Log.d("333", "1");
		
		String address = "http://www.weather.com.cn/data/list3/city"+ countyCode +".xml";
		queryFromServer(address,"countyCode");
		
	Log.d("333", "2");
	}
	
	
	private void queryWeatherInfo(String weatherCode){
		Log.d("333", "3");
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
		
		Log.d("333", "4");
	}
	
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			public void onFinish (final String response){
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						Log.d("333", "5");
						String []array = response.split("\\|");
						if(array != null && array.length ==2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
							Log.d("333", "6");
						}
					}
				
				}else if("weatherCode".equals(type)){
					Log.d("333", "8");
					Utility.handWeatherResponse(weatherActivity.this, response);
					runOnUiThread(new Runnable(){
						public void run(){
							showWeather();
							Log.d("333", "9");
						}
					});
				}
			}
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					public void run(){
						publishT.setText("ͬ��ʧ��");
					}
				});
			}
		});

	}
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameT.setText(prefs.getString("city_name",""));
		temp1T.setText(prefs.getString ("temp1",""));
		temp2T.setText(prefs.getString("temp2",""));
		weatherDespT.setText(prefs.getString("weather_desp",""));
		publishT.setText("����"+prefs.getString("publish_time","")+"����");
		currentDateT.setText(prefs.getString("current_date", ""));
		weatherInfoL.setVisibility(View.VISIBLE);
		cityNameT.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	
		// TODO Auto-generated method stub
	switch(v.getId()){
		case R.id.switch_city :
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather :
			publishT.setText("ͬ���У�����");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if ( !TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default :
			break;
		}
	}
}
