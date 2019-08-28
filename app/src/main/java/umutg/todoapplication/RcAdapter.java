package umutg.todoapplication;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import static umutg.todoapplication.DatabaseHelper.todo_createtime;
import static umutg.todoapplication.DatabaseHelper.todo_id;
import static umutg.todoapplication.DatabaseHelper.todo_name;
import static umutg.todoapplication.DatabaseHelper.todo_status;
import static umutg.todoapplication.DatabaseHelper.todoitem_id;
import static umutg.todoapplication.DatabaseHelper.todoitem_status;

public class RcAdapter extends RecyclerView.Adapter<RcAdapter.RcViewHolder> {

    OnItemListener mOnItemListener;
    private OnItemClickListener mListener;
    long id;
    String status;

    private Context mContext;
    private Cursor mCursor;


    public interface OnItemClickListener {

        void onCheckboxClick(long position,boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public RcAdapter(Context context, Cursor cursor, OnItemListener onItemListener){

        mContext = context;
        mCursor = cursor;
        this.mOnItemListener = onItemListener;
    }

    public class RcViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameText;
        public TextView createTimeText;
        OnItemListener onItemListener;
        public CheckBox checkBox;

        public RcViewHolder(@NonNull final View itemView , OnItemListener onItemListener, final OnItemClickListener listener) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textview_name_item);
            createTimeText = itemView.findViewById(R.id.textview_createtime_item);
            checkBox = itemView.findViewById(R.id.checkboxHomeItem);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

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

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick((long) itemView.getTag());

        }
    }

    @NonNull
    @Override
    public RcViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.home_list_item,viewGroup,false);
        return new RcViewHolder(view,mOnItemListener,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RcViewHolder rcViewHolder, int i) {

        if(!mCursor.moveToPosition(i)){
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(todo_name));
        String createTime = mCursor.getString(mCursor.getColumnIndex(todo_createtime));
        status = mCursor.getString(mCursor.getColumnIndex(todo_status));
//        long id = mCursor.getLong(mCursor.getColumnIndex(todo_id));

        id = mCursor.getLong(mCursor.getColumnIndex(todo_id));

        rcViewHolder.nameText.setText(name);
        rcViewHolder.createTimeText.setText(createTime);

        rcViewHolder.checkBox.setClickable(false);
        rcViewHolder.checkBox.setChecked(status.equals("1"));

        rcViewHolder.itemView.setTag(id);
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

    public interface OnItemListener{
        void onItemClick(long position);
    }
}
