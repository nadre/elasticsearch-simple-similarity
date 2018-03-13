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

package org.elasticsearch.plugin;

import org.elasticsearch.index.IndexModule;
import org.elasticsearch.index.similarity.PayloadTFIDFSimilarityProvider;
import org.elasticsearch.plugins.Plugin;

public class PayloadTFIDFSimilarityPlugin extends Plugin {
    public String name() {
        return "elasticsearch-payload-tfidf-similarity";
    }

    public String description() {
        return "Elasticsearch plugin that ignores the term frequency but uses the payload instead";
    }

    public void onIndexModule(IndexModule indexModule) {
        indexModule.addSimilarity("payload-tfidf-similarity", PayloadTFIDFSimilarityProvider::new);
    }
}
