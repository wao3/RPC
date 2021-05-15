package cn.wao3.rpc.serialize;

public interface Serializer {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
