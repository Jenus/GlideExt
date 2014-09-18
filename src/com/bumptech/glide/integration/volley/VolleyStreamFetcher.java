package com.bumptech.glide.integration.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

/**
 * A DataFetcher backed by volley for fetching images via http.
 */
public class VolleyStreamFetcher implements DataFetcher<InputStream> {
	private final RequestQueue<InputStream> requestQueue;
	private final GlideUrl url;
	private VolleyRequestFuture<InputStream> requestFuture;

	public VolleyStreamFetcher(RequestQueue<InputStream> requestQueue,
			GlideUrl url) {
		this(requestQueue, url, null);
	}

	public VolleyStreamFetcher(RequestQueue<InputStream> requestQueue,
			GlideUrl url, VolleyRequestFuture<InputStream> requestFuture) {
		this.requestQueue = requestQueue;
		this.url = url;
		this.requestFuture = requestFuture;
		if (requestFuture == null) {
			this.requestFuture = VolleyRequestFuture.newFuture();
		}
	}

	@Override
	public InputStream loadData(Priority priority) throws Exception {
		// Make sure the string url safely encodes non ascii characters.
		String stringUrl = url.toURL().toString();
		GlideRequest request = new GlideRequest(stringUrl, requestFuture,
				glideToVolleyPriority(priority));

		requestFuture.setRequest(requestQueue.add(request));

		return requestFuture.get();
	}

	@Override
	public void cleanup() {
		// Do nothing.
	}

	@Override
	public String getId() {
		return url.toString();
	}

	@Override
	public void cancel() {
		VolleyRequestFuture<InputStream> localFuture = requestFuture;
		if (localFuture != null) {
			localFuture.cancel(true);
		}
	}

	private static Request.Priority glideToVolleyPriority(Priority priority) {
		switch (priority) {
		case LOW:
			return Request.Priority.LOW;
		case HIGH:
			return Request.Priority.HIGH;
		case IMMEDIATE:
			return Request.Priority.IMMEDIATE;
		default:
			return Request.Priority.NORMAL;

		}
	}

	private static class GlideRequest extends Request<InputStream, InputStream> {
		private final VolleyRequestFuture<InputStream> future;
		private Priority priority;

		public GlideRequest(String url,
				VolleyRequestFuture<InputStream> future, Priority priority) {
			super(Method.GET, url, future);

			this.future = future;
			this.priority = priority;

			// use myself
			// this.setShouldCache(false);
		}

		@Override
		public Priority getPriority() {
			return priority;
		}

		@Override
		protected Response<InputStream, InputStream> parseNetworkResponse(
				NetworkResponse<InputStream> response) {
			return Response.success(response.data,
					HttpHeaderParser.parseCacheHeaders(response));
		}

		@Override
		protected void deliverResponse(InputStream response) {
			future.onResponse(response);
		}
	}
}
