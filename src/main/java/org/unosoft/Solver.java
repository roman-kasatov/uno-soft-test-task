package org.unosoft;

import java.util.*;

public class Solver {

    private List<String> lines;
    private List<LineNode> lineNodes;

    /**
     * Хранит текущий offset для строки (все фрагменты до этой позиции уже сравнивались)
     * и флаг является ли строка некорректной.
     * <p>
     * Также для построения графа, в котором компоненты связности - группы, используется
     * сет {@code connects} с номерами связанных строк.
     */
    private static class LineNode {
        int offset = 0;
        boolean corrupted = false;
        Set<Integer> connects = new HashSet<>();
    }

    /**
     * Пытается прочитать следующий фрагмент до знака <b>;</b> (или конца строки).
     * <p>
     * Корректный фрагмент имеет вид /"\d*"/, например, "123".
     * <p>
     * Если следующий фрагмент некорректный, будет установлен флаг {@code node.corrupted = true}.
     * В любом случае будет обновлено значение {@code node.offset}.
     * @return Следующий фрагмент, если он корректный, и <b>null</b> если нет.
     */
    private static String nextFragment(LineNode node, String line) {
        int semicolonPos = line.indexOf(';', node.offset);
        String fragment;
        if (semicolonPos == -1) {
            fragment = line.substring(node.offset);
            node.offset = line.length();
        } else {
            fragment = line.substring(node.offset, semicolonPos);
            node.offset = semicolonPos + 1;
        }

        String fragmentRegex = "\"\\d*\"";
        if (fragment.matches(fragmentRegex)) {
            return fragment;
        } else {
            node.corrupted = true;
            return null;
        }
    }

    /**
     * Разбивает строки на группы. Если две строки имеют одинаковые фрагменты на
     * одинаковых позициях, они помещаются в одну группу.
     * @param lines Список строк.
     * @return Список групп отсортированный по убыванию размера.
     */
    List<List<String>> solve(List<String> lines) {
        this.lines = lines;

        lineNodes = new ArrayList<>(lines.size()); // lineNodes[i] соответствует lines[i]
        for (int i = 0; i < lines.size(); i++) {
            lineNodes.add(new LineNode());
        }

        buildConnects();

        List<List<String>> groups = findGroupsInGraph();
        groups.sort(Comparator.<List<?>>comparingInt(List::size).reversed());

        return groups;
    }

    /**
     * Добавляет в {@code LineNode.connects} номера строк, которые
     * должны быть в той же группе.
     * <p>
     * Строки добавляются друг другу в connects
     * одновременно, чтобы получился неориентрированный граф.
     * <p>
     * На i-м шаге алгоритм проходит по i-м фрагментам строк, находит строки,
     * у которых они одинаковые и обновляет соответствующие им {@code LineNode.connects}.
     */
    private void buildConnects() {
        List<Integer> notFinishedLines = new LinkedList<>();
        for (int i = 0; i < lines.size(); i++) {
            notFinishedLines.add(i);
        }

        while (!notFinishedLines.isEmpty()) {
            Map<String, Integer> fragmentToLineNumber = new HashMap<>();

            for (var iter = notFinishedLines.iterator(); iter.hasNext(); ) {
                int idx = iter.next();
                LineNode node = lineNodes.get(idx);
                String line = lines.get(idx);

                if (node.offset < line.length()) {
                    String fragment = nextFragment(node, line);
                    if (fragment != null && fragment.length() > 2) { // not empty inside quotes
                        Integer similarLine = fragmentToLineNumber.putIfAbsent(fragment, idx);
                        if (similarLine != null) {
                            node.connects.add(similarLine);
                            lineNodes.get(similarLine).connects.add(idx);
                        }
                    }
                }
                if (node.corrupted || node.offset >= line.length()) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Берет корректную строку и с помощью dfs находит все корректные связанные с ней через
     * {@code LineNode.connects}. Переходит к следующей корректной строке, которая еще не в группе и т. д.
     * @return Неотсортированный список групп. Строки в группе не повторяются.
     */
    private List<List<String>> findGroupsInGraph() {
        List<List<String>> groups = new ArrayList<>();
        for (int idx = 0; idx < lines.size(); idx++) {
            LineNode node = lineNodes.get(idx);
            if (!node.corrupted) {
                groups.add(List.copyOf(bfs(idx)));
            }
        }
        return groups;
    }


    private Set<String> bfs(int startIdx) {
        Set<String> group = new HashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();

        group.add(lines.get(startIdx));
        queue.add(startIdx);
        lineNodes.get(startIdx).corrupted = true; // use corrupted as "visited" to save some space

        while (!queue.isEmpty()) {
            int idx = queue.remove();
            for (int nextIdx : lineNodes.get(idx).connects) {
                String line = lines.get(nextIdx);
                LineNode node = lineNodes.get(nextIdx);
                if (!node.corrupted) {
                    group.add(line);
                    queue.add(nextIdx);
                    node.corrupted = true;
                }
            }
        }
        return group;
    }
}
