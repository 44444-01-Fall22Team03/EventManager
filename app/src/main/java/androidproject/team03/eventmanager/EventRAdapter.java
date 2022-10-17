package androidproject.team03.eventmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventRAdapter extends RecyclerView.Adapter<EventRAdapter.EventViewHolder> {

    Context context;
    private EventModel myModel;

    public EventRAdapter(Context context, EventModel myModel){
        this.context= context;
        this.myModel = myModel;
    }

    @NonNull
    @Override
    public EventRAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recylcer_view_row,parent,false);
        return new EventRAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventRAdapter.EventViewHolder holder, int position) {
        holder.eventNameTV.setText(myModel.eventsList.get(position).eventName);
        holder.eventStDtTV.setText(myModel.eventsList.get(position).eventStartDt);
        holder.eventEndDtTV.setText(myModel.eventsList.get(position).eventEndDt);
        holder.eventLocationTV.setText(myModel.eventsList.get(position).eventLocation);
        holder.eventDescriptionTV.setText(myModel.eventsList.get(position).eventDescription);

        Log.v("position", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return myModel.eventsList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventNameTV, eventStDtTV, eventEndDtTV, eventLocationTV, eventDescriptionTV;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTV = itemView.findViewById(R.id.tvEvent);
            eventStDtTV = itemView.findViewById(R.id.tvEventStDt);
            eventEndDtTV = itemView.findViewById(R.id.tvEventEndDt);
            eventLocationTV = itemView.findViewById(R.id.tvEventLoc);
            eventDescriptionTV = itemView.findViewById(R.id.tvEventDesc);
        }
    }
}
