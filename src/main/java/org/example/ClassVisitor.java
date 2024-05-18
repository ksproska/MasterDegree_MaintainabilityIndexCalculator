package org.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassVisitor extends VoidVisitorAdapter<Map<String, List<String>>> {
    @Override
    public void visit(ClassOrInterfaceDeclaration cid, Map<String, List<String>> collector) {
        super.visit(cid, collector);
        List<String> methodsList = new ArrayList<>();
        for (MethodDeclaration method : cid.getMethods()) {
            methodsList.add(method.getNameAsString());
        }
        collector.put(cid.getNameAsString(), methodsList);
    }
}
