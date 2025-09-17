package cn.bugstack.ai.domain.ingest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestResult {
    private String repoUrl;
    private String branch;
    private String clonedPath;
    private int fileCount;
    private int parsedFiles;
    private int documents;
    private int chunks;
    private String knowledge;
    private String ragId;
}
