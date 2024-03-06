package org.unosoft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SolverTests {
    private List<String> blockToList(String block) {
        return Arrays.asList(block.split("\n"));
    }

    List<String> oneGroup = blockToList(
            """
            "111";"123";"222"
            "200";"123";"100\""""
    );
    List<List<String>> oneGroupAnswer = List.of(
            blockToList(
                """
                "111";"123";"222"
                "200";"123";"100\""""
            )
    );
    List<String> twoGroups = blockToList(
            """
            "111";"123";"222"
            "200";"123";"100"
            "123";"100";"200\""""
    );
    List<List<String>> twoGroupsAnswer = List.of(
            blockToList(
                    """
                    "111";"123";"222"
                    "200";"123";"100\""""
            ),
            List.of("""
                    "123";"100";"200\""""
            )
    );
    List<String> emptyStrings = blockToList(
            """
            "111";"";"222"
            "200";"";"100\""""
    );
    List<List<String>> emptyStringsAnswer = List.of(
            List.of("""
                    "111";"";"222\""""
            ),
            List.of("""
                    "200";"";"100\""""
            )
    );
    List<String> emptyStrings1 = blockToList(
            """
            "";"";""
            "";"1"
            "2";"\""""
    );
    List<List<String>> emptyStringsAnswer1 = List.of(
            List.of("""
                    "";"";"\""""),
            List.of("""
                    "";"1\""""),
            List.of("""
                    "2";"\"""")
    );
    List<String> groupOfFive = blockToList(
            """
            "1";"2";"3";"4"
            "0";"2";"0";"0"
            "2";"1";"0";"1"
            "3";"3";"3";"2"
            "4";"4";"1";"4\""""
    );
    List<List<String>> groupOfFiveAnswer = List.of(
            blockToList(
                """
                "1";"2";"3";"4"
                "0";"2";"0";"0"
                "2";"1";"0";"1"
                "3";"3";"3";"2"
                "4";"4";"1";"4\""""
            )
    );

    Solver solver = new Solver();

    @Test
    void testOneGroup() {
        List<List<String>> result = solver.solve(oneGroup);
        Assertions.assertEquals(oneGroupAnswer, result);
    }

    @Test
    void testTwoGroups() {
        List<List<String>> result = solver.solve(twoGroups);
        Assertions.assertEquals(twoGroupsAnswer, result);
    }

    @Test
    void testEmptyStrings() {
        List<List<String>> result = solver.solve(emptyStrings);
        Assertions.assertTrue(
                result.containsAll(emptyStringsAnswer)
        );
        Assertions.assertEquals(emptyStringsAnswer.size(), result.size());
    }
    @Test
    void testEmptyStrings1() {
        List<List<String>> result = solver.solve(emptyStrings1);
        Assertions.assertEquals(emptyStringsAnswer1, result);
    }
    @Test
    void testGroupOfFive() {
        List<List<String>> result = solver.solve(groupOfFive);
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(
                result.get(0).containsAll(groupOfFiveAnswer.get(0))
        );
        Assertions.assertEquals(groupOfFiveAnswer.get(0).size(), result.get(0).size());
    }
}
