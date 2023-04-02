package com.bobacadodl.imgmessage.bukkit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChatPaginator {
    public static final int GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH = 55;
    public static final int AVERAGE_CHAT_PAGE_WIDTH = 65;
    public static final int UNBOUNDED_PAGE_WIDTH = Integer.MAX_VALUE;
    public static final int OPEN_CHAT_PAGE_HEIGHT = 20;
    public static final int CLOSED_CHAT_PAGE_HEIGHT = 10;
    public static final int UNBOUNDED_PAGE_HEIGHT = Integer.MAX_VALUE;

    public ChatPaginator() {
    }

    @NotNull
    public static ChatPage paginate(@Nullable String unpaginatedString, int pageNumber) {
        return paginate(unpaginatedString, pageNumber, 55, 10);
    }

    @NotNull
    public static ChatPage paginate(@Nullable String unpaginatedString, int pageNumber, int lineLength, int pageHeight) {
        String[] lines = wordWrap(unpaginatedString, lineLength);
        int totalPages = lines.length / pageHeight + (lines.length % pageHeight == 0 ? 0 : 1);
        int actualPageNumber = pageNumber <= totalPages ? pageNumber : totalPages;
        int from = (actualPageNumber - 1) * pageHeight;
        int to = from + pageHeight <= lines.length ? from + pageHeight : lines.length;
        String[] selectedLines = Arrays.copyOfRange(lines, from, to);
        return new ChatPage(selectedLines, actualPageNumber, totalPages);
    }

    @NotNull
    public static String[] wordWrap(@Nullable String rawString, int lineLength) {
        if (rawString == null) {
            return new String[]{""};
        } else if (rawString.length() <= lineLength && !rawString.contains("\n")) {
            return new String[]{rawString};
        } else {
            char[] rawChars = (rawString + ' ').toCharArray();
            StringBuilder word = new StringBuilder();
            StringBuilder line = new StringBuilder();
            List<String> lines = new LinkedList();
            int lineColorChars = 0;

            int i;
            String partialWord;
            for (i = 0; i < rawChars.length; ++i) {
                char c = rawChars[i];
                if (c == 167) {
                    word.append(ChatColor.getByChar(rawChars[i + 1]));
                    lineColorChars += 2;
                    ++i;
                } else if (c != ' ' && c != '\n') {
                    word.append(c);
                } else {
                    int var10;
                    int var11;
                    String[] var12;
                    if (line.length() == 0 && word.length() > lineLength) {
                        var11 = (var12 = word.toString().split("(?<=\\G.{" + lineLength + "})")).length;

                        for (var10 = 0; var10 < var11; ++var10) {
                            partialWord = var12[var10];
                            lines.add(partialWord);
                        }
                    } else if (line.length() + 1 + word.length() - lineColorChars == lineLength) {
                        if (line.length() > 0) {
                            line.append(' ');
                        }

                        line.append(word);
                        lines.add(line.toString());
                        line = new StringBuilder();
                        lineColorChars = 0;
                    } else if (line.length() + 1 + word.length() - lineColorChars > lineLength) {
                        var11 = (var12 = word.toString().split("(?<=\\G.{" + lineLength + "})")).length;

                        for (var10 = 0; var10 < var11; ++var10) {
                            partialWord = var12[var10];
                            lines.add(line.toString());
                            line = new StringBuilder(partialWord);
                        }

                        lineColorChars = 0;
                    } else {
                        if (line.length() > 0) {
                            line.append(' ');
                        }

                        line.append(word);
                    }

                    word = new StringBuilder();
                    if (c == '\n') {
                        lines.add(line.toString());
                        line = new StringBuilder();
                    }
                }
            }

            if (line.length() > 0) {
                lines.add(line.toString());
            }

            if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != 167) {
                lines.set(0, ChatColor.WHITE + lines.get(0));
            }

            for (i = 1; i < lines.size(); ++i) {
                String pLine = lines.get(i - 1);
                partialWord = lines.get(i);
                char color = pLine.charAt(pLine.lastIndexOf(167) + 1);
                if (partialWord.length() == 0 || partialWord.charAt(0) != 167) {
                    lines.set(i, ChatColor.getByChar(color) + partialWord);
                }
            }

            return lines.toArray(new String[lines.size()]);
        }
    }

    public static class ChatPage {
        private final String[] lines;
        private final int pageNumber;
        private final int totalPages;

        public ChatPage(@NotNull String[] lines, int pageNumber, int totalPages) {
            this.lines = lines;
            this.pageNumber = pageNumber;
            this.totalPages = totalPages;
        }

        public int getPageNumber() {
            return this.pageNumber;
        }

        public int getTotalPages() {
            return this.totalPages;
        }

        @NotNull
        public String[] getLines() {
            return this.lines;
        }
    }
}