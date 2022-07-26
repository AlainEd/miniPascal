package Parsing;

import java.util.LinkedList;
import javax.swing.JOptionPane;

public class Parser {
    private Analex analex;
    private ErrorMgr errorMgr;
    
    public Parser(){
        errorMgr = new ErrorMgr();
        analex = new Analex(errorMgr);
    }
    
    public void init(String progFuente){    
        errorMgr.init();
        analex.init(progFuente);
        
        programa();     //Llamar al símbolo inicial de la BNF.
    }
    
    public boolean hayError(){
        return errorMgr.hayError();
    }
    
    public void comunicarError(){
        onComunicar(errorMgr.getPosLexema(), errorMgr.getLexema().length(), errorMgr.getErrorMsj());
    }
    
    public void onComunicar(int posLexema, int longitud, String errorMsg){   
        //Overridable (Para el front-end)       
    }
    
    private boolean pertenece(int token, LinkedList<Integer> L) {
        for (int i = 0; i < L.size(); i++) {
            if (L.get(i) == token) 
                return true;
        }
        return false;
    }
    
//---------    
    private void programa(){ //Símbolo inicial. programa->Header Cuerpo Main
            //Seguir única sección
        header();
        cuerpo();
        main();
    }
    
    private void header(){  //header -> PROGRAM ID; | lambda
        if (analex.preNom() == Token.PROGRAM){
            //Seguir 1era seccion: PROGRAM ID ;
           match(Token.PROGRAM, "Se espera PROGRAM");
           match(Token.ID, "Se espera un 'Nombre' para el programa");
           match(Token.PTOCOMA, "Se espera un PTOCOMA");
        }
        else //Seguir 2da sección: lambda
          ; 
    }
    
    private void cuerpo(){
        if (analex.preNom() == Token.VAR) {
            //seguir primera seccion 
            Decl();
            cuerpo();
        }else if (analex.preNom() == Token.PROCEDURE) {
            //seguir segunda seccion
            Proc();
            cuerpo();
        }else // lambda
            ;
    }
    
    private void main(){
        //unica seccion
        match(Token.BEGIN, "Se espera un 'BEGIN END.' obligatorio para la sección principal");
        Sentencia();
        match(Token.END, "Se espera un 'END' para cerrar la seccion principal ");
        match(Token.PTO, "Se espera un PUNTO ");
    }
    
    private void match(int tokenNom, String errorMsj){
        if (analex.preNom() == tokenNom)
            analex.avanzar();
        else
            errorMgr.setError(errorMsj + analex.preNomToken(), analex.getPosLexema(), analex.lexema());
    }
    
//------ Declaracion de variables 
    
    private void Decl() {
        if (analex.preNom() == Token.VAR) {
            //seguir primera seccion
            match(Token.VAR, "Se espera VAR");
            Linea();
            masLinea();
        }else // lambda
            ;
    }
    
    private void masLinea() {
        if (analex.preNom() == Token.ID) {
            //seguir primera seccion
            Linea();
            masLinea();
        }else // lambda
            ;
    }
    
    private void masID() {
        if (analex.preNom() == Token.COMA) {
            //seguir primera seccion
            match(Token.COMA, "Se espera COMA para separar las variables");
            match(Token.ID, "Se espera un ID");
            masID();
        }else //lambda
            ;
    }
    
    private void Linea() {
        //seguir unica seccion
        match(Token.ID, "Se espera un 'ID : TIPO;' para la declarion de variables");
        masID();
        match(Token.DOSPTOS, "Se espera DOSPUNTOS(:) para especificar el tipo de dato");
        match(Token.TIPO, "Se espera un tipo de dato BOOLEAN o INTEGER");
        match(Token.PTOCOMA, "Se espera un PTOCOMA(;)");
    }
    
//--------Declaracion de procedimientos   

    private void Proc() {
        if (analex.preNom() == Token.PROCEDURE) {
            //seguir primera seccion
            match(Token.PROCEDURE, "Se espera PROCEDURE");
            match(Token.ID, "Se espera un ID para el PROCEDURE");
            match(Token.PTOCOMA, "Se espera un PTOCOMA(;)");
            match(Token.BEGIN, "Se espera abrir un bloque BEGIN END para el PROCEDURE");
            Sentencia();
            match(Token.END, "Se espera END para cerrar el bloque PROCEDURE");
            match(Token.PTOCOMA, "Se espera un 'PTOCOMA(;)'.");
        }else  //lambda
            ;
    }

//----------Sentencia
   
    private void Sentencia() {
        if (analex.preNom() == Token.ID) {
            //seguir primera seccion
            match(Token.ID, "Se espera ID");
            Sentencia1();
            Sentencia();
        }else{
            if (analex.preNom() == Token.IF) {
                //seguir segunda seccion
                Condicional(true);
                Sentencia();
            }else{
                if (analex.preNom() == Token.FOR) {
                    //seguir tercera seccion
                    BucleFor(true);
                    Sentencia();
                }else{
                    if (analex.preNom() == Token.WHILE) {
                        //seguir cuarta seccion
                        BucleWhile(true);
                        Sentencia();
                    }else{
                        if (analex.preNom() == Token.REPEAT) {
                            //seguir quinta seccion
                            BucleRepeat(true);
                            Sentencia();
                        }else{
                            if (analex.preNom() == Token.READLN) {
                                //seguir sexta seccion
                                Lectura(true);
                                Sentencia();
                            }else{
                                if (analex.preNom() == Token.WRITELN) {
                                    //seguir septima seccion
                                    Impresion("Se espera el token WRITELN", true);
                                    Sentencia();
                                }else    //lambda
                                    ;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void Sentencia1() {
        if (analex.preNom() == Token.ASSIGN) {
            //seguir primera seccion
            match(Token.ASSIGN, "Se espera ':=' para la asignación");
            Expr("Se espera un token para la expresión");
            match(Token.PTOCOMA, "Se espera PTOCOMA");
        }else{
            //seguir segunda seccion 
            match(Token.PA, "Se espera '()' para la llamada de un PROCEDURE");
            match(Token.PC, "Se espera ')'");
            match(Token.PTOCOMA, "Se espera ';'");
        }
    }
    
    private void unaSentencia(String msj) {
        if (analex.preNom() == Token.ID) {
            //seguir primera seccion
            match(Token.ID, "Se espera ID");
            unaSentencia1();
        }else{
            if (analex.preNom() == Token.IF) {
                //seguir segunda seccion
                Condicional(false);
            }else{
                if (analex.preNom() == Token.FOR) {
                    //seguir tercera seccion
                    BucleFor(false);
                }else{
                    if (analex.preNom() == Token.WHILE) {
                        //seguir cuarta seccion
                        BucleWhile(false);
                    }else{
                        if (analex.preNom() == Token.REPEAT) {
                            //seguir quinta seccion
                            BucleRepeat(false);
                        }else{
                            if (analex.preNom() == Token.READLN) {
                                //seguir sexta seccion
                                Lectura(false);
                            }else{
                                //seguir septima seccion
                                Impresion(msj, false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void unaSentencia1() {
        if (analex.preNom() == Token.ASSIGN) {
            //seguir primera seccion
            match(Token.ASSIGN, "Se espera ':=' para la asignación");
            Expr("Se espera un token para la expresión");
        }else{
            //seguir segunda seccion 
            match(Token.PA, "Se espera '()' para la llamada de un PROCEDURE");
            match(Token.PC, "Falta ')'");
        }
    }

//------- Asignacion 
    
    private void Asignacion() {
        //seguir unica seccion
        match(Token.ID, "Se espera un ID para la asignación");
        match(Token.ASSIGN, "Se espera ':=' en el operador de asignación");
        Expr("Se espera un token para la expresión");
        match(Token.PTOCOMA, "Se espera ';' para cerrar la asignación");
    }
    
    private void Expr(String msj) {
        //seguir unica seccion
        Termino(msj);
        Expr2();
    }
    
    private void Expr2() {
        LinkedList<Integer> L = new LinkedList();
        L.add(Token.MAS); L.add(Token.MENOS);
        if (pertenece(analex.preNom(), L)) {
            //seguir primera seccion
            Expr1();
            Expr2();
        }else   //lambda
            ;
    }
    
    private void Expr1() {
        if (analex.preNom() == Token.MAS) {
            //seguir primera seccion
            match(Token.MAS, "Se espera '+' en la expresión.");
            Termino("Se espera un token para la expresión");
        }else{ 
            if (analex.preNom() == Token.MENOS) {
                //seguir segunda seccion
                match(Token.MENOS, "Se espera '-' en la expresión");
                Termino("Se espera un token para la expresión");
            }
        }
    }
    
    private void Termino(String msj) {
        //seguir unica seccion
        Factor(msj);
        Termino2();
    }
    
    public void Termino2() {
        LinkedList<Integer> L = new LinkedList();
        L.add(Token.POR); L.add(Token.DIV); L.add(Token.MOD);
        if (pertenece(analex.preNom(), L)) {
            //seguir primera seccion
            Termino1();
            Termino2();
        }else    //lambda
            ;
    }
    
    public void Termino1() {
        if (analex.preNom() == Token.POR) {
            //seguir primera seccion
            match(Token.POR, "Se espera '*' en la expresión");
            Factor("Se espera un token para la expresión");
        }else{ 
            if (analex.preNom() == Token.DIV) {
                //seguir segunda seccion
                match(Token.DIV, "Se espera '/' en la expresión");
                Factor("Se espera un token para la expresión");
            }else{
                //seguir tercera seccion
                match(Token.MOD, "Se espera 'MOD' en la expresión");
                Factor("Se espera un token para la expresión");
            }
        }
    }
    
    public void Factor(String msj) {
        if (analex.preNom() == Token.PA) {
            //seguir quinta seccion
            match(Token.PA, "Se espera un '(' para la expresión");
            Expr(msj);
            match(Token.PC, "Se espera un ')' para la expresión");
        }else{ 
            if (analex.preNom() == Token.ID) { 
                //seguir primera seccion
                match(Token.ID, "Se espera un ID para la expresión");
            }else{ 
                if (analex.preNom() == Token.MENOS) {
                    //seguir tercera seccion
                    match(Token.MENOS, "Se espera '-' para la expresión");
                    Factor("Se espera una expresión");
                }else{ 
                    if (analex.preNom() == Token.MAS) {
                        match(Token.MAS, "Se espera '+' para la expresión");
                        Factor("Se espera una expresión");
                    }else{
                        //seguir segunda seccion
                        match(Token.NUM, msj);
                    }
                }
            }
        }
    }
   
    
//---------Llamada
    
    private void Llamada() {
        //seguir unica seccion
        match(Token.ID, "Se espera un ID.");
        match(Token.PA, "Se espera una asigmación o llamada a un PROCEDURE");
        match(Token.PC, "Se espera ')' para cerrar un PROCEDURE");
        match(Token.PTOCOMA, "Se espera PTOCOMA");
    }

//---------Condicional
   
    private void ExprBoole(String msj) {
        //seguir unica seccion
        TermBoole(msj);
        ExprBoole1();
    }
    
    private void ExprBoole1() {
        if (analex.preNom() == Token.OR) {
            //seguir primera seccion
            match(Token.OR, "Se espera 'OR'");
            TermBoole("Se espera una expresión booleana para el token OR");
            ExprBoole1();
        }else    //lambda
            ;
    }
    
    private void TermBoole(String msj) {
        //seguir unica seccion
        FactorBoole(msj);
        TermBoole1(msj);
    }
    
    private void TermBoole1(String msj) {
        if (analex.preNom() == Token.AND) {
            //seguir primera seccion
            match(Token.AND, "Se espera un AND");
            FactorBoole(msj);
            TermBoole1("Se espera una expresion booleana para el token AND");
        }else    //lambda
            ;
    }
    
    private void FactorBoole(String msj) {
        if (analex.preNom() == Token.NOT) {
            //seguir segunda seccion Token.NOT
            match(Token.NOT, msj);
            FactorBoole(msj);
        }else{
            //seguir primera seccion
            Expr(msj);
            match(Token.OPREL, "Se espera un operador relacional [<, >, >=, <=, etc.]");
            Expr("Se espera una expresión");
        }
    }
    
    private void Condicional(boolean ptoComa) {
        //seguir unica seccion
        match(Token.IF, "Se espera un IF");
        ExprBoole("Se espera una expresión booleana para la condicional IF");
        match(Token.THEN, "Se espera THEN");
        Condicional1(ptoComa);
    }
    
    private void Condicional1(boolean ptoComa) {
        if (analex.preNom() == Token.BEGIN) {
            //seguir primera seccion
            match(Token.BEGIN, "Se espera un bloque 'BEGIN END' para la condicional IF");
            Sentencia();
            match(Token.END, "Se espera un 'END' para cerrar el bloque IF");
            Condicional2(ptoComa);
        }else{
            //seguir segunda seccion
            unaSentencia("Se espera una sentencia para la condicional IF");
            Condicional2(ptoComa);
        }
    }
    
    private void Condicional2(boolean ptoComa) {
        if (analex.preNom() == Token.ELSE) {
            //seguir primera seccion
            match(Token.ELSE, "Se espera un token ELSE");
            Condicional3(ptoComa);
        }else
            if (ptoComa)
                //seguir segunda seccion PTOCOMA
                match(Token.PTOCOMA, "Se espera un PTOCOMA para cerrar la condicional IF o un token");
    }
    
    private void Condicional3(boolean ptoComa) {  
        if (analex.preNom() == Token.BEGIN) {
            //seguir segunda seccion BEGIN
            match(Token.BEGIN, "Se espera un BEGIN");
            Sentencia();
            match(Token.END, "Se espera un END");
            if (ptoComa)
                match(Token.PTOCOMA, "Se espera un PTOCOMA");
        }else{
            //seguir primera seccion { F(unaSentencia) }
            unaSentencia("Se espera una sentencia o un bloque de sentencias 'BEGIN END' para la seccion ELSE");
            if (ptoComa)
                match(Token.PTOCOMA, "Se espera un PTOCOMA");
        }
    }
    
//--------Bucle For
    
    private void BucleFor(boolean ptoComa) {
        //seguir unica seccion
        match(Token.FOR, "Se espera un FOR");
        match(Token.ID, "Se espera un ID");
        match(Token.ASSIGN, "Se espera un ':=' para la asignacion");
        Expr("Se espera un token para la expresión");
        condFor();
        Expr("Se espera un token para la expresión");
        match(Token.DO, "Se espera un DO");
        BucleForWhile("FOR", ptoComa);
    }
    
    private void BucleForWhile(String struct, boolean ptoComa) {
        if (analex.preNom() == Token.BEGIN) {
            //seguir primera seccion
            match(Token.BEGIN, "Se espera un BEGIN");
            Sentencia();
            match(Token.END, "Se espera un END");
            if (ptoComa)
                match(Token.PTOCOMA, "Se espera un PTOCOMA");
        }else{
            //seguir segunda seccion unaSentencia
            if (struct.equals("FOR"))
                unaSentencia("Se espera otro token antes del final de la construcción FOR");
            else 
                unaSentencia("Se espera otro token antes del final de la construcción WHILE");
            
            if (ptoComa)
                match(Token.PTOCOMA, "Se espera un PTOCOMA");
        }
    }
    
    private void condFor() {
        if (analex.preNom() == Token.TO) {
            //seguir primera seccion
            match(Token.TO, "Se espera TO");
        }else{
            /*seguir segunda seccion DOWNTO
                Mensaje del token DOWNTO es :
                    Se espera un 'TO o DOWNTO' para el bucle FOR.
                Esto para que el usuario elija cual ingresar
            */
            match(Token.DOWNTO, "Se espera un 'TO o DOWNTO' para el bucle FOR");
        }
    }
    
//-------Bucle While
    
    private void BucleWhile(boolean ptoComa) {
        //seguir unica seccion
        match(Token.WHILE, "Se espera un WHILE");
        ExprBoole("Se espera una expresión booleana para la estructura WHILE");
        match(Token.DO, "Se espera un DO");
        BucleForWhile("WHILE", ptoComa);
    }   
    
//--------Repeat Until

    private void BucleRepeat(boolean ptoComa) {
        //seguir unica seccion
        match(Token.REPEAT, "Se espera un REPEAT");
        BucleRepeat1();
        match(Token.UNTIL, "Se espera UNTIL");
        ExprBoole("Se espera una expresión booleana para el bucle REPEAT UNTIL");
        if (ptoComa)
            match(Token.PTOCOMA, "Se espera un PTOCOMA");
    }
    
    private void BucleRepeat1() {
        LinkedList<Integer> L = new LinkedList();
        L.add(Token.ID); L.add(Token.IF); L.add(Token.FOR); L.add(Token.WHILE);
        L.add(Token.REPEAT); L.add(Token.WRITELN); L.add(Token.READLN); 
        
        if (pertenece(analex.preNom(), L)) {
            //seguir primera seccion
            Sentencia();
        }else    //lambda
            ;
    }
    
//-------Lectura
    
    private void Lectura(boolean ptoComa) {
        //seguir unica seccion
        match(Token.READLN, "Se espera un READLN");
        match(Token.PA, "Se espera un '('");
        match(Token.ID, "Se espera un ID");
        masID();
        match(Token.PC, "Se espera un ')'");
        if (ptoComa)
            match(Token.PTOCOMA, "Se espera un PTOCOMA");
    }
   
//-------Impresión
    
    private void Impresion(String msj, boolean ptoComa) {
        //seguir unica seccion
        match(Token.WRITELN, msj);
        match(Token.PA, "Se espera un '('");
        Elem();
        masElem();
        match(Token.PC, "Se espera un ')'");
        if (ptoComa)
            match(Token.PTOCOMA, "Se espera un PTOCOMA");
    }
    
    private void masElem() {
        if (analex.preNom() == Token.COMA) {
            //seguir primera seccion
            match(Token.COMA, "Se espera una COMA");
            Elem();
            masElem();
        }else    //lambda
            ;
    }
    
    private void Elem() {
        if (analex.preNom() == Token.STRINGctte) 
            //seguir primera seccion
            match(Token.STRINGctte, "Se espera una STRINGctte");
        else
            //seguir segunda seccion  EXPR()
            Expr("Se espera un token para imprimir");
    }
}
