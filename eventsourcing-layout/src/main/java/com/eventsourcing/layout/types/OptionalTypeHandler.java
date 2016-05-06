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

import com.eventsourcing.layout.TypeHandler;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.primitives.Bytes;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.Optional;

public class OptionalTypeHandler implements TypeHandler<Optional> {
    private final TypeHandler handler;

    public OptionalTypeHandler(AnnotatedType annotatedType) throws TypeHandlerException {
        if (!(annotatedType instanceof AnnotatedParameterizedType)) {
            throw new IllegalArgumentException("List type parameter should be specified");
        }
        AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) annotatedType;
        AnnotatedType arg = parameterizedType.getAnnotatedActualTypeArguments()[0];
        Class<?> klass;
        if (arg.getType() instanceof ParameterizedType) {
            klass = (Class<?>)((ParameterizedType)(arg.getType())).getRawType();
        } else {
            klass = (Class<?>) arg.getType();
        }
        ResolvedType resolvedType = new TypeResolver().resolve(klass);
        handler = TypeHandler.lookup(resolvedType, arg);
    }

    @Override
    public byte[] getFingerprint() {
        return Bytes.concat("Optional[".getBytes(), handler.getFingerprint(), "]".getBytes());
    }

    @Override
    public Optional deserialize(ByteBuffer buffer) {
        if (buffer.get() == 0) {
            return Optional.empty();
        }
        return Optional.of(handler.deserialize(buffer));
    }

    @Override @SuppressWarnings("unchecked")
    public int size(Optional value) {
        if (value == null) {
            return 1;
        }
        return value.isPresent() ? handler.size(value.get()) + 1 : 1;
    }

    @Override @SuppressWarnings("unchecked")
    public void serialize(Optional value, ByteBuffer buffer) {
        if (value == null) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) (value.isPresent() ? 1 : 0));
            if (value.isPresent()) {
                handler.serialize(value.get(), buffer);
            }
        }
    }

    @Override @SuppressWarnings("unchecked")
    public int comparableSize(Optional value) {
        if (value == null) {
            return 0;
        }
        return value.isPresent() ? handler.comparableSize(value.get()) : 0;
    }

    @Override @SuppressWarnings("unchecked")
    public void serializeComparable(Optional value, ByteBuffer buffer) {
        if (value == null) {
        } else {
            if (value.isPresent()) {
                handler.serializeComparable(value.get(), buffer);
            }
        }
    }
}
