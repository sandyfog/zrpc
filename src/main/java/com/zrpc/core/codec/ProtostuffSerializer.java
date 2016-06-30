package com.zrpc.core.codec;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer {

	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

	public ProtostuffSerializer() {
	}

	private static  Schema<?> getSchema(Class<?> clazz) {
		Schema<?> schema = (Schema<?>) cachedSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(clazz);
			if (schema != null) {
				cachedSchema.put(clazz, schema);
			}
		}
		return schema;
	}

	@Override
	public  <T> byte[] serialize(T obj) throws IOException {

		Class<?> clazz = obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			@SuppressWarnings("unchecked")
			Schema<T> schema = (Schema<T>) getSchema(clazz);
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}

	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {

		try {
			T t = clazz.newInstance();
			@SuppressWarnings("unchecked")
			Schema<T> schema = (Schema<T>) getSchema(clazz);
			ProtostuffIOUtil.mergeFrom(bytes, t, schema);
			return t;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
