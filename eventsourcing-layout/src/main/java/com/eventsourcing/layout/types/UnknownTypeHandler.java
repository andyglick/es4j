/**
 * Copyright 2016 Eventsourcing team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package com.eventsourcing.layout.types;

import com.eventsourcing.layout.Deserializer;
import com.eventsourcing.layout.Layout;
import com.eventsourcing.layout.Serializer;
import com.eventsourcing.layout.TypeHandler;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;

public class UnknownTypeHandler implements TypeHandler {

    private final Class klass;
    private final Layout layout;
    private final Serializer serializer;
    private final Deserializer deserializer;

    @SneakyThrows @SuppressWarnings("unchecked")
    public UnknownTypeHandler(Class klass) {
        this.klass = klass;
        layout = new Layout<>(klass);
        serializer = new Serializer<>(layout);
        deserializer = new Deserializer<>(layout);
    }

    @Override
    public byte[] getFingerprint() {
        return "Unknown".getBytes();
    }

    @Override @SneakyThrows
    @SuppressWarnings("unchecked")
    public int size(Object value) {
        if (value == null) {
            return size(klass.newInstance());
        }
        return serializer.size(value);
    }

    @Override @SneakyThrows @SuppressWarnings("unchecked")
    public void serialize(Object value, ByteBuffer buffer) {
        if (value != null) {
            serializer.serialize(value, buffer);
        } else {
            serialize(klass.newInstance(), buffer);
        }
    }

    @Override
    public Object deserialize(ByteBuffer buffer) {
        return deserializer.deserialize(buffer);
    }
}
