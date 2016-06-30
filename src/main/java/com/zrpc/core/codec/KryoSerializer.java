package com.zrpc.core.codec;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.zrpc.core.RpcRequest;
import com.zrpc.core.RpcResponse;

public class KryoSerializer  implements Serializer  {
	
	// singleton
	private static final KryoSerializer INSTNACE = new KryoSerializer();
	public static KryoSerializer getInstance() { return INSTNACE; } 
	private KryoSerializer() {}
	
	
	// thread local cache
    private static final ThreadLocal<SoftReference<Kryo>> CACHE = new ThreadLocal<SoftReference<Kryo>>() {
    	@Override
    	protected SoftReference<Kryo> initialValue() {
            Kryo kryo = newKryo();
            return new SoftReference<Kryo>(kryo);
        }
    };
    
    private static Kryo newKryo() {
    	Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        return kryo;
    }
	
	
	@Override
	public byte[] serialize(Object rb) {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    Output output = new Output(baos);
		    kryo().writeObject(output, rb);
		    output.close();
		    return baos.toByteArray();
	}
	
	private Kryo kryo() {
		Kryo kryo = CACHE.get().get();
		if (kryo == null) {
			kryo = newKryo();
			CACHE.set(new SoftReference<Kryo>(kryo));
		}
		return kryo;
	}

	@Override
	public <T> T deserialize(byte[] bytes,Class<T> clazz) {
		
		    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		    Input input = new Input(bais);
		    T rb = kryo().readObject(input,clazz);
	        input.close();
		    return  rb;
	
	}

}
