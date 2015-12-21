package limex.timetracker.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import limex.timetracker.com.models.Tracker;
import limex.timetracker.com.timetracker.R;

/**
 * Created by limex on 9/16/15.
 */
public class TrackerAdapter extends RecyclerView.Adapter<TrackerAdapter.TrackerViewHolder> {
    private LayoutInflater inflater;
    public List<Tracker> list = Collections.emptyList();

    public TrackerAdapter(Context context, List<Tracker> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public TrackerViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.tracker_list, parent, false);
        TrackerViewHolder holder = new TrackerViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(TrackerViewHolder holder, int position) {
        Tracker tracker = list.get(position);
        String out = "-";

        if (tracker.out != null) {
            out = tracker.out;
        }
        holder.txtDate.setText(tracker.date);
        holder.txtIn.setText(tracker.in);
        holder.txtOut.setText(out);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TrackerViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        TextView txtIn;
        TextView txtOut;

        public TrackerViewHolder(View itemView) {
            super(itemView);

            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtIn = (TextView) itemView.findViewById(R.id.txt_login);
            txtOut = (TextView) itemView.findViewById(R.id.txt_logout);
        }
    }
}
