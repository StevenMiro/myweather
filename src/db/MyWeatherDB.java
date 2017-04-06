package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyWeatherDB {
	
	
	/*���ݿ����� */
	public static final String DB_NAME = "cool_weather";
	
	
	/*���ݿ�汾*/
	public static final int VERSION = 2;
	private static MyWeatherDB cooldb;
	private SQLiteDatabase db;
	
	
	
	/* ���췽��˽�л�  */
		private MyWeatherDB (Context context){
		MyWeatherOpenHelper dbHelper = new MyWeatherOpenHelper(context,DB_NAME,null,VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	
		
	/*��ȡ MyWeatherDB��ʵ��*/
	public synchronized static MyWeatherDB getInstance(Context context){
		if(cooldb == null){
			cooldb = new MyWeatherDB(context);
		}
		return cooldb;
	}
	
	
	
	/*�� provinceʵ���洢�����ݿ�*/
	public void saveProvince (Province province){
		if (province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getprovinceName());
			values.put("province_code", province.getprovinceCode());
			db.insert("Province", null, values);
		}
	}
	 
	
	
	/*�����ݿ��ȡȫ������ʡ�ݵ���Ϣ*/
	public List <Province> loadProvince(){
		List <Province> list = new ArrayList <Province>();
		
		Cursor cursor = db.query("Province", null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			
			do {
				Province province = new Province ();
				
				province.setid(cursor.getInt(cursor.getColumnIndex("id")));
				province.setprovinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setprovinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
				
			}while (cursor.moveToNext());
		}
		return list;
	}
	
	
	
	/*��cityʵ���洢�����ݿ�*/
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues ();
			values.put("city_name",city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	
	/*�����ݿ��ж�ȡĳʡ�����г�����Ϣ*/
	public List <City> loadCities (int provinceId){
		List <City>  list = new ArrayList <City>();
		Cursor cursor = db.query("City", null,"province_id = ?",new String []{String.valueOf(provinceId)},null,null,null);
		if (cursor.moveToFirst()){
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while (cursor.moveToNext());
		}
		return list;
	}
	
	
	
	/*��countyʵ���洢�����ݿ�*/
	public void saveCounty (County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	
	
	/*�����ݿ��ж�ȡĳ�������������Ϣ*/
	public List <County> loadcounty(int cityid){
		List <County> list = new ArrayList <County>();
		Cursor cursor = db.query("County", null, "city_id = ?", new String []{String.valueOf(cityid)}, null,null,null);
		if (cursor.moveToFirst()){
			do {
				County county = new County ();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityid);
				list.add(county);
			}while (cursor.moveToNext());
		}return list;
	}
}