package com.tezewike.er;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Tobe on 5/19/2016.
 */
public class MainFragment extends Fragment {

    /**
    private OnOptionSelectedListener itemListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String[] options = new String[] {"Movies", "Steam", "YouTube"};
    int[] drawables = new int[] {R.drawable.movies, R.drawable.steam, R.drawable.youtube};

    // Container Activity must implement this interface
    public interface OnOptionSelectedListener {
        public void onOptionSelected(int selection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MainAdapter(getActivity(),options, drawables);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemListener = (OnOptionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOptionSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemListener = null;
    }

    class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
        private String[] mOptions;
        private int[] mDrawables;
        private Context mContext;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {
            // each data item is just a string in this case

            public ImageView imageView;
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.main_option_image);
                imageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getPosition();
                if (itemListener != null) {
                    itemListener.onOptionSelected(position);
                }
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MainAdapter(Context c, String[] options, int[] drawables) {
            this.mContext = c;
            this.mOptions = options;
            this.mDrawables = drawables;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder viewHolder = new ViewHolder(convertView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Picasso.with(mContext).load(mDrawables[position]).into(holder.imageView);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDrawables.length;
        }

    }
     **/
}

