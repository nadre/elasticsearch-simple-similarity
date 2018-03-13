/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticsearch.index.similarity;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.logging.Loggers;


public class PayloadTFIDFSimilarity extends TFIDFSimilarity {

    public PayloadTFIDFSimilarity() {}

    @Override
    public float lengthNorm(int numTerms) {
        Loggers.getLogger(this.getClass().toString()).info("LENGTH-NORM");
        return 1f;
    }

    @Override
    public float tf(float freq) {
        Loggers.getLogger(this.getClass().toString()).info("SCORE-TF");
        return 1f;
    }

    @Override
    public float sloppyFreq(int distance) {
        return 1f;
    }

    public float scorePayload(int doc, int start, int end, BytesRef payload) {
        Loggers.getLogger(this.getClass().toString()).info("SCORE-PAYLOAD-OUT");
        if (payload != null) {
            Loggers.getLogger(this.getClass().toString()).info("SCORE-PAYLOAD-IN");
            return PayloadHelper.decodeFloat(payload.bytes, payload.offset);
        } else {
            return 1;
        }
    }

    @Override
    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats) {
        final long df = termStats.docFreq();
        final long docCount = collectionStats.docCount() == -1 ? collectionStats.maxDoc() : collectionStats.docCount();
        final float idf = idf(df, docCount);
        return Explanation.match(idf, "idf, computed as log((docCount+1)/(docFreq+1)) + 1 from:",
                Explanation.match(df, "docFreq"),
                Explanation.match(docCount, "docCount"));
    }

    @Override
    public float idf(long docFreq, long docCount) {
        return (float)(Math.log((docCount+1)/(double)(docFreq+1)) + 1.0);
    }

    @Override
    public String toString() {
        return "PayloadTFIDFSimilarity";
    }

}
