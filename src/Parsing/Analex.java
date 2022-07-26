package Parsing;


public class Analex {   //Es una cinta de tokens
    private ErrorMgr errorMgr;
    private Cinta M;
    private Token R;        //Result del dt (Preanalisis)
    private String ac;      
    private int pos;        //Posición de inicio del lexema del preanalisis()
    
    public Analex(ErrorMgr errorMgr){
        this.errorMgr = errorMgr;
        M = new Cinta();
        R = new Token();
    }
    
    public final void init(String progFuente){
        errorMgr.init();
        M.init(progFuente);
        avanzar();      //Calcular el primer preanalisis.
    }
    
    public Token preanalisis(){
        return R;
    }
    
    /** @return el campo <b>nombre</b> del Preanalisis()*/
    public int preNom(){
        return preanalisis().getNom();
    }
    
    public String preNomToken() {
        return ", pero se encontró un " + preanalisis().getNomToken(preNom());
    }
    
    public String lexema(){
        return ac;
    }
    
    public int getPosLexema(){
        return pos;
    }
    
    public void avanzar(){
       dt();
    }
    
    private void dt(){
        int nom;
        Token t;       
        
        int estado = 0;
        ac = "";
        
        while (true){
            char c = M.cc();
           
            switch (estado){
                case 0:                            
                        avanzarEspacios(M); //Avanzar espacios iniciales.
                        
                        c   = M.cc();
                        pos = M.getPos();   //Posición de inicio del lexem que se formará en ac.                        
                        
                        if (c == Cinta.EOF){
                           ac = "";
                           R.setNom(Token.FIN);
                           return;
                        }  
                        
                        ac  = ""+c;      //Empezar a formar el lexem 
                        
                        if (c=='/'){
                            M.avanzar();
                            estado = 100;   //Reconocer DIV o gastar comentario de línea.
                            break;
                        }
                        
                        if (c=='{'){    //Gastar comentario multilinea {...}
                            M.avanzar();
                            estado = 200;
                            break;
                        }
                        
                        if (c=='('){    //Reconocer PA o gastar comentario multilinea (*...*)
                            M.avanzar();
                            estado = 300;
                            break;
                        }
                        
                        t = isOprelORassignORdosptos(M);
                        if (t != null){ //Se reconoció un OPREL o ASSIGN o DOSPTOS
                            R.set(t);
                            return;
                        }
                        
                        if ( (nom=Token.getNomToken(c)) != -1){     //M.cc() es un token monosímbolo
                            M.avanzar();
                            R.setNom(nom);
                            return;
                        }
                        
                        if (c=='"'){    //Ir a reconocer STRINGctte.
                            M.avanzar();
                            estado = 30;
                            break;
                        }
                        
                        if (Character.isDigit(c)){
                            M.avanzar();
                            estado = 10;    //Ir a reconocer NUM.
                        }
                        else                                                       
                            if (isLetra(c)){
                                M.avanzar();
                                estado = 20;    //ID, KeyWord o TIPO
                            }
                            else{   //otro
                                errorMgr.setError("Char no permitido: '"+ac+"'", pos, ac);
                                R.setNom(Token.ERROR);
                                M.avanzar();
                                return;
                            }                       
                    break;
                
                case 10:        //Secuenciar el NUM
                            while ( Character.isDigit(M.cc()) ){
                              ac += M.cc();
                              M.avanzar();
                            }
                            
                            R.set(Token.NUM, Integer.parseInt(ac));
                            return;
                    
                case 20:        //Secuenciar una combinación de Letras y/o Digitos
                            while ( Character.isDigit(M.cc()) || isLetra(M.cc()) ){
                              ac += M.cc();
                              M.avanzar();
                            }
                             
                            t = isKeyWord(ac);
                            if (t != null)
                                R.set(t);   //Se reconoció una palabra reservada o TIPO.
                            else
                                R.set(Token.ID, -1);    //Es un ID
                                                                                     
                            return; 
                            
                case 30:        //char anteriror=". Reconocer STRINGctte
                            while (M.cc()!=Cinta.EOLN && M.cc()!=Cinta.EOF && M.cc() != '"'){
                              ac += M.cc();
                              M.avanzar();
                            }
                            if (M.cc()=='"'){
                                ac += '"';
                                M.avanzar();
                                R.set(Token.STRINGctte, -1);
                            }
                            else{
                                errorMgr.setError("Esta String ctte, excede la línea", pos, ac);
                                R.setNom(Token.ERROR);
                            }                               
                            return;            
                case 100:       //char anterior='/'.
                            if (c!='/'){
                                R.setNom(Token.DIV);
                                return;
                            }                            
                                //Gastar comentario de línea:  //...
                            ac += '/';
                            while (M.cc()!=Cinta.EOLN && M.cc()!=Cinta.EOF)  
                                M.avanzar();
                            
                            estado = 0;
                            break;
                            
                case 200:       //char anterior='{'. Gastar comentario multilínea ...}
                            while (M.cc()!=Cinta.EOF && M.cc()!='}')  
                                M.avanzar();
                            
                            if (M.cc()==Cinta.EOF){
                                errorMgr.setError("Este comentario multilinea {..,no se ha cerrado", pos, ac);
                                R.setNom(Token.ERROR);
                                return;
                            }
                            else
                                M.avanzar();    //Avanzar '}'
                            
                            estado = 0;
                            break;
                            
                case 300:       //char anterior='('.
                            if (c!='*'){
                                R.setNom(Token.PA);
                                return;
                            } 
                            
                            ac += '*';
                            M.avanzar();    //Avanzar '*'
                            
                                //Gastar comentario multilinea:  ... *)
                            char ant = M.cc();
                            while (M.cc()!=Cinta.EOF && (ant != '*' || M.cc()!=')')){ 
                                ant = M.cc();
                                M.avanzar();
                            }    
                            
                            if (M.cc()==Cinta.EOF){
                                errorMgr.setError("Este comentario multilinea (*..., no se ha cerrado", pos, ac);
                                R.setNom(Token.ERROR);
                                return;
                            }
                            else
                                M.avanzar();    //Avanzar ')'
                            
                            estado = 0;
                            break;
                            
            } //End switch
        } //End while
    }
    
    
    private boolean isEspacio(char cc){
        final char SPACE=32, TAB=9;
        return (cc==Cinta.EOLN || cc==SPACE || cc==TAB);
    }
    
    private boolean isLetra(char cc){
        cc = Character.toUpperCase(cc);
        return ('A' <= cc && cc <= 'Z');
    }
    
    private void avanzarEspacios(Cinta M){
        while (isEspacio(M.cc()))  
            M.avanzar();
    }
    
    private Token isOprelORassignORdosptos(Cinta M){    //Corrutina del dt()
        if (aux == null)
            aux = new Token();                                            
        
        switch (M.cc()){
            case ':':   M.avanzar();
                        if (M.cc()=='='){
                            ac += '=';
                            M.avanzar();
                            aux.setNom(Token.ASSIGN);
                        }
                        else
                           aux.setNom(Token.DOSPTOS);
                        
                        break;
                        
            case '<':   M.avanzar();
                        switch (M.cc()) {
                            case '=':
                                ac += '=';
                                M.avanzar();
                                aux.set(Token.OPREL, Token.MEI);
                                break;
                            case '>':
                                ac += '>';
                                M.avanzar();
                                aux.set(Token.OPREL, Token.DIS);
                                break;
                            default:
                                aux.set(Token.OPREL, Token.MEN);
                                break;
                        }                        
                        break;
                        
            case '>':   M.avanzar();
                        if (M.cc()=='='){
                            ac += '=';
                            M.avanzar();
                            aux.set(Token.OPREL, Token.MAI);
                        }
                        else
                           aux.set(Token.OPREL, Token.MAY);
                       
                        break;
                        
            case '=':   M.avanzar();
                        aux.set(Token.OPREL, Token.IGUAL);
                        break;
                        
            default:    return null;
        }
        
        return aux;
    }
    
    private Token isKeyWord(String ac){   //A manera de TPC
        ac = ac.toUpperCase();
        
        if (aux == null)
            aux = new Token();
        
        for (int i=0; i < KEYWORD.length; i++){   //Verificar si ac es una keyword
           if (KEYWORD[i].equals(ac)){
              aux.setNom(i);
              return aux;
           }          
        }
        
        for (int i=0; i < TIPOstr.length; i++){   //Verificar si ac es un TIPO
           if (TIPOstr[i].equals(ac)){
              aux.set(Token.TIPO, TIPOatr[i]);
              return aux;
           }          
        }
        
        return null;    //ac no es una keyWord (palabra reservada) ni un TIPO.
    }
    
    private Token aux;
    
    private static final String KEYWORD[]={
        "","",
        "PROGRAM","VAR","PROCEDURE","BEGIN","END",
        "IF","THEN","ELSE","FOR","TO","DO","DOWNTO","WHILE","REPEAT","UNTIL",
        "READLN","WRITELN", "NOT","AND","OR", "DIV","MOD"
    };
    
    private static final String TIPOstr[]={"BOOLEAN", "INTEGER"};
    private static final int    TIPOatr[]={Token.BOOLEAN, Token.INTEGER};
}
