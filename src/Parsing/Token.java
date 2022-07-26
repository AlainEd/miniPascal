package Parsing;

public class Token {
        //Para el NOMBRE del token. 
    public static final int FIN        = 0;
    public static final int ERROR      = 1;
    public static final int PROGRAM    = 2;     //"program"
    public static final int VAR        = 3;
    public static final int PROCEDURE  = 4;
    public static final int BEGIN      = 5;
    public static final int END        = 6;
    public static final int IF         = 7;
    public static final int THEN       = 8;
    public static final int ELSE       = 9;
    public static final int FOR        = 10;
    public static final int TO         = 11;
    public static final int DO         = 12;
    public static final int DOWNTO     = 13;
    public static final int WHILE      = 14;
    public static final int REPEAT     = 15;
    public static final int UNTIL      = 16;
    public static final int READLN     = 17;
    public static final int WRITELN    = 18;
    public static final int NOT        = 19;     //"not"
    public static final int AND        = 20;     //"and"
    public static final int OR         = 21;     //"or"
    public static final int DIV        = 22;     //"div" o "/"
    public static final int MOD        = 23;     //"mod" o "%"
    public static final int MAS        = 24;     //"+"
    public static final int MENOS      = 25;     //"-"
    public static final int POR        = 26;     //"*" 
    public static final int COMA       = 27;     //","
    public static final int DOSPTOS    = 28;     //":"
    public static final int PTO        = 29;     //"."
    public static final int PTOCOMA    = 30;     //";"
    public static final int PA         = 31;     //"("
    public static final int PC         = 32;     //")"   
    public static final int ASSIGN     = 33;     //":="   
    public static final int NUM        = 34;
    public static final int ID         = 35;
    public static final int STRINGctte = 36;
    public static final int OPREL      = 37;
    public static final int TIPO       = 38;    //"Integer" o "boolean"
    
    
        //Atributos del token OPREL
    public static final int IGUAL = 0;  //"="
    public static final int MEN   = 1;  //"<"
    public static final int MAY   = 2;  //">"
    public static final int MEI   = 3;  //"<="
    public static final int MAI   = 4;  //">="
    public static final int DIS   = 5;  //"<>"
    
        //Atributos del token TIPO
    public static final int BOOLEAN = -3;
    public static final int INTEGER = -2;
     
    
        //Campos de la class
    private int nom, atr;   //<nom, atr>
    
    public Token(int nombre, int atributo){
        nom = nombre;   atr=atributo;
    }
    
    public Token(int nombre){
        this(nombre, 0);
    }
    
    public Token(){
       this(FIN); 
    }      
    
    public void set(Token t){
        nom = t.nom;   atr=t.atr;
    }
    
    public void set(int nombre, int atributo){
        nom = nombre;   atr=atributo;
    }

    public void setNom(int nom) {
        set(nom, -1);
    }

    public void setAtr(int atr) {
        this.atr = atr;
    }

    public int getNom() {
        return nom;
    }
    
    public String getNomToken(int idToken) {
        return get(NOMtokenSTR, idToken);
    }

    public int getAtr() {
        return atr;
    }
 
//----------
    private static char AUXchar[]={'/','%','+','-','*', ',', ':', '.', ';', '(', ')'};
    
    /**@param aux un char auxiliar del lenguaje o /, %, +, -, * 
     * @return el nombre del token correspondiente a aux. Si aux, no es un char del lenguaje, return -1 */
    public static int getNomToken(char aux){
        for (int i=0; i<AUXchar.length; i++){
            if (aux==AUXchar[i])                
                return i+DIV;            
        }
        
        return -1;
    }
 
    public static String getNomAsString(int nombre){
        return get(NOMtokenSTR, nombre);
    }
    
//---------- 
    @Override
    public String toString(){
        return "<" + get(NOMtokenSTR, nom) + "," + atrToString() + ">";
    }
       
    private String atrToString(){   //Corrutina de toString()
        if (FIN <= nom && nom <=ASSIGN)
            return "_";
        
        if (nom == OPREL)
            return get(OPRELstr, atr);
        
        if (nom == TIPO)
            return get(TIPOstr, atr-BOOLEAN);
        
        return ""+atr;
    }

    private static String get(String v[], int i){
        try {
            return v[i]; 
        } catch (Exception e) {
            return DESCONOCIDO;
        }
    }
    
    
    private static final String DESCONOCIDO = "??";
    
    private static final String OPRELstr[]={"IGUAL", "MEN", "MAY", "MEI", "MAI", "DIS"};
    private static final String TIPOstr[] ={"BOOLEAN", "INTEGER"};
    
    private static final String NOMtokenSTR[] ={
        "FIN","ERROR",
        "PROGRAM","VAR","PROCEDURE","BEGIN","END",
        "IF","THEN","ELSE","FOR","TO","DO","DOWNTO","WHILE","REPEAT","UNTIL",
        "READLN","WRITELN", "NOT","AND","OR", "DIV","MOD",
        "MAS","MENOS","POR","COMA", "DOSPTOS", "PTO", "PTOCOMA", "PA", "PC",        
        "ASSIGN",
        "NUM","ID","STRINGctte","OPREL","TIPO"
    };      
}
