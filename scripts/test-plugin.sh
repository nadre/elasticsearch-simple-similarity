#!/usr/bin/env bash


sudo /usr/share/elasticsearch/bin/elasticsearch-plugin remove elasticsearch-payload-tfidf-similarity

sudo /usr/share/elasticsearch/bin/elasticsearch-plugin install file:///home/xuri3814/workspace/elasticsearch-payload-tfidf-similarity/build/distributions/elasticsearch-payload-tfidf-similarity-6.1.0.zip

sudo service elasticsearch restart

sleep 10

curl --header "Content-Type:application/json" -s -XDELETE "http://localhost:9200/test_index"

curl --header "Content-Type:application/json" -s -XPUT "http://localhost:9200/test_index" -d '
{
  "settings": {
    "index": {
      "number_of_shards": 1,
      "number_of_replicas": 0,
      "similarity": {
        "default": {
          "type": "classic"
        }
      }
    },
    "similarity": {
      "payload_tfidf_similarity": {
        "type": "payload-tfidf-similarity"
      }
    },
    "analysis": {
      "analyzer": {
        "payload_analyzer": {
          "type": "custom",
          "tokenizer": "whitespace",
          "filter": [
            "payload_filter"
          ]
        }
      },
      "filter": {
        "payload_filter": {
          "delimiter": "|",
          "encoding": "float",
          "type": "delimited_payload_filter"
        }
      }
    }
  }
}
'

curl --header "Content-Type:application/json" -XPUT 'localhost:9200/test_index/test_type/_mapping' -d '
{
  "test_type": {
    "properties": {
      "field1": {
        "type": "text"
      },
      "field2": {
        "type": "text",
        "term_vector": "with_positions_offsets_payloads",
        "analyzer": "payload_analyzer",
        "similarity": "payload_tfidf_similarity"
      }
    }
  }
}
'

curl --header "Content-Type:application/json" -s -XPUT "localhost:9200/test_index/test_type/1" -d '
{"field1" : "bar foo", "field2" : "bar foo|1000.1"}
'

curl --header "Content-Type:application/json" -s -XPUT "localhost:9200/test_index/test_type/2" -d '
{"field1" : "foo foo bar bar bar", "field2" : "foo bar|1.2 baz|3.1"}
'

curl --header "Content-Type:application/json" -s -XPUT "localhost:9200/test_index/test_type/3" -d '
{"field1" : "bar bar foo foo", "field2" : "bar|2.3 baz|3"}
'

curl --header "Content-Type:application/json" -s -XPOST "http://localhost:9200/test_index/_refresh"

echo
echo
echo 'explain highest score'

curl --header "Content-Type:application/json" -s "localhost:9200/test_index/test_type/_search?pretty=true" -d '
{
  "explain": true,
  "from": 0,
  "size": 1,
  "query": {
    "match": {
      "field2": "foo"
    }
  }
}
'

echo
echo
echo 'expecting doc 2 to have highest score'

curl --header "Content-Type:application/json" -s "localhost:9200/test_index/test_type/_search?pretty=true" -d '
{
  "explain": true,
  "query": {
    "multi_match": {
      "boost": 1.5,
      "query": "foo bar",
      "fields": [
        "field2"
      ]
    }
  }
}
'