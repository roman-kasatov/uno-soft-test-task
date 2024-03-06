package org.unosoft;

import java.util.*;

public class Solver {

    // Disjoint Set Union построенный на мапе
    private Map<Integer, Integer> dsu;
    private List<List<String>> groups;

    private int getGroupInDsu(int x) {
        if (dsu.containsKey(x)) {
            int group = getGroupInDsu(dsu.get(x));
            dsu.put(x, group);
            return group;
        }
        return x;
    }

    private void mergeGroupsInDsu(int x, int y) {
        int parentX = getGroupInDsu(x);
        int parentY = getGroupInDsu(y);
        if (parentX != parentY) {
            dsu.put(parentY, parentX);
        }
    }

    /**
     * Проверяет, корректен ли фрагмент.
     * Корректный фрагмент это "[^"]*", например, "123", или пустая строка.
     */
    private static boolean isFragmentCorrect(String fragment) {
        String fragmentRegex = "\"[^\"]*\"";
        return fragment.matches(fragmentRegex) || fragment.isEmpty();
    }

    private static boolean isFragmentEmpty(String fragment) {
        return fragment.isEmpty() || fragment.equals("\"\"");
    }

    /**
     * Разбивает строки на группы. Если две строки имеют одинаковые фрагменты на
     * одинаковых позициях, они помещаются в одну группу.
     * @param lines Список строк.
     * @return Список групп отсортированный по убыванию размера.
     */
    List<List<String>> solve(List<String> lines) {
        this.dsu = new HashMap<>();
        this.groups = new ArrayList<>();

        List<Map<String, Integer>> groupByPositionAndFragment = new ArrayList<>();

        for (String line : lines) {
            String[] fragments = line.split(";");

            if (!Arrays.stream(fragments).allMatch(Solver::isFragmentCorrect)) {
                continue;
            }

            Set<Integer> matchedGroups = new HashSet<>();
            for (int position = 0; position < fragments.length; position++) {
                if (position == groupByPositionAndFragment.size()) {
                    groupByPositionAndFragment.add(new HashMap<>());
                }

                String fragment = fragments[position];
                if (isFragmentEmpty(fragment)) continue;

                Integer group = groupByPositionAndFragment.get(position).get(fragment);
                if (group != null) {
                    matchedGroups.add(group);
                }
            }

            int targetGroup;
            if (matchedGroups.isEmpty()) {
                targetGroup = groups.size();
                groups.add(new ArrayList<>());
            } else {
                targetGroup = matchedGroups.stream().findFirst().get();
                for (int group : matchedGroups) {
                    if (group != targetGroup) {
                        mergeGroupsInDsu(targetGroup, group);
                    }
                }
            }
            groups.get(targetGroup).add(line);
            for (int position = 0; position < fragments.length; position++) {
                groupByPositionAndFragment.get(position).put(
                        fragments[position],
                        targetGroup
                );
            }
        }

        normalizeGroups();

        groups.sort(Comparator.<List<?>>comparingInt(List::size).reversed());
        return groups;
    }

    /**
     * Мержит группы в соответствии с dsu.
     */
    private void normalizeGroups() {
        for (int i = 0; i < groups.size(); i++) {
            int groupInDsu = getGroupInDsu(i);
            if (groupInDsu != i) {
                groups.get(groupInDsu).addAll(groups.get(i));
                groups.set(i, null);
            }
        }
        int newSize = 0;
        for (int i = 0; i < groups.size(); i++) {
            List<String> group = groups.get(i);
            if (group != null) {
                groups.set(i, null);
                groups.set(newSize++, group);
            }
        }
        groups.subList(newSize, groups.size()).clear();
    }
}
