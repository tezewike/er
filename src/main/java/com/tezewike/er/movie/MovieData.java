package com.tezewike.er.movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tobe on 5/18/2016.
 */

public class MovieData implements Parcelable {
    public String title;
    public String poster;
    public String backdrop;
    public String description;
    public String release;
    public String vote;

    public MovieData(String t, String p, String b, String d, String r, String v) {
        this.title = t;
        this.poster = p;
        this.backdrop = b;
        this.description = d;
        this.release = dateFormatter(r);
        this.vote = v;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(poster);
        out.writeString(backdrop);
        out.writeString(description);
    }

    private MovieData(Parcel in) {
        title = in.readString();
        poster = in.readString();
        backdrop = in.readString();
        description = in.readString();
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    private String dateFormatter(String d) {
        String months[] =  {"", "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        String[] date;
        int month, day, year;
        if (!d.equals("null")) {
            date = d.split("-");
            try {
                year = Integer.parseInt(date[0]);
                month = Integer.parseInt(date[1]);
                day = Integer.parseInt(date[2]);
            } catch (NumberFormatException e) {

                return null;
            }
        } else {
            return null;
        }

        return months[month] + " " + day + ", " + year;
    }

}
