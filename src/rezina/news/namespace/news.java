package rezina.news.namespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

public class news extends Activity {

	private String[] newsTitles = new String[1];
    private String[] newsShort = new String[1];
    private Integer[] ids = new Integer[1];
    Drawable[] newsThumb = new Drawable[1]; 
    private String categId = new String();
    

	private class loadArt  extends AsyncTask<Void, Void, Void> {
			private ProgressDialog progress;
	
			public loadArt(ProgressDialog progress) {
			    this.progress = progress;
			}
	
			public void onPreExecute() {
				setArts();
			    progress.show();
			}
	
			public Void doInBackground(Void... unused) {
				getIt();
				return null;
			}
	
			public void onPostExecute(Void unused) {
			    progress.dismiss();
			    setArts();
				SharedPreferences category_prefs = getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
				TabHost tabHost = (TabHost) getParent().findViewById(android.R.id.tabhost);
				SharedPreferences.Editor editar = category_prefs.edit();
				editar.putBoolean("catRelo"+tabHost.getCurrentTabTag(),false);
				editar.commit();
			}
			  
	
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
    		
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences category_prefs = getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
		TabHost tabHost = (TabHost) getParent().findViewById(android.R.id.tabhost);
		if(category_prefs.getBoolean("catRelo"+tabHost.getCurrentTabTag(),false) && networkDialog())	{
			ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Încărcare listă articole...");
			new loadArt(progress).execute();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news); 
		if(networkDialog())	{
			ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Încărcare listă articole...");
			new loadArt(progress).execute();
		}
	}
	

	private boolean networkDialog()	{
		if(haveNetworkConnection()==false)	{
			AlertDialog.Builder builder = new AlertDialog.Builder(news.this);
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
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	private void setArts()	{
		TabHost tabHost = (TabHost) getParent().findViewById(android.R.id.tabhost);
		categId = tabHost.getCurrentTabTag();
	    //Initialize ListView
		ListView listView1 = (ListView)findViewById(R.id.listView1);
	    newsArray newsAdapter1 = new newsArray(this, newsTitles, newsShort, newsThumb);
	    if(listView1==null)
	    	Toast.makeText(getApplicationContext(),
						"list is null", Toast.LENGTH_LONG)
						.show();
	    else
	    	listView1.setAdapter(newsAdapter1);

	    listView1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(newsTitles[arg2].compareTo("Nu există articole în categoria dată") != 0)	{
					Intent fullArt = new Intent(arg0.getContext(),article.class);
					fullArt.putExtra("artId", ids[arg2]);
					fullArt.putExtra("categId", categId);
					startActivity(fullArt);
				}
			}
	    	
		});
	    
	}

	protected void getIt()	{
		Integer cnt = 0;
		try{
	  	JSONObject json_data=new JSONObject(postIt("http://consiliu.rezina.md/script/arts.php","0",categId));
		cnt = json_data.getInt("count");
      	Log.e("CNT ========================", String.valueOf(cnt));
		}catch(JSONException e1){
			newsTitles[0] = "Nu există articole în categoria dată";
		}catch (ParseException e1){
			e1.printStackTrace();
		}
		if(cnt!=0)	{
			newsTitles = new String[cnt];
			newsShort = new String[cnt];
			ids = new Integer[cnt];
			newsThumb = new Drawable[cnt];
		}
	   /* for(int i=0;i<newsThumb.length;i++)
	        newsThumb[i]=getResources().getDrawable(R.drawable.ic_launcher);*/
		try{
	      	JSONArray jArray = new JSONArray(postIt("http://consiliu.rezina.md/script/arts.php","1",categId));
	      	JSONObject json_data=null;
	      	Log.e("JArray ========================", String.valueOf(jArray.length()));
	      	for(int i=0;i<jArray.length();i++){
					json_data = jArray.getJSONObject(i);
					newsTitles[i] = Html.fromHtml(json_data.getString("title")).toString();
					newsShort[i] = Html.fromHtml(json_data.getString("content")).toString();
					ids[i] = json_data.getInt("id");
					try {
						if(categId.compareTo("3")==0)	{
							newsThumb[i] = drawableFromUrl("http://consiliu.rezina.md/content_imgs/congratulations_img/"+json_data.getString("thumb"));
							
						}
						else
							newsThumb[i] = drawableFromUrl("http://consiliu.rezina.md/content_imgs/articles_imgs/"+json_data.getString("thumb"));
					} catch (IOException e) {
						e.printStackTrace();
					}
	      	}
	      	
			}catch(JSONException e1){
				newsThumb[0] = getResources().getDrawable(R.drawable.aboutopt);
				newsTitles[0] = "Nu există articole în categoria dată";
			}catch (ParseException e1){
				e1.printStackTrace();
			}
	}
	
	public static Drawable drawableFromUrl(String url) throws IOException {
	    Bitmap x;

	    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	    connection.connect();
	    InputStream input = connection.getInputStream();

	    x = BitmapFactory.decodeStream(input);
	    return new BitmapDrawable(x);
	}
	
    protected String postIt(String URL,String count,String catId){
		String result = null;
		InputStream is = null;
		StringBuilder sb=null;
		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cat_id", catId));
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
