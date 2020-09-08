/*
 * Copyright 2020 Lenses.io
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
 * limitations under the License.
 */

package com.landoop.streamreactor.connect.hive.source.offset

import com.landoop.streamreactor.connect.hive.source
import com.landoop.streamreactor.connect.hive.source.{SourceOffset, SourcePartition}

class HiveSourceRefreshOffsetStorageReader(originalOffsets: Map[SourcePartition, SourceOffset], contextReader: HiveOffsetStorageReader) extends HiveOffsetStorageReader {

  override def offset(partition: source.SourcePartition): Option[source.SourceOffset] = {
    originalOffsets.get(partition)
      .fold(
        contextReader.offset(partition)
      ) { eo =>
        val nextStartOffset = eo.rowNumber + 1
        // the source wants the first offset to read, not the last offset encountered, therefore we add 1 here
        Some(SourceOffset(nextStartOffset))
      }
  }

}