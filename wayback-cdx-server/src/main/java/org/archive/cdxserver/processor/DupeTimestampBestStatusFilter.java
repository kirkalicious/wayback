package org.archive.cdxserver.processor;

import org.apache.commons.lang.math.NumberUtils;
import org.archive.format.cdx.CDXLine;



public class DupeTimestampBestStatusFilter extends WrappedProcessor {
	final static int WORST_HTTP_CODE = 9999;
	
	protected String lastTimestamp;

	protected int bestHttpCode = WORST_HTTP_CODE;
	protected int timestampDedupLength;
	
	public DupeTimestampBestStatusFilter(BaseProcessor output, int timestampDedupLength)
	{
		super(output);
		this.timestampDedupLength = timestampDedupLength;
	}
	
	@Override
	public int writeLine(CDXLine line)
	{
		if (include(line)) {
			return super.writeLine(line);
		} else {
			return 0;
		}
	}
	
	public boolean include(CDXLine line) {
		
		if (timestampDedupLength <= 0) {
			return true;
		}
		
		String timestamp = line.getTimestamp();
		timestamp = timestamp.substring(0, Math.min(timestampDedupLength, timestamp.length()));
		int httpCode = NumberUtils.toInt(line.getStatusCode(), WORST_HTTP_CODE);
		
		boolean isDupe = false;
		
		if ((lastTimestamp != null) && timestamp.equals(lastTimestamp)) {
			if (httpCode < bestHttpCode) {
				bestHttpCode = httpCode;
			} else {
				isDupe = true;
			}
		} else {
			bestHttpCode = httpCode;
		}
		
		lastTimestamp = timestamp;
		
		return !isDupe;
	}
}
