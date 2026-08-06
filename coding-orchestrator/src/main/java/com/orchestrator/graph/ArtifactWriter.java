package com.orchestrator.graph;

import com.orchestrator.tools.FileSystemTool;
import com.orchestrator.tools.ToolResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses agent output containing {@code === FILE: path ===} blocks and materializes each block to
 * the workspace through the deterministic {@link FileSystemTool}. When no block markers are present,
 * the whole payload is written to {@code fallbackPath} so nothing is lost.
 */
final class ArtifactWriter {

    private static final Pattern FILE_BLOCK =
            Pattern.compile("===\\s*FILE:\\s*(.+?)\\s*===\\r?\\n", Pattern.CASE_INSENSITIVE);

    private ArtifactWriter() {
    }

    static List<String> write(FileSystemTool fs, String content, String fallbackPath) {
        List<String> results = new ArrayList<>();
        Map<String, String> files = parse(content);
        if (files.isEmpty()) {
            ToolResult r = fs.writeFile(fallbackPath, content == null ? "" : content);
            results.add(r.toString());
            return results;
        }
        for (Map.Entry<String, String> e : files.entrySet()) {
            ToolResult r = fs.writeFile(sanitize(e.getKey()), e.getValue());
            results.add(r.toString());
        }
        return results;
    }

    static Map<String, String> parse(String content) {
        Map<String, String> files = new LinkedHashMap<>();
        if (content == null || content.isBlank()) {
            return files;
        }
        Matcher m = FILE_BLOCK.matcher(content);
        List<int[]> spans = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        while (m.find()) {
            paths.add(m.group(1).trim());
            spans.add(new int[]{m.end(), -1});
        }
        for (int i = 0; i < spans.size(); i++) {
            int start = spans.get(i)[0];
            int end = (i + 1 < spans.size())
                    ? matchStartBefore(content, spans, i)
                    : content.length();
            String body = content.substring(start, end).trim();
            body = stripCodeFences(body);
            files.put(paths.get(i), body);
        }
        return files;
    }

    private static int matchStartBefore(String content, List<int[]> spans, int i) {
        // The next block's header start is just before its captured content start.
        // Recompute the header position by searching backwards from the next content start.
        int nextContentStart = spans.get(i + 1)[0];
        String prefix = content.substring(0, nextContentStart);
        Matcher m = FILE_BLOCK.matcher(prefix);
        int last = nextContentStart;
        while (m.find()) {
            last = m.start();
        }
        return last;
    }

    private static String stripCodeFences(String body) {
        String trimmed = body.strip();
        if (trimmed.startsWith("```")) {
            int firstNl = trimmed.indexOf('\n');
            if (firstNl > 0) {
                trimmed = trimmed.substring(firstNl + 1);
            }
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3);
            }
        }
        return trimmed.strip() + "\n";
    }

    private static String sanitize(String path) {
        return path.replace('\\', '/').replaceAll("^/+", "");
    }
}
