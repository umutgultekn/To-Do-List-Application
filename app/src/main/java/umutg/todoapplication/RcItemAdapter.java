package umutg.todoapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static umutg.todoapplication.DatabaseHelper.todoitem_createdate;
import static umutg.todoapplication.DatabaseHelper.todoitem_deadline;
import static umutg.todoapplication.DatabaseHelper.todoitem_description;
import static umutg.todoapplication.DatabaseHelper.todoitem_id;
import static umutg.todoapplication.DatabaseHelper.todoitem_name;
import static umutg.todoapplication.DatabaseHelper.todoitem_status;

public class RcItemAdapter extends RecyclerView.Adapter<RcItemAdapter.RcItemViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    String name,description,deadline,createTime,status;
    private OnItemClickListener mListener;
    long id;

    Date dateDeadline;
    long diff;


    private static final String TAG = "RcItemAdapter";


    public interface OnItemClickListener {

        void onCheckboxClick(long position,boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    
    

    public RcItemAdapter(Context context, Cursor cursor){

        mContext = context;
        mCursor = cursor;
    }

    public class RcItemViewHolder extends RecyclerView.ViewHolder{

        public TextView nameText;
        public TextView descriptionText;
        public TextView deadlineText;
        public TextView createTimeText;
        public TextView statusText;
        public LinearLayout mLayout;
        public CheckBox checkBox;

        public RcItemViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textview_name_itempage);
            descriptionText = itemView.findViewById(R.id.textview_description_itempage);
            deadlineText = itemView.findViewById(R.id.textview_deadline_itempage);
            createTimeText = itemView.findViewById(R.id.textview_createtime_itempage);
            statusText = itemView.findViewById(R.id.textview_status_itempage);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            mLayout = itemView.findViewById(R.id.layout_color);


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {

                        boolean x = checkBox.isChecked();

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCheckboxClick((long)itemView.getTag(),x);
                        }
                    }
                }
            });

        }

    }
    @NonNull
    @Override
    public RcItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_list,viewGroup,false);
        return new RcItemViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RcItemViewHolder rcItemViewHolder, final int i) {


        if(!mCursor.moveToPosition(i)){
            return;
        }

        name = mCursor.getString(mCursor.getColumnIndex(todoitem_name));
        description = mCursor.getString(mCursor.getColumnIndex(todoitem_description));
        deadline = mCursor.getString(mCursor.getColumnIndex(todoitem_deadline));
        createTime = mCursor.getString(mCursor.getColumnIndex(todoitem_createdate));
        status = mCursor.getString(mCursor.getColumnIndex(todoitem_status));

        id = mCursor.getLong(mCursor.getColumnIndex(todoitem_id));

        rcItemViewHolder.nameText.setText(name);
        rcItemViewHolder.descriptionText.setText(description);
        rcItemViewHolder.deadlineText.setText(deadline);
        rcItemViewHolder.createTimeText.setText(createTime);
        rcItemViewHolder.statusText.setText(status);

        rcItemViewHolder.checkBox.setChecked(status.equals("1"));

        rcItemViewHolder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }

        mCursor = newCursor;

        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

}
