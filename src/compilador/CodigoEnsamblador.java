/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 * Clase que contiene codigos para generar funciones especificas de mi lenguaje,
 * seran estaticos
 *
 * @author Alberto
 */
public class CodigoEnsamblador {
    
    static String goTo(int tag){
        System.out.println("LLAMANDOGOTO");
        return ";goto "+tag+"\n"+
                "jmp "+tag+"\n";
    }

    static String comparaciones(String var1, String cmp, String var2, int tag) {
        String codigoTemp = ";if " + var1 + cmp + var2 + "goto" + tag + "\n";
        if (var1.matches("\\d")) {
            //El primero es constante
            codigoTemp += "mov t1," + var1 + "\n";
        } else {
            //El primero es variable
            codigoTemp += "mov ax, " + var1 + "\n";
            codigoTemp += "mov t1, ax" + "\n";
        }
        if (var2.matches("\\d")) {
            //El segundo es constante
            codigoTemp += "mov t2," + var2 + "\n";
        } else {
            //El segundo es variable
            codigoTemp += "mov ax, " + var2 + "\n";
            codigoTemp += "mov t2, ax" + "\n";
        }

        switch (cmp) {
            case "==":
                return codigoTemp += var1_igual_igual_var2(tag);
            case "<":
                return codigoTemp += var1_menor_var2(tag);
            case "<=":
                return codigoTemp += var1_menor_igual_var2(tag);
            case ">":
                return codigoTemp += var1_mayor_var2(tag);
            case ">=":
                return codigoTemp += var1_mayor_igual_var2(tag);
        }
        return "error";
    }

    static String var1_menor_var2(int tag) {
        return "mov ax, t1\n"
                + "cmp ax, t2\n"
                + "jl   T" + tag + "\n";
    }

    static String var1_menor_igual_var2(int tag) {
        return "mov ax, t1\n"
                + "cmp ax, t2\n"
                + "jle   T" + tag + "\n";
    }

    static String var1_mayor_var2(int tag) {
        return "mov ax, t1\n"
                + "cmp ax, t2\n"
                + "jg   T" + tag + "\n";
    }

    static String var1_mayor_igual_var2(int tag) {
        return "mov ax, t1\n"
                + "cmp ax, t2\n"
                + "jge   T" + tag + "\n";
    }

    static String var1_igual_igual_var2(int tag) {
        return "mov ax, t1\n"
                + "cmp ax, t2\n"
                + "je   T" + tag + "\n";
    }

    static String asignaciones(String asign, String op1, String operador, String op2) {
        switch (operador) {
            case "+":
                if (op1.matches("\\d") && op2.matches("\\d")) {
                    //return "const+const";
                    return ";const_plus_const" + const_plus_const(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && op2.matches("\\d")) {
                    //return "arg+const";
                    return ";arg_plus_const" + arg_plus_const(asign, op1, operador, op2);
                } else if (op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "const+arg";
                    return ";arg_plus_const" + arg_plus_const(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "arg+arg";
                    return ";arg_plus_arg" + arg_plus_arg(asign, op1, operador, op2);
                }
            case "-":
                if (op1.matches("\\d") && op2.matches("\\d")) {
                    //return "const-const";
                    return ";const_minus_const" + const_minus_const(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && op2.matches("\\d")) {
                    //return "arg-const";
                    return ";arg_minus_const" + arg_minus_const(asign, op1, operador, op2);
                } else if (op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "const-arg";
                    return ";const_minus_arg" + const_minus_arg(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "arg+arg";
                    return ";arg_minus_arg" + arg_minus_arg(asign, op1, operador, op2);
                }
            case "*":
                if (op1.matches("\\d") && op2.matches("\\d")) {
                    //return "const-const";
                    return ";const_times_const" + const_times_const(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && op2.matches("\\d")) {
                    //return "arg-const";
                    return ";arg_times_const" + arg_times_const(asign, op1, operador, op2);
                } else if (op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "const-arg";
                    return ";arg_times_const" + arg_times_const(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "arg+arg";
                    return ";arg_times_arg" + arg_times_arg(asign, op1, operador, op2);
                }
            case "/":
                if (op1.matches("\\d") && op2.matches("\\d")) {
                    //return "const-const";
                    return ";const_over_const" + const_over_const(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && op2.matches("\\d")) {
                    //return "arg-const";
                    return ";arg_over_const" + arg_over_const(asign, op1, operador, op2);
                } else if (op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "const-arg";
                    return ";const_over_arg" + const_over_arg(asign, op1, operador, op2);
                } else if (!op1.matches("\\d") && !op2.matches("\\d")) {
                    //return "arg+arg";
                    return ";arg_over_arg" + arg_over_arg(asign, op1, operador, op2);
                }
        }
        return "";
    }

    static String asignacionVar(String var, String asign) {
        System.out.println("LLAMADOasignacionVar");
        return ";" + var + "=" + asign + "\n"
                + "mov ax, " + asign + "\n"
                + "mov " + var + ", ax\n";
        //       +
        //       ;

        // return "falta codigo";
    }

    public static String arg_plus_arg(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "add ax, " + op2 + "\n"
                + "mov " + asign + ", ax; temp0\n";
    }

    public static String arg_plus_const(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "add ax, " + op2 + "\n"
                + "mov " + asign + ", ax;\n";
    }

    public static String const_plus_const(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "add ax, " + op2 + "\n"
                + "mov " + asign + ", ax\n";
    }

    public static String arg_minus_arg(String asign, String op1, String operador, String op2) {

        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "sub ax, " + op2 + "\n"
                + "mov " + asign + ", ax\n";
    }

    public static String arg_minus_const(String asign, String op1, String operador, String op2) {

        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "sub ax, " + op2 + "\n"
                + "mov " + asign + ", ax\n";
    }

    public static String const_minus_const(String asign, String op1, String operador, String op2) {

        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "sub ax, " + op2 + "\n"
                + "mov " + asign + ", ax\n";
    }

    public static String const_minus_arg(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + ";mov ax, " + op1 + ";\n"
                + ";sub ax, " + op2 + ";\n"
                + ";mov " + asign + ", ax;\n";
    }

    public static String arg_times_arg(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op2 + "\n"
                + "mul " + op1 + "\n"
                + "mov " + asign + ", ax\n";
    }

    public static String arg_times_const(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op2 + "\n"
                + "mul " + op1 + "\n"
                + "mov " + asign + ", ax";
    }

    public static String const_times_const(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "\n"
                + "mov " + asign + ", " + op2 + "\n"
                + "mul " + asign + "\n"
                + "mov " + asign + ", ax\n";
    }

    //Creo dos constantes mas t3 y t4 auxiliares de la division
    public static String arg_over_arg(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov dx, 0\n"
                + "mov ax, " + op1 + "\n"
                + "idiv " + op2 + "\n"
                + "mov " + asign + ", ax\n";
    }

    //Creo dos constantes mas t3 y t4 auxiliares de la division
    public static String arg_over_const(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov t3, " + op2 + "\n"
                + "mov dx, 0\n"
                + "mov ax, " + op1 + "\n"
                + "idiv t3\n"
                + "mov " + asign + ", ax\n";
    }

    public static String const_over_arg(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + ";mov t1, " + op1 + ";arg1\n"
                + ";mov dx, 0\n"
                + ";mov ax, " + op2 + "\n"
                + ";idiv " + op2 + "\n"
                + ";mov " + asign + ", ax\n"
                + ";call printud  ";
    }

    //Uso t3 y t4
    public static String const_over_const(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov t3, " + op1 + "\n"
                + "mov t4, " + op2 + "\n"
                + "mov dx, 0\n"
                + "mov ax, t3\n"
                + "idiv t4\n"
                + "mov a1, ax";
    }
}
