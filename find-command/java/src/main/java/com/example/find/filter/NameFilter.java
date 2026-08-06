package com.example.find.filter;

import com.example.find.model.FileSystemEntry;

import java.util.regex.Pattern;

/** Matches entry names exactly, or with shell-style glob wildcards '*' and '?'. */
public class NameFilter implements Filter {

    private final String pattern;
    private final boolean glob;
    private final Pattern regex;

    public NameFilter(String pattern) {
        this.pattern = pattern;
        this.glob = pattern.contains("*") || pattern.contains("?");
        this.regex = glob ? Pattern.compile(toRegex(pattern)) : null;
    }

    private static String toRegex(String globPattern) {
        StringBuilder out = new StringBuilder("^");
        for (char c : globPattern.toCharArray()) {
            switch (c) {
                case '*' -> out.append(".*");
                case '?' -> out.append('.');
                case '.', '(', ')', '+', '|', '^', '$', '@', '%', '{', '}', '[', ']', '\\' ->
                        out.append('\\').append(c);
                default -> out.append(c);
            }
        }
        return out.append('$').toString();
    }

    @Override
    public boolean matches(FileSystemEntry entry, int depth) {
        return glob ? regex.matcher(entry.getName()).matches() : entry.getName().equals(pattern);
    }
}
