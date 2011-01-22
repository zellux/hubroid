/**
 * Hubroid - A GitHub app for Android
 * 
 * Copyright (c) 2011 Idlesoft LLC.
 * 
 * Licensed under the New BSD License.
 */

package net.idlesoft.android.apps.github.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.idlesoft.android.apps.github.R;
import net.idlesoft.android.apps.github.activities.Hubroid;
import net.idlesoft.android.apps.github.utils.GravatarCache;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class CommitListAdapter extends BaseAdapter {
    public static class ViewHolder {
        public TextView commit_date;

        public TextView commit_shortdesc;

        public ImageView gravatar;
    }

    private final Context m_context;

    private JSONArray m_data = new JSONArray();

    private final HashMap<String, Bitmap> m_gravatars;

    private final LayoutInflater m_inflater;

    public CommitListAdapter(final Context context, final JSONArray jsonarray) {
        m_context = context;
        m_inflater = LayoutInflater.from(m_context);
        m_data = jsonarray;
        m_gravatars = new HashMap<String, Bitmap>(m_data.length());

        loadGravatars();
    }

    public int getCount() {
        return m_data.length();
    }

    public Object getItem(final int i) {
        try {
            return m_data.get(i);
        } catch (final JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getItemId(final int i) {
        return i;
    }

    public View getView(final int index, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = m_inflater.inflate(R.layout.commit_list_item, null);
            holder = new ViewHolder();
            holder.commit_date = (TextView) convertView
                    .findViewById(R.id.tv_commit_list_item_commit_date);
            holder.commit_shortdesc = (TextView) convertView
                    .findViewById(R.id.tv_commit_list_item_shortdesc);
            holder.gravatar = (ImageView) convertView
                    .findViewById(R.id.iv_commit_list_item_gravatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            String end;
            final SimpleDateFormat dateFormat = new SimpleDateFormat(Hubroid.GITHUB_TIME_FORMAT);
            final Date commit_time = dateFormat.parse(m_data.getJSONObject(index).getString(
                    "committed_date"));
            final Date current_time = dateFormat.parse(dateFormat.format(new Date()));
            final long ms = current_time.getTime() - commit_time.getTime();
            final long sec = ms / 1000;
            final long min = sec / 60;
            final long hour = min / 60;
            final long day = hour / 24;
            if (day > 0) {
                if (day == 1) {
                    end = " day ago";
                } else {
                    end = " days ago";
                }
                holder.commit_date.setText(day + end);
            } else if (hour > 0) {
                if (hour == 1) {
                    end = " hour ago";
                } else {
                    end = " hours ago";
                }
                holder.commit_date.setText(hour + end);
            } else if (min > 0) {
                if (min == 1) {
                    end = " minute ago";
                } else {
                    end = " minutes ago";
                }
                holder.commit_date.setText(min + end);
            } else {
                if (sec == 1) {
                    end = " second ago";
                } else {
                    end = " seconds ago";
                }
                holder.commit_date.setText(sec + end);
            }
            holder.gravatar.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.gravatar_border));
            holder.gravatar.setImageBitmap(m_gravatars.get(m_data.getJSONObject(index)
                    .getJSONObject("author").getString("login")));
            holder.commit_shortdesc.setText(m_data.getJSONObject(index).getString("message").split(
                    "\n")[0]);
        } catch (final JSONException e) {
            e.printStackTrace();
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * Get the Gravatars of all users in the commit log
     */
    public void loadGravatars() {
        final int length = m_data.length();
        for (int i = 0; i < length; i++) {
            try {
                final String login = m_data.getJSONObject(i).getJSONObject("author").getString(
                        "login");
                if (!m_gravatars.containsKey(login)) {
                    m_gravatars.put(login, GravatarCache.getDipGravatar(GravatarCache
                            .getGravatarID(login), 30.0f, m_context.getResources()
                            .getDisplayMetrics().density));
                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
