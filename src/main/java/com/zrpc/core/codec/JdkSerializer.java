package com.zrpc.core.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JdkSerializer implements Serializer  {

	@Override
	public byte[] serialize(Object obj) throws IOException {

		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		ObjectOutputStream stream = new ObjectOutputStream(bis);
		stream.writeObject(obj);
		stream.close();
		byte[] bytes = bis.toByteArray();
		return bytes;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes,Class<T> clazz) throws IOException {

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream stream = new ObjectInputStream(bis);
		try {
			return (T) stream.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("deserialize error",e);
		}
	}

}
