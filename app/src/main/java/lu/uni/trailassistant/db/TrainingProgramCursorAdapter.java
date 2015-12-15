package lu.uni.trailassistant.db;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.objects.TrainingProgram;

/**
 * Created by leandrogil on 30.11.15.
 */
public class TrainingProgramCursorAdapter extends CursorAdapter {
    public TrainingProgramCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    // this method creates (inflates) a new view and returns it, but no data will be bound yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.training_programs_list_view_item, parent, false);
    }

    // this method binds some data to a view object (f.ex. created through the newView() method of this class)
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find our TextView element defined in our custom ListView item layout
        TextView trainingProgramTextView = (TextView) view.findViewById(R.id.trainingProgramName);
        // extract training program name from cursor and populate TextView with it
        String trainingProgramNameFromCursor = cursor.getString(1);
        trainingProgramTextView.setText(trainingProgramNameFromCursor);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return Long.valueOf(cursor.getInt(0));
    }
}
