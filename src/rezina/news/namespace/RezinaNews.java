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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class RezinaNews extends TabActivity {

	private String categs[] = new String[1];
	private Integer ids[] = new Integer[1];
	//private boolean states[] = null;
	
	private class loadTabs  extends AsyncTask<Void, Void, Void> {
			private ProgressDialog progress;
	
			public loadTabs(ProgressDialog progress) {
			    this.progress = progress;
			}
	
			public void onPreExecute() {
				setTopics();
			    progress.show();
			}
	
			public Void doInBackground(Void... unused) {
				getIt();
				return null;
			}
	
			public void onPostExecute(Void unused) {
			    progress.dismiss();
			    setTopics();
				SharedPreferences category_prefs =getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
				SharedPreferences.Editor editar = category_prefs.edit();
				editar.putBoolean("catRelo",false);
				editar.commit();
			}
			  
	
			
	}
	
	private boolean networkDialog()	{
		if(haveNetworkConnection()==false)	{
			AlertDialog.Builder builder = new AlertDialog.Builder(RezinaNews.this);
	           builder.setMessage("Nu poate fi efectuată conexiunea la rețea.");
	           builder.setNegativeButton("IeÈ™ire", new DialogInterface.OnClickListener() {
		              public void onClick(DialogInterface dialog, int which) {
		                 finish();
		              } 
		           });
	           AlertDialog alert = builder.create();
	           alert.show();
		}	else	{
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
	/** Called when the activity is first created. */  
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if(networkDialog())	{
	        ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Încărcare listă categorii...");
			new loadTabs(progress).execute();
        }
        /*TimerTask mTimerTask = new TimerTask() {

            @Override
            public void run() {
            	//notifer();
           }
        };
        Timer mTimer = new Timer();
		SharedPreferences category_prefs =getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
        mTimer.scheduleAtFixedRate(mTimerTask,1000,category_prefs.getInt("timeRelo",99999999));*/
        
    }
    
   /* protected void notifer()	{
    	String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Hello";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = "My notification";
        CharSequence contentText = "Hello World!";
        Intent notificationIntent = new Intent(this, RezinaActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        mNotificationManager.notify(1, notification);
    }*/
    
	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences category_prefs =getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
		if(category_prefs.getBoolean("catRelo",false) && networkDialog())	{
	        ProgressDialog progress = new ProgressDialog(this);
			progress.setMessage("Încărcare listă categorii...");
			new loadTabs(progress).execute();
		}
	}
	
	public void setTopics(){
		SharedPreferences category_prefs =getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
        Drawable[] icons = new Drawable[categs.length];
        //Linear drawable set
        //icons[0]=getResources().getDrawable(R.drawable.cat1);
        //icons[1]=getResources().getDrawable(R.drawable.cat2);
        //icons[2]=getResources().getDrawable(R.drawable.cat3);
        //initialize TabHost
		TabHost mTabHost = getTabHost();
		//Clear tabs
		mTabHost.setCurrentTab(0);
		mTabHost.clearAllTabs();
		//Use intents to recreate tabs
		Intent homeIntent = new Intent().setClass(this,home.class);
		final Intent newsIntent = new Intent().setClass(this,news.class);
		//home tab is created automaticaly
		mTabHost.addTab(mTabHost.newTabSpec("homeTab")
								.setIndicator("     "+"Acasă"+"     ",getResources().getDrawable(R.drawable.home))
								.setContent(homeIntent));
		//Categories tab creation
        for(int i=0;i<categs.length;i++)
        	if(category_prefs.getBoolean(categs[i],false))	{
        		switch	(ids[i])	{
        			case 1:
        				icons[i] = getResources().getDrawable(R.drawable.cul);
        				break;
        			case 2:
        				icons[i] = getResources().getDrawable(R.drawable.edu);
        				break;
        			case 3:
        				icons[i] = getResources().getDrawable(R.drawable.congr);
        				break;
        			case 4:
        				icons[i] = getResources().getDrawable(R.drawable.dec);
        				break;
        			case 5:
        				icons[i] = getResources().getDrawable(R.drawable.mas);
        				break;
        			case 6:
        				icons[i] = getResources().getDrawable(R.drawable.lic);
        				break;
        			case 7:
        				icons[i] = getResources().getDrawable(R.drawable.eve);
        				break;
        			case 8:
        				icons[i] = getResources().getDrawable(R.drawable.inf);
        				break;
        			case 9:
        				icons[i] = getResources().getDrawable(R.drawable.soc);
        				break;
        			case 10:
        				icons[i] = getResources().getDrawable(R.drawable.spo);
        				break;
        			case 11:
        				icons[i] = getResources().getDrawable(R.drawable.fun);
        				break;
        			case 17:
        				icons[i] = getResources().getDrawable(R.drawable.constr);
        				break;
        			case 18:
        				icons[i] = getResources().getDrawable(R.drawable.agri);
        				break;
        			case 12:
        				icons[i] = getResources().getDrawable(R.drawable.dec);
        				break;
        			case 13:
        				icons[i] = getResources().getDrawable(R.drawable.dec);
        				break;
        			case 16:
        				icons[i] = getResources().getDrawable(R.drawable.dec);
        				break;
        			case 14:
        				icons[i] = getResources().getDrawable(R.drawable.econom);
        				break;
        			case 15:
        				icons[i] = getResources().getDrawable(R.drawable.calam);
        				break;
        			default:
        				icons[i] = getResources().getDrawable(R.drawable.oth);
        				break;
        		}
        		mTabHost.addTab(mTabHost.newTabSpec(ids[i].toString())
        				.setIndicator("     "+categs[i]+"     ",icons[i])
        				.setContent(newsIntent));
        	}
        mTabHost.setCurrentTab(0);
        //tab widget background
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++)	{
        		mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.default_tab);
        		TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        	    tv.setTextColor(0xff888888);
        }
        //selected tab background
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.pressed_tab);	
		TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title);
	    tv.setTextColor(0xffffffff);
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
        	
        	//when active tab changes
        	public void onTabChanged(String tabId) {
        	TabHost ourTabHost = getTabHost();
        	//simple tabs background
        	ourTabHost.getTabContentView().startAnimation(AnimationUtils.loadAnimation(ourTabHost.getTabContentView().getContext(), R.anim.rotate_left));
            //setAnimation(inFromRightAnimation());
        	for (int i = 0; i <ourTabHost.getTabWidget().getChildCount(); i++) { 
        		ourTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.default_tab); 
        		TextView tv = (TextView) ourTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        	    tv.setTextColor(0xff888888);
        	}
        	//selected tab background
        	ourTabHost.getTabWidget().getChildAt(ourTabHost.getCurrentTab())
        	.setBackgroundResource(R.drawable.pressed_tab);	
    		TextView tv = (TextView) ourTabHost.getTabWidget().getChildAt(ourTabHost.getCurrentTab()).findViewById(android.R.id.title);
    	    tv.setTextColor(0xffffffff);
        	}
        	
        	/*public Animation inFromRightAnimation() {

        	    Animation inFromRight = new TranslateAnimation(
        	            Animation.RELATIVE_TO_PARENT, +1.0f,
        	            Animation.RELATIVE_TO_PARENT, 0.0f,
        	            Animation.RELATIVE_TO_PARENT, 0.0f,
        	            Animation.RELATIVE_TO_PARENT, 0.0f);
        	    inFromRight.setDuration(100);
        	    inFromRight.setInterpolator(new AccelerateInterpolator());
        	    return inFromRight;
        	}*/

        });
    	
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
		ids = new Integer[cnt];
		try{
	      	JSONArray jArray = new JSONArray(postIt("http://consiliu.rezina.md/script/cats.php","2"));
	      	JSONObject json_data=null;
	      	
	      	for(int i=0;i<jArray.length();i++){
					json_data = jArray.getJSONObject(i);
					categs[i] = Html.fromHtml(json_data.getString("cat_name")).toString();
					ids[i] = json_data.getInt("cat_id");
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
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.item2://settings menu chosen
	        	startActivity(new Intent(this,settings.class));
	            return true;
	        case R.id.item1://refresh menu chosen
				TabHost tabHost = getTabHost();
				String currentTab = tabHost.getCurrentTabTag();
	        	SharedPreferences category_prefs =getSharedPreferences("LISTA_CATEGORII",MODE_PRIVATE);
				SharedPreferences.Editor editar = category_prefs.edit();
				if(currentTab.compareTo("homeTab")==0)	{
					for(int i=0;i<50;i++)
						editar.putBoolean("catRelo"+i,true);
					editar.commit();
				}	else	{
					editar.putBoolean("catRelo"+currentTab,true);
					Log.e("ANUS","@@@@@@@@@@@@@@@@@@@@@ "+currentTab);
					editar.commit();
					tabHost.setCurrentTab(0);
					tabHost.setCurrentTabByTag(currentTab);
				}
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
}