package com.example.coyg.todolist;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coyg.todolist.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
{
    private static final String DATE_FORMAT = "dd/MM/yyy";
    final private ItemClickListener mItemClickListener;
    private List<TaskEntry> mTaskEntries;
    private Context mContext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    public Adapter(Context context, ItemClickListener listener)
    {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter, parent, false);

        return new Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position)
    {
        TaskEntry taskEntry = mTaskEntries.get(position);
        String description = taskEntry.getDescription();
        int priority = taskEntry.getPriority();
        String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());
        String priorityString = "" + priority;

        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
        holder.priorityView.setText(priorityString);

//        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
//        int priorityColor = getPriorityColor(priority);
//        priorityCircle.setColor(priorityColor);
    }

//    private int getPriorityColor(int priority)
//    {
//        int priorityColor = 0;
//
//        switch (priority)
//        {
//            case 1:
//                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
//                break;
//            case 2:
//                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
//                break;
//            case 3:
//                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
//                break;
//            default:
//                break;
//        }
//        return priorityColor;
//    }


    public interface ItemClickListener
    {
        void onItemClickListener(int itemId);
    }

    @Override
    public int getItemCount()
    {
        if (mTaskEntries == null)
        {
            return 0;
        }
        return mTaskEntries.size();
    }

    public List<TaskEntry> getmTaskEntries()
    {

        return mTaskEntries;
    }

    public void setTasks(List<TaskEntry> taskEntries)
    {
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;

        public ViewHolder(View itemView)
        {
            super (itemView);

            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView = itemView.findViewById(R.id.priorityTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
