package activity;

import java.util.ArrayList;
import java.util.List;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.example.coolweather.R;

import model.City;
import model.County;
import model.Province;

import db.MyWeatherDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressdialog;
	private TextView titaltext;
	private ListView listview;
	private ArrayAdapter <String> adapter;
	private db.MyWeatherDB cooldb;
	private List <String > datalist = new ArrayList<String>();
	
	
	private List<Province> provincelist;
	private List <City> citylist;
	private List <County> countylist ;
	private Province sprovince;
	private City scity;
	private County scounty;
	private int current;
	
	private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity= getIntent().getBooleanExtra("from_weather_activity", false); 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
			Intent intent = new Intent (this,weatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
	
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listview = (ListView)findViewById(R.id.list_view);
		titaltext = (TextView)findViewById(R.id.tital_parent);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
		listview.setAdapter(adapter);
		cooldb = db.MyWeatherDB.getInstance(this);
		listview.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?>arg0,View view,int index, long arg3){
				if(current == LEVEL_PROVINCE){
					sprovince = provincelist.get(index);
					querycities() ;
				}else if(current ==LEVEL_CITY){
					scity = citylist.get(index);
					querycounties();
				}else if(current == LEVEL_COUNTY){
					String countyCode = countylist.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,weatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
					
				}
			}
		});
			queryProvinces();
}
	
	
	/*��ѯȫ������ʡ�����޴����ݿ��ѯ�����û��ȥ��������ѯ*/
	private void queryProvinces(){
		Log.d("111", "4");
		provincelist = cooldb.loadProvince();
		if(provincelist.size() >0){
			datalist.clear();
			for(Province province :provincelist){
				datalist.add(province.getprovinceName());			
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);			
			titaltext.setText("�й�");
			current = LEVEL_PROVINCE;
			
		}else {
			queryFromServer(null,"province");
		}
	}
	
	
	
	
	/*��ѯѡ��ʡ�ڵ�������*/
	private void querycities(){
		
		
		citylist = cooldb.loadCities(sprovince.getid());
		if(citylist.size() > 0){
			datalist.clear();
			for(City city : citylist){
				datalist.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titaltext.setText(sprovince.getprovinceName());
			current = LEVEL_CITY;
		}else{
			queryFromServer(sprovince.getprovinceCode(),"city");
		}
	}
	
	
	
	
	
	
	/*��ѯѡ���е������أ��������ݿ��ѯ��û����ȥ��������ѯ*/
	private void querycounties(){
		
		
		countylist = cooldb.loadcounty(scity.getId());
		if(countylist.size() > 0){
			datalist.clear();
			for(County county : countylist){
			
				datalist.add(county.getCountyName());
			}
			
			Log.d("111", "1");
			
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titaltext.setText(scity.getCityName());
			current = LEVEL_COUNTY;
		}else {
			queryFromServer(scity.getCityCode(),"county");
		}
	}
	
	
	
	/*�ӷ�������ѯʡ����*/
	private void queryFromServer (final String code,final String type){

		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
			
		}else {
			address ="http://www.weather.com.cn/data/list3/city.xml";
	}
		showprogressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			public void onFinish(String response){
				boolean result = false;
				
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(cooldb, response);
				}else if("city".equals(type)){
					result = Utility.handleCitiesResponse(cooldb, response, sprovince.getid());
				}else if("county".equals(type)){
					result =  Utility.handleCountiesResponse(cooldb, response, scity.getId());
				}
				if(result){
					runOnUiThread(new Runnable(){
						public void run (){
							
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
								
							}else if("city".equals(type)){
								querycities();
							}else if("county".equals(type)){
								querycounties();
							}
						}
					});
				}
			}	
				public void onError ( Exception e ) {
					runOnUiThread(new Runnable(){
						public void run (){
					
							closeProgressDialog();
							Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
						}
						
					});
				}
			});
}


	private void showprogressDialog() {
		// TODO Auto-generated method stub
		if (progressdialog == null){
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("���ڼ��ء�����");
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	
	
	private void closeProgressDialog(){
		if(progressdialog != null){
			progressdialog.dismiss();
		}
	}
	
	
	public void onBackPressed(){
		if(current == LEVEL_COUNTY){
			querycities();
		}else if (current == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this,weatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}