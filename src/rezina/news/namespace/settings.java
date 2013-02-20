package rezina.news.namespace;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
        Drawable[] newsThumb = new Drawable[3]; 
        //load thumbnails
        newsThumb[0]=getResources().getDrawable(R.drawable.catopt);
        //newsThumb[1]=getResources().getDrawable(R.drawable.refreshopt);
        newsThumb[1]=getResources().getDrawable(R.drawable.aboutopt);
		String[] option_list = new String[] {"Categorii afisate",/*"Optiuni actualizare",*/"Despre program"};
		ListView options = (ListView)findViewById(R.id.option_list);
		options.setAdapter(new optionsArray(this,option_list,option_list,newsThumb));

		options.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
							if(position==0)
								startActivity(new Intent(view.getContext(),categories.class));
							/*else if (position==1)
								startActivity(new Intent(view.getContext(),sync.class));*/
							else if (position==1)	{
								//set up dialog
				                final Dialog dialog = new Dialog(settings.this);
				                dialog.setContentView(R.layout.about);
				                dialog.setTitle("Consiliul Raional Rezina");
				                dialog.setCancelable(true);
				                //there are a lot of settings, for dialog, check them all out!
				 
				                //set up text
				                TextView text = (TextView) dialog.findViewById(R.id.TextView01);
				                text.setText(R.string.license);
				 
				                //set up image view
				                ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);
				                img.setImageResource(R.drawable.ic_launcher);
				                //set up button
				                Button butto = (Button) dialog.findViewById(R.id.Button01);
				                butto.setOnClickListener(new OnClickListener() {
									
									public void onClick(View v) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
				                dialog.show();
							}
				}

		});
	}
	


}
