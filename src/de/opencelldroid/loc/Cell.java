package de.opencelldroid.loc;

/**
 * Data object that holds the relevant attributes of a {@link CellLocation}
 * TODO: JavaDoc
 * @author info@leoliebig.de
 */
public class Cell {
	
	private int mcc = -1;
	private int mnc = -1;
	private int lac = -1;
	private int cellId = -1;
	private double lat = -1d;
	private double lon = -1d;
	
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

	public final double getLat() {
		return lat;
	}

	public final void setLat(double lat) {
		this.lat = lat;
	}

	public final double getLon() {
		return lon;
	}

	public final void setLon(double lon) {
		this.lon = lon;
	}
	
	@Override
	public String toString() {
		return new String("MCC:MNC:LAC:CID " + mcc + ":" + mnc + ":" + lac + ":" + cellId
				+ " | " + "LAT:LONG " + lat + ":" + lon);
	}

}
