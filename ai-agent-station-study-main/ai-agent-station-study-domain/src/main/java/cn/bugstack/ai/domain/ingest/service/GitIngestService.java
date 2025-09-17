package cn.bugstack.ai.domain.ingest.service;

import cn.bugstack.ai.domain.ingest.model.IngestResult;

import java.util.List;

public interface GitIngestService {

    IngestResult ingest(String repoUrl,
                        String branch,
                        List<String> parseOptions,
                        String knowledgeName,
                        String ragId,
                        String baseDir);
}
