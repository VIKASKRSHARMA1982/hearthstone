package hertz.hertz.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

import hertz.hertz.R;
import hertz.hertz.activities.BaseActivity;
import hertz.hertz.model.Chat;

/**
 * Created by rsbulanon on 11/25/15.
 */
public class ChatAdapter extends BaseAdapter {

    private Context context;
    private BaseActivity activity;
    private ArrayList<Chat> chats;
    private LayoutInflater inflater;

    public ChatAdapter(Context context, ArrayList<Chat> chats) {
        this.context = context;
        this.chats = chats;
        this.activity = (BaseActivity)context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Chat getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.row_chat,null);
            holder = new ViewHolder();
            holder.tvMessage = (TextView)view.findViewById(R.id.tvMessage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        GradientDrawable bgShape = (GradientDrawable)holder.tvMessage.getBackground();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (getItem(position).getSender().equals(ParseUser.getCurrentUser().getObjectId())) {
            bgShape.setColor(ContextCompat.getColor(context, R.color.metro_blue));
            lp.gravity= Gravity.RIGHT;
        } else {
            bgShape.setColor(ContextCompat.getColor(context,R.color.metro_green));
            lp.gravity= Gravity.LEFT;
        }
        holder.tvMessage.setLayoutParams(lp);
        holder.tvMessage.setText(getItem(position).getMessage());
        return view;
    }

    public class ViewHolder {
        TextView tvMessage;
    }
}
