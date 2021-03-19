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

package com.github.housepower.data;

import com.github.housepower.io.ColumnWriterBuffer;
import com.github.housepower.jdbc.ClickHouseArray;
import com.github.housepower.data.type.complex.DataTypeArray;
import com.github.housepower.io.CompositeSink;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnArray extends AbstractColumn {

    private final List<Long> offsets;
    // data represents nested column in ColumnArray
    private final IColumn data;

    public ColumnArray(String name, DataTypeArray type, Object[] values) {
        super(name, type, values);
        offsets = new ArrayList<>();
        data = ColumnFactory.createColumn(null, type.getElemDataType(), null);
    }

    @Override
    public void write(Object object) throws IOException, SQLException {
        Object[] arr = ((ClickHouseArray) object).getArray();

        offsets.add(offsets.isEmpty() ? arr.length : offsets.get((offsets.size() - 1)) + arr.length);
        for (Object field : arr) {
            data.write(field);
        }
    }

    @Override
    public void flush(CompositeSink sink, boolean immediate) throws SQLException, IOException {
        if (isExported()) {
            sink.writeUTF8Binary(name);
            sink.writeUTF8Binary(type.name());
        }

        flushOffsets(sink);
        data.flush(sink, false);

        if (immediate) {
            buffer.writeTo(sink);
        }
    }

    public void flushOffsets(CompositeSink sink) {
        for (long offsetList : offsets) {
            sink.writeLongLE(offsetList);
        }
    }

    @Override
    public void setColumnWriterBuffer(ColumnWriterBuffer buffer) {
        super.setColumnWriterBuffer(buffer);
        data.setColumnWriterBuffer(buffer);
    }

    @Override
    public void clear() {
        offsets.clear();
        data.clear();
    }
}
