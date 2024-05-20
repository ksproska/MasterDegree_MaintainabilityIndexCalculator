package org.example;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.example.old.ASTVisitorMod;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ASTVisitorModTest {
    @Test
    void testOperatorsAndOperands() throws IOException {
        var content = Files.readString(Path.of("src/test/resources/OldProjectExample.java"));
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(content.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        var visitor = new ASTVisitorMod();
        cu.accept(visitor);

        var distinctOperators = visitor.getDistinctOperators();
        var distinctOperatorsCount = visitor.getDistinctOperatorsCount();
        var allOperatorsCount = visitor.getAllOperatorsCount();

        var distinctOperands = visitor.getDistinctOperands();
        var distinctOperandsCount = visitor.getDistinctOperandsCount();
        var allOperandsCount = visitor.getAllOperandsCount();

        assertEquals("[+, ++, +=, -, --, /, <<, <=, =, ==]", distinctOperators.toString());
        assertEquals(10, distinctOperatorsCount);
        assertEquals(19, allOperatorsCount);

        assertEquals("[0, 1, 2, 7777, 8, Example, String, binSearch, bot, cmp, item, mid, n, null, table, top, xx]", distinctOperands.toString());
        assertEquals(17, distinctOperandsCount);
        assertEquals(41, allOperandsCount);
    }
}
