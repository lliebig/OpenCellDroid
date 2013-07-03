/*
 * Copyright 2013 Leo Liebig (lliebig, info@leoliebig.de), Dmitrij Ignatjew, Jose Martinez Gonzalez (Tunnel1337)
 * This file is part of OpenCellDroid.
 * 
 * OpenCellDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenCellDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenCellDroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opencelldroid.loc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data object that holds the relevant attributes of a {@link CellLocation}
 * TODO: JavaDoc
 * @author info@leoliebig.de
 */
public class Cell implements Parcelable{
	
	private int mcc = -1;
	private int mnc = -1;
	private int lac = -1;
	private int cellId = -1;
	private float lat = -1f;
	private float lon = -1f;
	
	/**
	 * Creates a data object that holds the relevant attributes of a {@link CellLocation}
	 * @param mcc
	 * @param mnc
	 * @param lac
	 * @param cellId
	 * @param lat
	 * @param lon
	 */
	public Cell(int mcc, int mnc, int lac, int cellId, float lat, float lon) {
		this.mcc = mcc;
		this.mnc = mnc;
		this.lac = lac;
		this.cellId = cellId;
		this.lat = lat;
		this.lon = lon;
	}
	
	/**
	 * Creates a data object that holds the relevant attributes of a {@link CellLocation}
	 * @param mcc
	 * @param mnc
	 * @param lac
	 * @param cellId
	 */
	public Cell(int mcc, int mnc, int lac, int cellId) {
		this.mcc = mcc;
		this.mnc = mnc;
		this.lac = lac;
		this.cellId = cellId;
	}
	
	public final int getMcc() {
		return mcc;
	}

	public final void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public final int getMnc() {
		return mnc;
	}

	public final void setMnc(int mnc) {
		this.mnc = mnc;
	}

	public final int getLac() {
		return lac;
	}

	public final void setLac(int lac) {
		this.lac = lac;
	}

	public final int getCellId() {
		return cellId;
	}

	public final void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public final float getLat() {
		return lat;
	}

	public final void setLat(float lat) {
		this.lat = lat;
	}

	public final float getLon() {
		return lon;
	}

	public final void setLon(float lon) {
		this.lon = lon;
	}
	
	@Override
	public String toString() {
		return new String(mcc + ":" + mnc + ":" + lac + ":" + cellId + " (MCC:MNC:LAC:CID) | " + lat + ":" + lon + " (LAT:LONG)");
	}
	
	public String getCellIdString(){
		return new String(mcc + ":" + mnc + ":" + lac + ":" + cellId);
	}
	
	@Override
	public int describeContents() {
		//
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mcc);
		dest.writeInt(mnc);
		dest.writeInt(lac);
		dest.writeInt(cellId);
		dest.writeFloat(lat);
		dest.writeFloat(lon);
	}

	public Cell(Parcel in) {
		mcc = in.readInt();
		mnc = in.readInt();
		lac = in.readInt();
		cellId = in.readInt();
		lat = in.readFloat();
		lon = in.readFloat();
	}

	public static final Parcelable.Creator<Cell> CREATOR = new Parcelable.Creator<Cell>() {

		public Cell createFromParcel(Parcel in) {
			return new Cell(in);
		}

		public Cell[] newArray(int size) {
			return new Cell[size];
		}
	};
}
