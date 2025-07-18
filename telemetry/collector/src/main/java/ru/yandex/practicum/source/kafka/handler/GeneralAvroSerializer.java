package ru.yandex.practicum.source.kafka.handler;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class GeneralAvroSerializer<T extends GenericRecord> implements Serializer<T> {

    private static final byte MAGIC_BYTE = 0;
    private SchemaRegistryClient schemaRegistryClient;
    private String schemaRegistryUrl;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Object url = configs.get("schema.registry.url");
        if (url instanceof String) {
            this.schemaRegistryUrl = (String) url;
        } else {
            throw new IllegalArgumentException("Missing schema.registry.url in serializer configs");
        }
        this.schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryUrl, 1000);
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Schema schema = data.getSchema();

            int schemaId = schemaRegistryClient.register(topic + "-value", schema);

            out.write(MAGIC_BYTE);

            out.write(ByteBuffer.allocate(4).putInt(schemaId).array());

            DatumWriter<T> writer = new SpecificDatumWriter<>(schema);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(data, encoder);
            encoder.flush();

            return out.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Error serializing Avro message", e);
        } catch (Exception e) {
            throw new SerializationException("Error registering schema", e);
        }
    }
}
