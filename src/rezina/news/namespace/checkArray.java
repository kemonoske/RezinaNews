package rezina.news.namespace;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class checkArray extends ArrayAdapter<String> {
	private final Context context;
	private final String[] name;
	private final boolean[] state;
	private CheckBox cb1;


	public checkArray(Context context, String[] name, boolean[] state) {
		super(context, R.layout.news_short, name);
		this.context = context;
		this.name=name;
		this.state=state;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.category, parent, false);
		cb1= (CheckBox) rowView.findViewById(R.id.state);
		cb1.setText(name[position]);
		cb1.setChecked(state[position]);
		cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences category_prefs =context.getSharedPreferences("LISTA_CATEGORII",context.MODE_PRIVATE);
				SharedPreferences.Editor editar = category_prefs.edit();
				if(isChecked)
					editar.putBoolean((String) buttonView.getText(),true);
				else
					editar.putBoolean((String) buttonView.getText(),false);
				editar.putBoolean("catRelo",true);
				editar.commit();
			}
		});
		return rowView;
	}
	
}