package com.bumptech.glide.load.engine;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

import java.io.File;

interface ResourceRunnerFactory {

    public <T, Z, R> ResourceRunner<Z, R> build(EngineKey key, int width, int height,
            ResourceDecoder<File, Z> cacheDecoder, DataFetcher<T> fetcher,  Encoder<T> sourceEncoder,
            ResourceDecoder<T, Z> decoder, Transformation<Z> transformation, ResourceEncoder<Z> encoder,
            ResourceTranscoder<Z, R> transcoder, Priority priority, boolean isMemoryCacheable,
            DiskCacheStrategy diskCacheStrategy, EngineJobListener listener);
}
