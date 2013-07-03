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
package de.opencelldroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.SparseArray;

/**
 * 
 * 
 * @author Dmitrij Ignatjew
 */
public class CSVParser {

	private static SparseArray<SparseArray<String>> operatorDataMap = null;

	private static void readFromStream(InputStream is) {
		try {
			operatorDataMap = new SparseArray<SparseArray<String>>();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = "";

			/** skip the first line containing MNC MCC table titles */
			reader.readLine();

			/** read from inputstream */
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(";");
				if (data[0].equals(""))
					break;
				int mcc = Integer.parseInt(data[2]);
				String operator = data[1];
				int mnc = Integer.parseInt(data[0]);
				if (operatorDataMap.get(mcc) != null) {
					SparseArray<String> tempList = operatorDataMap.get(mcc);
					tempList.put(mnc, operator);
				} else {
					SparseArray<String> tempList = new SparseArray<String>();
					tempList.put(mnc, operator);
					operatorDataMap.put(mcc, tempList);
				}
			}
		} catch (IOException ex) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	public static SparseArray<SparseArray<String>> getOperatorData(Context context) {
		
		if (operatorDataMap != null){
			return operatorDataMap;
		}
		else{
			AssetManager assetManager = context.getResources().getAssets();
			InputStream csvStream;
			try {
				csvStream = assetManager.open("mnc.csv");
				readFromStream(csvStream);
				return operatorDataMap;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
			
	}
}
