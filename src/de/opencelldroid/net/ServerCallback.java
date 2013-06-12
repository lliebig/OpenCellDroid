package de.opencelldroid.net;

import java.util.List;

import de.opencelldroid.loc.Cell;
import de.opencelldroid.net.ServerRequest.ResponseCode;

/**
 * Implements callback methods
 * 
 * @author info@leoliebig.de, Jose Martinez Gonzalez (Tunnel1337)
 */
public interface ServerCallback {
	
	public void addCellCallback(ResponseCode code);
	public void getInAreaCallback(ResponseCode code, List<Cell> cells);
	
}
