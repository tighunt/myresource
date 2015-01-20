
package com.rtk.tv.data;

import android.os.Parcel;
import android.os.Parcelable;

public final class TimeshiftStatus implements Parcelable {

    public static final Parcelable.Creator<TimeshiftStatus> CREATOR = new Parcelable.Creator<TimeshiftStatus>() {
        public TimeshiftStatus createFromParcel(Parcel in) {
            TimeshiftStatus parcel = new TimeshiftStatus();
            parcel.readFromParcel(in);
            return parcel;
        }

        public TimeshiftStatus[] newArray(int size) {
            return new TimeshiftStatus[size];
        }
    };

    public int stat;

    public int startHour;
    public int startMinute;
    public int startSecond;

    public int playHour;
    public int playMinute;
    public int playSecond;

    public int recordHour;
    public int recordMinute;
    public int recordSecond;
    
    public float rate;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(stat);

        out.writeInt(startHour);
        out.writeInt(startMinute);
        out.writeInt(startSecond);

        out.writeInt(playHour);
        out.writeInt(playMinute);
        out.writeInt(playSecond);

        out.writeInt(recordHour);
        out.writeInt(recordMinute);
        out.writeInt(recordSecond);

        out.writeFloat(rate);
    }

    public void readFromParcel(Parcel in) {
        stat = in.readInt();

        startHour = in.readInt();
        startMinute = in.readInt();
        startSecond = in.readInt();

        playHour = in.readInt();
        playMinute = in.readInt();
        playSecond = in.readInt();

        recordHour = in.readInt();
        recordMinute = in.readInt();
        recordSecond = in.readInt();

        rate = in.readFloat();
    }

    public TimeshiftStatus() {

    }

    public TimeshiftStatus(Parcel in) {
        readFromParcel(in);
    }

    public int getPlayTimeSeconds() {
        return playHour * 3600 + playMinute * 60 + playSecond;
    }

    public int getStartTimeSeconds() {
        return startHour * 3600 + startMinute * 60 + startSecond;
    }

    public int getRecordTimeSeconds() {
        return recordHour * 3600 + recordMinute * 60 + recordSecond;
    }

    public boolean isTimeshifting() {
        /*return stat != TvManager.STAT_TIMESHIFT_DISABLED &&
               stat != TvManager.STAT_TIMESHIFT_STOP &&
               stat != TvManager.STAT_TIMESHIFT_UNKNOWN;*/
    	return false;
    }

    @Override
    public String toString() {
        return String.format(
                "Timeshift: {stat = %d, start = %02d:%02d:%02d, play = %02d:%02d:%02d, record = %02d:%02d:%02d, rate = %f}",
                stat, startHour, startMinute,startSecond,
                playHour, playMinute, playSecond,
                recordHour, recordMinute, recordSecond,
                rate);
    }

}
