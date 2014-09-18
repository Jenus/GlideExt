package com.bumptech.glide.integration.volley;

import java.io.InputStream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

/**
 *  A simple model loader for fetching media over http/https using Volley.
 */
public class VolleyUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    /**
     * The default factory for {@link VolleyUrlLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static RequestQueue<InputStream> internalQueue;
        private RequestQueue<InputStream> requestQueue;

        private static RequestQueue<InputStream> getInternalQueue(Context context) {
            if (internalQueue == null) {
                synchronized (Factory.class) {
                    if (internalQueue == null) {
                        internalQueue = Volley.newStreamRequestQueue(context);
                    }
                }
            }
            return internalQueue;
        }

        /**
         * Constructor for a new Factory that runs requests using a static singleton request queue.
         */
        public Factory(Context context) {
            this(getInternalQueue(context));
        }

        /**
         * Constructor for a new Factory that runs requests using the given {@link RequestQueue}.
         */
        public Factory(RequestQueue<InputStream> requestQueue) {
            this.requestQueue = requestQueue;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new VolleyUrlLoader(requestQueue);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the request queue.
        }
    }

    private final RequestQueue<InputStream> requestQueue;

    public VolleyUrlLoader(RequestQueue<InputStream> requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl url, int width, int height) {
        return new VolleyStreamFetcher(requestQueue, url, new VolleyRequestFuture<InputStream>());
    }
}
