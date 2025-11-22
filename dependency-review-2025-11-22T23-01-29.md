# Maven Dependency Review - 2025-11-22T23-01-29

## Dependencies to Update

| Dependency | Current Version | Available Version | Summary |
|------------|-----------------|-------------------|---------|
| co.elastic.clients:elasticsearch-java | 8.18.8 | 8.19.7 | **Release Notes:** https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.0 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.1 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.2 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.3 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.4 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.5 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.6 • https://github.com/elastic/elasticsearch-java/releases/tag/v8.19.7<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:** 8.19.0: Field type changes in PinnedRetriever/LinearRetriever (rankWindowSize: int→Integer), removed incorrect fields (SubmitRequest.minCompatibleShardNode, AliasesRequest.masterTimeout), DeleteAliasResponse parent class changed, PipelineSettings deprecated field replacements<br><br>**Major Features:** BulkIngester retry policy for 429 responses with configurable backoff<br><br>**Notes:** 8 versions behind. Review code for retriever/pipeline settings usage. Patch releases contain API spec updates and bug fixes. |
| com.couchbase.client:java-client | 3.8.3 | 3.10.0 | **Release Notes:** https://docs.couchbase.com/java-sdk/current/project-docs/sdk-release-notes.html<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:** 3.9.0: Mono-versioning adopted (version alignment, no API breaks). More aggressive dead connection detection (closes after 5min idle, NOOP at 2.5min)<br><br>**Major Features:** 3.10.0: mTLS cert refresh without restart, JwtAuthenticator. 3.9.0: BOM for JVM clients, FTS vector prefilter<br><br>**Notes:** Jackson 2.17.3→2.20.1, Netty 4.1.x updates. Compatible with SDK API 3.8. |

## Verification Status
- Total dependencies: 47
- Research in progress...
