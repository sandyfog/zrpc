package com.zrpc.core.codec;

import java.io.IOException;

public interface Serializer {

	<T> byte[] serialize(T obj) throws IOException;

	<T>  T deserialize(byte[] bytes,Class<T> clazz) throws IOException;
}
