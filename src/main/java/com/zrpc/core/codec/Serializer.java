package com.zrpc.core.codec;

import java.io.IOException;

public interface Serializer {

	byte[] serialize(Object obj) throws IOException;

	<T>  T deserialize(byte[] bytes,Class<T> clazz) throws IOException;
}
