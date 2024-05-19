package org.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassVisitor extends VoidVisitorAdapter<Map<String, List<MethodDetails>>> {
    @Override
    public void visit(ClassOrInterfaceDeclaration cid, Map<String, List<MethodDetails>> collector) {
        super.visit(cid, collector);
        List<MethodDetails> methodsList = new ArrayList<>();
        for (MethodDeclaration method : cid.getMethods()) {
            if (!method.getBody().orElseThrow().toString().equals("{\n}")) {
                methodsList.add(new MethodDetails(
                                method.getNameAsString(),
                                method.getDeclarationAsString(true, true, true) + " "
                                        + method.getBody().orElseThrow()
                        )
                );
            }
        }
        collector.put(cid.getNameAsString(), methodsList);
    }
}
