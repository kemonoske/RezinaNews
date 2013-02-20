package rezina.news.namespace;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class optionsArray extends ArrayAdapter<String> {
	private final Context context;
	private final String[] titles;
	private final String[] short_articles;
	private final Drawable[] thumbnail;

	public optionsArray(Context context, String[] titles, String[] short_articles, Drawable[] thumbnail) {
		super(context, R.layout.news_short, titles);
		this.context = context;
		this.titles =titles;
		this.short_articles = short_articles;
		this.thumbnail = thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.option, parent, false);
		TextView textView1 = (TextView) rowView.findViewById(R.id.opt_title);
		textView1.setText(titles[position]);
		TextView textView2 = (TextView) rowView.findViewById(R.id.opt_desc);
		textView2.setText(short_articles[position]);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.opt_thumb);
		imageView.setImageDrawable(thumbnail[position]);
		return rowView;
	}
	
}
