/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Carlos Andres Jimenez <apps@carlosandresjimenez.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package co.carlosandresjimenez.android.gotit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.User;

/**
 * Created by carlosjimenez on 10/30/15.
 */
public class FollowRequestAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> users;

    Listener mListener;

    public interface Listener {
        void onAcceptRequested(String userEmail);

        void onRejectRequested(String userEmail);
    }

    public FollowRequestAdapter(Context context, Listener listener, ArrayList<User> users) {
        super(context, -1, users);
        this.context = context;
        this.mListener = listener;
        this.users = users;
    }

    public void clear() {
        users.clear();
        super.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_folowrequest, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        final User user = users.get(position);
        if (user != null) {
            viewHolder.tvName.setText(user.getName() + " " + user.getLastName());
            viewHolder.tvEmail.setText(user.getEmail());

            viewHolder.ivAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAcceptRequested(user.getEmail());
                }
            });

            viewHolder.ivReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRejectRequested(user.getEmail());
                }
            });
        }

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView tvName;
        public final TextView tvEmail;
        public final ImageView ivAccept;
        public final ImageView ivReject;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.user_name);
            tvEmail = (TextView) view.findViewById(R.id.user_email);
            ivAccept = (ImageView) view.findViewById(R.id.accept_request);
            ivReject = (ImageView) view.findViewById(R.id.reject_request);
        }
    }

}