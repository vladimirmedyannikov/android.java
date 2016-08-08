package ru.mos.elk.netframework.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley2.RequestQueue;
import com.android.volley2.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.mos.elk.R;
import ru.mos.elk.netframework.model.results.Result;
import ru.mos.elk.netframework.model.results.ResultImg;
import ru.mos.elk.netframework.model.results.ResultType;
import ru.mos.elk.netframework.utils.BitmapLruCache;


public class DynamicsAdapter extends ArrayAdapter<Result> {
    private LayoutInflater inflater;
    private String strPattern;
    private List<Integer> notHidden;
    private Set<ResultType> clickable;
    private ImageLoader imgLoader;
	private ButtonListener buttonListener;
	private Map<String, Integer> styledLayouts = new HashMap<String, Integer>();
    
    public DynamicsAdapter(Context context, RequestQueue requestQueue, ButtonListener buttonListener) {
        super(context, 0);
        imgLoader = new ImageLoader(requestQueue, new BitmapLruCache());
        this.inflater = LayoutInflater.from(context);
        this.strPattern = null; 
        this.notHidden = null;
        this.buttonListener = buttonListener;
        this.clickable = new HashSet<ResultType>();
        clickable.add(ResultType.LINK);
        clickable.add(ResultType.RICH_LINK);
        clickable.add(ResultType.TABLE_LINK);
        clickable.add(ResultType.PHONE);
        clickable.add(ResultType.OUR_APP);
        clickable.add(ResultType.WWW);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
    	if(TextUtils.isEmpty(strPattern))
    		return super.getCount();
    	else
    		return notHidden.size();
    }
    
    @Override
    public boolean isEnabled(int position) {
    	if(position>=getCount()) return false;
    	
        ResultType type = getItem(getRealPosition(position)).getType();
        return clickable.contains(type);
    }

    /** we need this method because of hiding logic*/
    public int getRealPosition(int position){
    	return TextUtils.isEmpty(strPattern) ? position : notHidden.get(position);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Holder holder = null;
        Result resultItem = getItem(getRealPosition(position));
        ResultType type = resultItem.getType();
        String typeTag = type.toString()+resultItem.getStyle();
        if (convertView == null || !typeTag.equals(convertView.getTag(R.string.type_tag))) {
        	Integer styledLayout = styledLayouts.get(typeTag);
        	convertView = inflater.inflate(styledLayout==null?type.getLayout():styledLayout, parent, false);
        	convertView.setTag(R.string.type_tag, typeTag);
        	holder = type.getHolder();
        	holder.findViews(convertView);
        	if(type==ResultType.BUTTON)
        		((ButtonHolder)holder).buttonListener = buttonListener;
        	convertView.setTag(R.string.holder_tag, holder);
        }
        
        holder = (Holder) convertView.getTag(R.string.holder_tag);
        holder.fillView(position, resultItem);
       	
        if(type==ResultType.IMG || type==ResultType.OUR_APP || type==ResultType.RICH_LINK || type==ResultType.TABLE_LINK || type==ResultType.RICH_TEXT || type==ResultType.TABLE_TEXT)
        	loadImg((ImgHolder<? extends ResultImg>) holder, (ResultImg)resultItem);

        return convertView;
    }

	private void loadImg(ImgHolder<? extends ResultImg> holder, ResultImg imgItem){
        String src = imgItem.getSrc();
        if(src==null)
            holder.imgView.setVisibility(View.GONE);
        else {
            holder.imgView.setVisibility(View.VISIBLE);
            holder.imgView.setImageUrl(src, imgLoader);
            // #16653
            /*holder.imgView.setDefaultImageResId(R.drawable.elk_default_image);
            holder.imgView.setErrorImageResId(R.drawable.elk_default_image);*/
        }
    }
	
    //FIXME если новый strPattern включает старый, то фильтровать надо ранее отфильтрованные списки.
    /** set pattern for search in list. Search is made using toString() method of Results*/
	public void setPattern(String strPattern) {
		if(this.strPattern==strPattern) return;
		
		this.strPattern = strPattern;
		if(!TextUtils.isEmpty(strPattern)){
			notHidden = new ArrayList<Integer>();
			for(int position=0, count = super.getCount();position<count; position++){
				Result resultItem = getItem(position);
				if(resultItem.toString().toLowerCase().contains(strPattern.toLowerCase()))
					notHidden.add(position);
			}			
		} else
			this.notHidden = null;

		notifyDataSetChanged();
	}
	
	@Override
	public void add(Result resultItem) {
		if(notHidden!=null && resultItem.toString().toLowerCase().contains(strPattern.toLowerCase()))
			notHidden.add(super.getCount());
		super.add(resultItem);		
	}
    
	@Override
	public void clear() {
		strPattern = null;
		notHidden = null;
		super.clear();		
	}

	public String getPattern() {
		return strPattern;
	}

	/**
	 * @param type
	 */
	public void addClickable(ResultType type) {
		clickable.add(type);
	}
	
	public void removeClickable(ResultType type) {
		clickable.remove(type);
	}
 
	public void addStyledLayout(String style, int layout){
		styledLayouts.put(style, layout);
	}
}
