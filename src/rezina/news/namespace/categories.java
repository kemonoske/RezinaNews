package rezina.news.namespace;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class categories extends Activity {
	private String[] categs = new String[1];
	private boolean[] states = new boolean[1];
	private ListView categ;
	

	private class loadTabs  extends AsyncTask<Void, Void, Void> {
			private ProgressDialog progress;
	
			public loadTabs(ProgressDialog progress) {
			    this.progress = progress;
			}
	
			public void onPreExecute() {
				setCats();
			    progress.show();
			}
	
			public Void doInBackground(Void... unused) {
				getIt();
				return null;
			}
	
			public void onPostExecute(Void unused) {
			    progress.dismiss();
				setCats();
			}
			  
	
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(networkDialog())	{
			ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Încărcare listă categorii...");
			new loadTabs(progress).execute();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories);
		
	}


	private boolean networkDialog()	{
		if(haveNetworkConnection()==false)	{
			AlertDialog.Builder builder = new AlertDialog.Builder(categories.this);
	           builder.setMessage("Nu poate fi efectuată conexiunea la reţea.");
	           builder.setNegativeButton("Ieșire", new DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int which) {
		                 finish();
		              } 
		           });
	           AlertDialog alert = builder.create();
	           alert.show();
		}		else	{
			return true;
		}
		return false;
	}
	
	private boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}
    @Override
	protected void onStop() {
		super.onStop();
		Toast.makeText(getApplicationContext(),
				"Lista de categorii a fost salvată cu succes.", Toast.LENGTH_LONG)
				.show();
	}
    
    protected void setCats()	{
    	states = new boolean[categs.length];
		categ =(ListView) findViewById(R.id.category_list);
		SharedPreferences category_prefs = getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
		for(int i=0;i<categs.length;i++)	
			states[i]=category_prefs.getBoolean(categs[i],false);
		checkArray adapter = new checkArray(this,categs,states);
		categ.setAdapter(adapter);
    }

	protected void getIt()	{
		Integer cnt = 0;
		try{
	  	JSONObject json_data=new JSONObject(postIt("http://consiliu.rezina.md/script/cats.php","0"));
		cnt = json_data.getInt("count");
		}catch(JSONException e1){
			categs[0] = "Nu putem accesa datele";
		}catch (ParseException e1){
			e1.printStackTrace();
		}
		categs = new String[cnt];
		try{
	      	JSONArray jArray = new JSONArray(postIt("http://consiliu.rezina.md/script/cats.php","1"));
	      	JSONObject json_data=null;
	      	
	      	for(int i=0;i<jArray.length();i++){
					json_data = jArray.getJSONObject(i);
					categs[i] = Html.fromHtml(json_data.getString("cat_name")).toString();
	      	}
	      	
			}catch(JSONException e1){
				categs[0] = "Nu putem accesa datele";
			}catch (ParseException e1){
				e1.printStackTrace();
			}
	}
	
    protected String postIt(String URL,String count){
		String result = null;
		InputStream is = null;
		StringBuilder sb=null;
		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("count", count));
			httppost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection"+e.toString());
		}
	
		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line="0";
	     
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			is.close();
			result=sb.toString();
			
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		return result;
    }
    

}
