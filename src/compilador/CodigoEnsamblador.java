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

    static String asignaciones(String asign, String op1, String operador, String op2) {
        switch (operador) {
            case "+":
                //necesito saber si el operador es una variable O un temporal (Se tratan igual)

                
                if ((op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("arg+const");
                    return arg_plus_const(asign, op1, operador, op2);
                } else if (!(op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("const+const");
                    return const_plus_const(asign, op1, operador, op2);
                } else {
                    return arg_plus_arg(asign, op1, operador, op2);
                }
            case "-":
                if ((op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("arg-const");
                    return arg_minus_const(asign, op1, operador, op2);
                } else if (!(op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("const-const");
                    return const_minus_const(asign, op1, operador, op2);
                } else {
                    return arg_minus_arg(asign, op1, operador, op2);
                }

            case "*":
                if ((op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("arg*const");
                    return arg_times_const(asign, op1, operador, op2);
                } else if (!(op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("const*const");
                    return const_times_const(asign, op1, operador, op2);
                } else {
                    return arg_times_arg(asign, op1, operador, op2);
                }
            case "/":
                if ((op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("arg/const");
                    return arg_over_const(asign, op1, operador, op2);
                } else if (!(op1.contains("t") || op1.contains("a")) && !(op2.contains("t") || op2.contains("a"))) {
                    System.out.println("const/const");
                    return const_over_const(asign, op1, operador, op2);
                } else {
                    return arg_over_arg(asign, op1, operador, op2);
                }
        }
        return "";
    }

    static String asignacionVar(String var, String asign) {
        return ";"+var+"="+asign;
         //       +
         //       ;
        
       // return "falta codigo";
    }

    public static String arg_plus_arg(String asign, String op1, String operador, String op2) {
        return ";" + asign + "=" + op1 + operador + op2 + "\n"
                + "mov ax, " + op1 + "; subo el operando a la memoria\n"
                + "add ax, " + op2 + "; sumo el numero en memoria\n"
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
