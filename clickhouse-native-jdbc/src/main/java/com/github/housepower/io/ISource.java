/*
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
 * limitations under the License.
 */

package com.github.housepower.io;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public interface ISource extends AutoCloseable {

    void skipBytes(int len);

    boolean readBoolean();

    byte readByte();

    short readShortLE();

    int readIntLE();

    long readLongLE();

    long readVarInt();

    float readFloatLE();

    double readDoubleLE();

    ByteBuf readSlice(int len);

    ByteBuf readRetainedSlice(int len);

    CharSequence readCharSequence(int len, Charset charset);

    ByteBuf readSliceBinary();

    CharSequence readCharSequenceBinary(Charset charset);

    String readUTF8Binary();

    // explicitly overwrite to suppress Exception
    @Override
    void close();
}