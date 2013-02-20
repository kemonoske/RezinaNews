package rezina.news.namespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class article extends Activity {

	private String newsTitle = new String();
    private String newsContent = new String();
    private String artId = new String();
    private Drawable newsThumb = null; 
    private String categId = new String();

	private class loadArt  extends AsyncTask<Void, Void, Void> {
			private ProgressDialog progress;
	
			public loadArt(ProgressDialog progress) {
			    this.progress = progress;
			}
	
			public void onPreExecute() {
				setArt();
			    progress.show();
			}
	
			public Void doInBackground(Void... unused) {
				getIt();
				return null;
			}
	
			public void onPostExecute(Void unused) {
			    progress.dismiss();
			    setArt();
			}
	}

	private void setArt()	{
		TextView title = (TextView)findViewById(R.id.articleTitle);
		title.setText(newsTitle);
		WebView content = (WebView)findViewById(R.id.articleContent);
		
		newsContent="<!doctype html>"
				   + "<meta	http-equiv=\"content-type\"	content=\"text/html;charset=UTF-8\"/>"
				   + "<html>"
				   + "<body>"  
				   + "<hr/>"
				   + newsContent.replaceAll("&icirc;", "î").replaceAll("\\+","")
				   + "<hr/>"
				   + "</body>"
				   + "</html>";
		
		content.loadDataWithBaseURL(null, newsContent.trim(), "text/html", "UTF-8", null);
		content.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
		content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ImageView thumb = (ImageView)findViewById(R.id.articleThumb);

		try	{
			newsThumb.getBounds().height();	
			thumb.setImageDrawable(newsThumb);
			LayoutParams lp = new LinearLayout.LayoutParams(
					320,
					125
					);
			lp.gravity = Gravity.CENTER;
			thumb.setLayoutParams(
					lp
					);
		}catch (NullPointerException e)	{
			thumb.setLayoutParams(
					new LinearLayout.LayoutParams(
							0,
							0
							)
					);
		}
	}


	protected void getIt()	{
		try{
	      	JSONArray jArray = new JSONArray(postIt("http://consiliu.rezina.md/script/arts.php","2",categId,artId));
	      	JSONObject json_data=null;
	      	
	      	for(int i=0;i<jArray.length();i++){
					json_data = jArray.getJSONObject(i);
					newsTitle = Html.fromHtml(json_data.getString("title")).toString();
					newsContent = json_data.getString("content");
					try {
						if(categId.compareTo("3")==0)	{
							newsThumb = drawableFromUrl("http://consiliu.rezina.md/content_imgs/congratulations_img/"+json_data.getString("thumb"));
							
						}
						else
							newsThumb = drawableFromUrl("http://consiliu.rezina.md/content_imgs/articles_imgs/"+json_data.getString("thumb"));
					} catch (IOException e) {
						e.printStackTrace();
					}
	      	}
	      	
			}catch(JSONException e1){
				newsThumb = null;
				newsTitle = "Nu există articol cu identificatorul dat.";
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
	
    protected String postIt(String URL,String count,String catId,String artId){
		String result = null;
		InputStream is = null;
		StringBuilder sb=null;
		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cat_id", catId));
			params.add(new BasicNameValuePair("art_id", artId));
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
		/*ImageView iV = (ImageView) findViewById(R.id.articleThumb);
		Drawable thu = null;
		try {
			thu = drawableFromUrl("http://consiliu.rezina.md/content_imgs/articles_imgs/144/thumb.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		iV.setImageDrawable(thu);*/
		//iV.setImageResource(R.drawable.thumb);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(networkDialog())	{
			Intent fullArt = getIntent();
			artId = String.valueOf(fullArt.getExtras().getInt("artId"));
			categId = fullArt.getExtras().getString("categId");
			Log.e("ANUS","@@@@@@@@@@@@@@ categ="+artId);
			Log.e("ANUS","@@@@@@@@@@@@@@ categ="+categId);
			ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Încărcare articol...");
			new loadArt(progress).execute();
		}
	}
	

	private boolean networkDialog()	{
		if(haveNetworkConnection()==false)	{
			AlertDialog.Builder builder = new AlertDialog.Builder(article.this);
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
	
	
}
