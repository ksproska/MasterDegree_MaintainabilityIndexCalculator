package org.example.old;
/**
 * @author Ahmed Metwally
 * @repository https://github.com/aametwally/Halstead-Complexity-Measures/blob/master/src/main/java/ASTVisitorMod.java
 */

import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.List;


// This class is intended to override the specific methods in the ASTVisitor in order to
// calculate the operator and operands and 
public class ASTVisitorMod extends ASTVisitor {
    private HashMap<String, Integer> operands = new HashMap<String, Integer>();
    private HashMap<String, Integer> operators = new HashMap<String, Integer>();
    CompilationUnit compilation=null;

    public List<String> getDistinctOperands() {
        return operands.keySet().stream().sorted().toList();
    }
    public int getDistinctOperandsCount() {
        return operands.keySet().stream().sorted().toList().size();
    }
    public int getAllOperandsCount() {
        return operands.values().stream().mapToInt(i -> i).sum();
    }

    public List<String> getDistinctOperators() {
        return operators.keySet().stream().sorted().toList();
    }
    public int getDistinctOperatorsCount() {
        return operators.keySet().stream().sorted().toList().size();
    }
    public int getAllOperatorsCount() {
        return operators.values().stream().mapToInt(i -> i).sum();
    }

    // Override visit the infix expressions nodes.
    // if the expression's operator doesn't exist in the operator hashmap, insert it, otherwise, increment the count field.
    public boolean visit(InfixExpression node)
    {
        if (!this.operators.containsKey(node.getOperator().toString()))
        {
            this.operators.put(node.getOperator().toString(), 1);
        }
        else
        {
            this.operators.put(node.getOperator().toString(), this.operators.get(node.getOperator().toString())+1);
        }
        return true;
    }



    // Override visit the postfix expressions nodes.
    // if the expression's operator doesn't exist in the operator hashmap, insert it, otherwise, increment the count field.
    public boolean visit(PostfixExpression node)
    {
        if (!this.operators.containsKey(node.getOperator().toString()))
        {
            this.operators.put(node.getOperator().toString(), 1);
        }
        else
        {
            this.operators.put(node.getOperator().toString(), this.operators.get(node.getOperator().toString())+1);
        }
        return true;
    }



    // Override visit the prefix expressions nodes.
    // if the expression's operator doesn't exist in the operator hashmap, insert it, otherwise, increment the count field.
    public boolean visit(PrefixExpression node)
    {
        if (!this.operators.containsKey(node.getOperator().toString()))
        {
            this.operators.put(node.getOperator().toString(), 1);
        }
        else
        {
            this.operators.put(node.getOperator().toString(), this.operators.get(node.getOperator().toString())+1);
        }

        return true;
    }



    // Override visit the Assignment statements nodes.
    // if the assignment's operator doesn't exist in the operator hashmap, insert it, otherwise, increment the count field.
    public boolean visit(Assignment node)
    {
        if (!this.operators.containsKey(node.getOperator().toString()))
        {
            this.operators.put(node.getOperator().toString(), 1);
        }
        else
        {
            this.operators.put(node.getOperator().toString(), this.operators.get(node.getOperator().toString())+1);
        }

        return true;
    }



    // Override visit the Single Variable Declaration nodes.
    // add the "=" operators to the hashmap of operators if the variable is initialized
    public boolean visit(SingleVariableDeclaration node) {
        if(node.getInitializer()!=null)
        {
            if (!this.operators.containsKey("="))
            {
                this.operators.put("=", 1);
            }
            else
            {
                this.operators.put("=", this.operators.get("=")+1);
            }
        }

        return true;
    }



    // Override visit the Variable Declaration Fragment nodes.
    // add the "=" operators to the hashmap of operators if the variable is initialized
    public boolean visit(VariableDeclarationFragment node) {

        if(node.getInitializer()!=null)
        {
            if (!this.operators.containsKey("="))
            {
                this.operators.put("=", 1);
            }
            else
            {
                this.operators.put("=", this.operators.get("=")+1);
            }
        }

        return true;
    }



    // Override visit the SimpleNames nodes.
    // if the SimpleName doesn't exist in the names hashmap, insert it, otherwise, increment the count field.
    public boolean visit(SimpleName node) {
        if (!this.operands.containsKey(node.getIdentifier()))
        {
            this.operands.put(node.getIdentifier(),1);
        }
        else
        {
            this.operands.put(node.getIdentifier(), this.operands.get(node.getIdentifier())+1);
        }
        return true;
    }



    // Override visit the null nodes.
    // if the null doesn't exist in the names hashmap, insert it, otherwise, increment the count field.
    public boolean visit(NullLiteral node) {
        if (!this.operands.containsKey("null"))
        {
            this.operands.put("null", 1);
        }
        else
        {
            this.operands.put("null", this.operands.get("null")+1);
        }

        return true;
    }



    // Override visit the string literal nodes.
    // if the string literal doesn't exist in the names hashmap, insert it, otherwise, increment the count field.
    public boolean visit(StringLiteral node) {

        if (!this.operands.containsKey(node.getLiteralValue()))
        {
            this.operands.put(node.getLiteralValue(),1);
        }
        else
        {
            this.operands.put(node.getLiteralValue(), this.operands.get(node.getLiteralValue())+1);
        }
        return true;
    }



    // Override visit the character literal nodes.
    // if the character literal doesn't exist in the names hashmap, insert it, otherwise, increment the count field.
    public boolean visit(CharacterLiteral node) {

        if (!this.operands.containsKey(Character.toString(node.charValue())))
        {
            this.operands.put(Character.toString(node.charValue()),1);
        }
        else
        {
            this.operands.put(Character.toString(node.charValue()), this.operands.get(Character.toString(node.charValue()))+1);
        }

        return true;
    }



    // Override visit the boolean literal nodes.
    // if the boolean literal doesn't exist in the names hashmap, insert it, otherwise, increment the count field.
    public boolean visit(BooleanLiteral node) {

        if (!this.operands.containsKey(Boolean.toString(node.booleanValue())))
        {
            this.operands.put(Boolean.toString(node.booleanValue()),1);
        }
        else
        {
            this.operands.put(Boolean.toString(node.booleanValue()), this.operands.get(Boolean.toString(node.booleanValue()))+1);
        }


        return true;
    }



    // Override visit the Number literal nodes.
    // if the Number literal doesn't exist in the names hashmap, insert it, otherwise, increment the count field.
    public boolean visit(NumberLiteral node) {
        if (!this.operands.containsKey(node.getToken()))
        {
            this.operands.put(node.getToken(),1);
        }
        else
        {
            this.operands.put(node.getToken(), this.operands.get(node.getToken())+1);
        }

        return true;
    }



    // Override visit the compilationUnit to be able to retrieve the line numbers.
    public boolean visit(CompilationUnit unit)	{
        compilation=unit;
        return true;
    }
}
