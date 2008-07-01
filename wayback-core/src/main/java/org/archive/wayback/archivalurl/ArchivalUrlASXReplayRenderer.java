package org.archive.wayback.archivalurl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archive.wayback.ResultURIConverter;
import org.archive.wayback.core.Resource;
import org.archive.wayback.core.CaptureSearchResult;
import org.archive.wayback.core.CaptureSearchResults;
import org.archive.wayback.core.WaybackRequest;
import org.archive.wayback.exception.BadContentException;
import org.archive.wayback.replay.HTMLPage;
import org.archive.wayback.replay.HttpHeaderOperation;

public class ArchivalUrlASXReplayRenderer extends ArchivalUrlReplayRenderer {
	/* (non-Javadoc)
	 * @see org.archive.wayback.ReplayRenderer#renderResource(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.archive.wayback.core.WaybackRequest, org.archive.wayback.core.SearchResult, org.archive.wayback.core.Resource, org.archive.wayback.ResultURIConverter, org.archive.wayback.core.SearchResults)
	 */
	public void renderResource(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, WaybackRequest wbRequest,
			CaptureSearchResult result, Resource resource,
			ResultURIConverter uriConverter, CaptureSearchResults results)
			throws ServletException, IOException, BadContentException {

		
		HttpHeaderOperation.copyHTTPMessageHeader(resource, httpResponse);

		Map<String,String> headers = HttpHeaderOperation.processHeaders(
				resource, result, uriConverter, this);
	
		// Load content into an HTML page, and resolve embedded HREF urls:
		HTMLPage page = new HTMLPage(resource,result,uriConverter);
		page.readFully();

		page.resolveASXRefUrls();

		// set the corrected length:
		int bytes = page.getBytes().length;
		headers.put(HTTP_LENGTH_HEADER, String.valueOf(bytes));

		// send back the headers:
		HttpHeaderOperation.sendHeaders(headers, httpResponse);

		page.writeToOutputStream(httpResponse.getOutputStream());
	}
}
